package com.example.chatkotlin.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatkotlin.AppConstants
import com.example.chatkotlin.ChatActivity

import com.example.chatkotlin.R
import com.example.chatkotlin.recyclerView.PersonItem
import com.example.chatkotlin.utils.Utils
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_peoples.*
import kotlinx.android.synthetic.main.fragment_peoples.view.*


class Peoples : Fragment() {
    private lateinit var userListenerRegistration:ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var peopleSection: Section
    private lateinit var recycler_view_people: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_peoples, container, false)
        recycler_view_people =  view.findViewById(R.id.recycler_view_people) as RecyclerView
        userListenerRegistration = Utils.addUsersListner(this.activity!!,::updateRecyclerView)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>){
        fun init(){
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@Peoples.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if(shouldInitRecyclerView)
            init()
        else
            updateItems()
    }
    private val onItemClick = OnItemClickListener{item, view ->
        if(item is PersonItem){
           var intent = Intent(requireActivity(),ChatActivity::class.java)
            intent.putExtra(AppConstants.USER_NAME,item.person.name)
            intent.putExtra(AppConstants.USER_ID,item.userId)
            startActivity(intent)

        }

    }


}
