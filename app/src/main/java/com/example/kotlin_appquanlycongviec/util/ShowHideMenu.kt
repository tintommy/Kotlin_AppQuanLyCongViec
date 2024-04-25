package com.example.kotlin_appquanlycongviec.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.activity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView





        fun Fragment.hideBottomNavigation() {
            val bottomNavigation =
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomMenu)

            bottomNavigation.visibility = View.GONE

        }

        fun Fragment.showBottomNavigation() {
            val bottomNavigation =
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomMenu)

            bottomNavigation.visibility = View.VISIBLE



}

