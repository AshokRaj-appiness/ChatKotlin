package com.example.chatkotlin.oldChatApp

import com.example.chatkotlin.oldChatApp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.NullPointerException

object Utils {
    private val firebaseFirestoreInstance:FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }
    private val documentReference:DocumentReference get() = firebaseFirestoreInstance.document("users/${FirebaseAuth.getInstance().uid ?: throw NullPointerException("Uid is Null")}")

    fun initializeUserIfFirstTime(onComplete:()->Unit){
        documentReference.get().addOnSuccessListener { documentSnapshot->
            if(!documentSnapshot.exists()){
                val newUser = User(
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "", "", null
                )
                documentReference.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }else
                onComplete()
        }
    }

    fun updateCurrentUser(name:String ="",bio:String ="",profilePicturepath:String?=null){
        val userFieldMap = mutableMapOf<String,Any>()
        if(name.isNotBlank()) userFieldMap["name"]=name
        if(bio.isNotBlank()) userFieldMap["bio"]=bio
        if(profilePicturepath !=null)
            userFieldMap["profilePicturepath"] = profilePicturepath
        documentReference.update(userFieldMap)
    }
    fun getCurrentUser(onComplete: (User) -> Unit){
        documentReference.get().addOnSuccessListener {
            it.toObject(User::class.java)?.let { it1 -> onComplete(it1) }
        }
    }
}