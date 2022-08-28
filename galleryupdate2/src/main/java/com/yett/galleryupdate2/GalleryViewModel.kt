package com.yett.galleryupdate2

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

//定义一个常量，项目的所有类都可以访问
const val TAG = "lyy"
class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    //私有数组，而且是不可变的
    private val _keyWords =//
        arrayOf("cat", "dog", "car", "photo", "computer", "animal","girl", "beauty", "flower")
    //私有的可变的LiveData列表，这是私有列表
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    //这是共有变量，该变量是不可修改的，即只读的
    val photoListLive:LiveData<List<PhotoItem>>
    //变量的get方法
        get() {
            return _photoListLive
        }
    //私有函数，返回值是字符串
    private fun getUrl():String{
        return "https://pixabay.com/api/?key=29437152-a33377ba40178c11d9ad842b6&q=${_keyWords.random()}&per_page=${100}"
    }

    fun ftechData(){
        val stringRequest:StringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                _photoListLive.value = Gson().fromJson(it,Pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.e(TAG, "ftechData: ", it)
            }
        )
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }
}