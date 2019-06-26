package com.example.chatkotlin.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor():this(mutableListOf())
}