package com.example.chatkotlin.oldChatApp.model

data class User(val name:String,val bio:String,val profilePicturePath:String?){
    constructor():this("","",null)
}