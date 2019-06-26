package com.example.chatkotlin.oldChatApp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.chatkotlin.R
import com.example.chatkotlin.oldChatApp.SignInActivity
import com.example.chatkotlin.oldChatApp.StorageUtil
import com.example.chatkotlin.oldChatApp.Utils
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import kotlinx.android.synthetic.main.fragment_my_account.view.editText_bio
import kotlinx.android.synthetic.main.fragment_my_account.view.editText_name
//import kotlinx.android.synthetic.main.fragment_my_account.* 1
import java.io.ByteArrayOutputStream

class MyAccount : Fragment() {
    val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes:ByteArray
    private var pictureJustChanged = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        view.apply {
            imageView_profile_picture.setOnClickListener{
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"Select Image"),RC_SELECT_IMAGE)
            }

            btn_save.setOnClickListener {
                if(::selectedImageBytes.isInitialized){
                    StorageUtil.uploadProfilePhoto(selectedImageBytes){
                        Utils.updateCurrentUser(editText_name.text.toString(),editText_bio.text.toString(),it)
                    }
                }else{
                    Utils.updateCurrentUser(editText_name.text.toString(),editText_bio.text.toString(),null)
                }
            }

            btn_sign_out.setOnClickListener {
                AuthUI.getInstance().signOut(this@MyAccount.context!!).addOnCompleteListener {
                    val intent = Intent(requireActivity(), SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Utils.getCurrentUser {
            if(this@MyAccount.isVisible){
                editText_name.setText(it.name)
                editText_bio.setText(it.bio)
//                if(!pictureJustChanged && pictureJustChanged != null){
//
//                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data?.data != null){

            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            selectedImageBytes = outputStream.toByteArray()

            pictureJustChanged = false

        }
    }


}
