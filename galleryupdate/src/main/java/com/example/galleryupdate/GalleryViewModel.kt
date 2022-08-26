package com.example.galleryupdate

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
    private val _keyWords = arrayOf("cat", "girl", "dog", "car", "beauty", "photo", "computer", "flower", "animal")
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive:LiveData<List<PhotoItem>>
        get() = _photoListLive

    private fun getUrl():String{
        return "https://pixabay.com/api/?key=29437152-a33377ba40178c11d9ad842b6&q=${_keyWords.random()}&per_page=100"
    }

    fun fetchData(){
        val stringRequest:StringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                _photoListLive.value = Gson().fromJson(it,Pixabay::class.java).hits.toList();
            },
            Response.ErrorListener {
                Log.e(TAG, "fetchData: ", it)
            }
        )

        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }
}