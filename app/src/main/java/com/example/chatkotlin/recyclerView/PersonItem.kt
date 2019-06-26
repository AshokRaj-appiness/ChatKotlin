package com.example.chatkotlin.recyclerView

import android.content.Context
import com.example.chatkotlin.R
import com.example.chatkotlin.model.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*


class PersonItem(val person: User,val userId:String,private val context: Context) : Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
    }

    override fun getLayout() = R.layout.item_person

}