package com.yett.pagergallerysave

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

const val TAG = "lyy"
class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _keyWords=//定义不可变数组
        arrayOf("cat", "girl", "dog", "car", "beauty", "photo", "computer", "flower", "animal")
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()//可变的LiveData列表
    val photoListLive:LiveData<List<PhotoItem>>//不可变的列表，只读属性
        get() {
            return _photoListLive
        }

    fun fetchData(){
        //数据下载的请求数据准备
        val stringRequest:StringRequest= StringRequest(
            Request.Method.GET,//Get方式获取数据
            getUrl(),//下载地址
            Response.Listener {
                _photoListLive.value = Gson().fromJson(it,Pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.e(TAG, "fetchData: ", it)
            }
        )
        //将请求放入Volley请求队列，进行数据下载
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    //没有输入但是返回字符串的函数，内部函数
    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=29437152-a33377ba40178c11d9ad842b6&q=${_keyWords.random()}&per_page=100"
    }
}