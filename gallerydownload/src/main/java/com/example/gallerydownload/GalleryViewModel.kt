package com.example.gallerydownload

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val keyWords =//定义不可变数组
        arrayOf("cat", "girl", "dog", "car", "beauty", "photo", "computer", "flower", "animal")
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()//这是可以修改的队列
    val photoListLive: LiveData<List<PhotoItem>>
        //这是只读属性
        get() {
            return _photoListLive
        }//这是设置get属性

    //使用Volley从网络获取数据
    fun fetchData() {
        val stringRequest = StringRequest(
            Request.Method.GET,//使用get方式获取数据
            getUrl(),
            //获取数据成功的监听
            Response.Listener {
                Log.d("lyy", "fetchData: ${it}")
                _photoListLive.value = Gson().fromJson(it, Pixabay::class.java).hits.toList()
            },
            //获取数据失败的监听
            Response.ErrorListener {
                Log.e("lyy", "fetchData: ", it)
            }
        )

        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=29437152-a33377ba40178c11d9ad842b6&q=${keyWords.random()}&per_page=100"
    }
}