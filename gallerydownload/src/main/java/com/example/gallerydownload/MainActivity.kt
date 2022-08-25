package com.example.gallerydownload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //导航栏显示返回箭头
        val navController: NavController = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)?.findNavController() as NavController
        NavigationUI.setupActionBarWithNavController(this,navController)
    }
    //给返回箭头添加事件
    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.fragmentContainerView).navigateUp()
    }
}