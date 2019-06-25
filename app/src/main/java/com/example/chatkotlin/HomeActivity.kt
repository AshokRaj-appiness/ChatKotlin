package com.example.chatkotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.chatkotlin.fragments.MyAccount
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_peoples -> {
                    //TODO
                    true
                }
                R.id.navigation_my_account -> {
                    replaceFragments(MyAccount())
                    true
                }
                else -> false

            }
        }


    }

    fun replaceFragments(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_layout,fragment)
            commit()
        }
    }
}
