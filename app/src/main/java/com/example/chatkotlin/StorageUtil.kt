package com.example.chatkotlin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.NullPointerException
import java.util.*

object StorageUtil {
    val storageInstance:FirebaseStorage by lazy{
        FirebaseStorage.getInstance()
    }

    val currentUserRef:StorageReference get() = storageInstance.reference.child(FirebaseAuth.getInstance().uid ?: throw NullPointerException("Uid is Null"))

    fun uploadProfilePhoto(imageBytes:ByteArray,onSuccess:(imagePath:String)->Unit){
        val ref = currentUserRef.child("profilePicture/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes).addOnSuccessListener {
            onSuccess(ref.path)
        }

    }

    fun pathToReference(path:String) = storageInstance.getReference(path)
}