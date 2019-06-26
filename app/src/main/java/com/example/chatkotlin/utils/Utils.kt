package com.example.chatkotlin.utils

import android.content.Context
import android.util.Log
import com.example.chatkotlin.model.*
import com.example.chatkotlin.recyclerView.PersonItem
import com.example.chatkotlin.recyclerView.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import java.lang.NullPointerException

object Utils {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")


    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "", null, mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }

    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }


    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }


    fun addUsersListner(context: Context,onListen:(List<Item>) -> Unit):ListenerRegistration{
        return firestoreInstance.collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException != null){
                Log.e("error=>","user listner error")
                return@addSnapshotListener
            }
            val items = mutableListOf<Item>()
            querySnapshot!!.documents.forEach {
                if(it.id != FirebaseAuth.getInstance().currentUser?.uid)
                    items.add(PersonItem(it.toObject(User::class.java)!!,it.id,context))
            }
            onListen(items)

        }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannels(otherUserId:String,
                                onComplete: (channelId:String) -> Unit){

        currentUserDocRef.collection("engagedChatChannels").document(otherUserId).get().addOnSuccessListener {
            if(it.exists()){
                onComplete(it["channelId"] as String)
                return@addOnSuccessListener
            }
            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

            val newChannel = chatChannelsCollectionRef.document()
            newChannel.set(ChatChannel(mutableListOf(currentUserId,otherUserId)))

            currentUserDocRef.collection("engagedChatChannels").document(otherUserId).set(mapOf("channelId" to newChannel.id))

            firestoreInstance.collection("users").document(otherUserId)
                .collection("engagedChatChannels")
                .document(currentUserId)
                .set(mapOf("channelId" to newChannel.id))


            onComplete(newChannel.id)
        }

    }

    fun addChatMessageListener(userId:String,channelId:String,context:Context,onListen:(List<Item>) -> Unit):ListenerRegistration{

        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    Log.e("Error=>","chat message listener")
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    Log.e("Error=>${userId}","==> ${it["senderId"].toString()} ==> reciver ${it["recipientId"].toString()}")
                    if(it["type"] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!,context))
                    else{
                        //TODO
                    }


                   return@forEach

                }
                onListen(items)
            }
    }

    fun sendMessage(message:Message,channelId: String){

        chatChannelsCollectionRef.document(channelId).collection("messages").add(message)

    }

}