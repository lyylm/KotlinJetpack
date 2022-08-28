package com.yett.galleryupdate2

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context){
    //半生类。类似Java里的静态变量，使用类名来调用
    companion object{
        //这是定义一个私有的变量，可以修改的变量
        private var INSTANCE:VolleySingleton?=null
        //定义一个函数，一个参数，没有返回值
        fun getInstance(context: Context)=
            //?:意思是当INSTANCE为空，则执行后面的语句；synchronized是线程锁，防止多线程冲突
            INSTANCE?: synchronized(this){
                //apply可以让操作简便
                VolleySingleton(context).apply { INSTANCE=this }
            }
    }
    //定义一个公共变量，但是是一个不可变的变量，在Kotlin中默认都是公开的，函数和变量，by lazy是懒加载，当被调用时才赋值
    val requestQueue:RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }
}