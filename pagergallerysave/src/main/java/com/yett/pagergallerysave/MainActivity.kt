package com.yett.pagergallerysave

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
        //在导航栏上显示返回箭头按钮
        val navController:NavController = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)?.findNavController() as NavController
        NavigationUI.setupActionBarWithNavController(this,navController)
    }

    //给返回箭头按钮添加点击事件
    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.fragmentContainerView).navigateUp()
    }
}