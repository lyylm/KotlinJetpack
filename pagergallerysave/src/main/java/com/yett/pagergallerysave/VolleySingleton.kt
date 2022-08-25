package com.yett.pagergallerysave

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

//单例实现，构造函数要私有化
class VolleySingleton private constructor(context: Context){
    //类似于Java中的静态变量和静态函数，这里叫伴生对象
    companion object{
        private var INSTANCE:VolleySingleton?=null
        fun getInstance(context: Context)=
            //加一个线程锁，防止多线程冲突
            INSTANCE?: synchronized(this){
                VolleySingleton(context).also { INSTANCE=it }
            }
    }
    //这里是懒加载，当使用时才进行初始化
    val requestQueue:RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }
}