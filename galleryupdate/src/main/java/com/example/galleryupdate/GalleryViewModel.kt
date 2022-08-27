package com.example.galleryupdate

import android.app.Application
import android.telephony.CellIdentity
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.math.ceil

const val TAG = "lyy"
const val DATA_STATUS_CAN_LOAD_MORE = 0
const val DATA_STATUS_NO_MORE = 1
const val DATA_STATUS_NETWORK_ERROR = 2

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _dataStatusLive = MutableLiveData<Int>()
    private var perPage = 100
    private val _keyWords =//
        arrayOf("cat", "dog", "car", "photo", "computer", "animal","girl", "beauty", "flower")
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive
    val dataStatusLive:LiveData<Int>
        get() {
            return _dataStatusLive
        }

    //在刷新完数据后，是不是需要跳转到顶部
    var needToScrollToTop = true
    private var currentPage = 1
    private var totalPage = 1
    private var currentKey = "cat"
    private var isNewQuery = true
    private var isLoading = false

    init {
        resetQuery()//当创建对象的时候刷新数据
    }

    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=29437152-a33377ba40178c11d9ad842b6&q=${currentKey}&per_page=${perPage}&page=${currentPage}"
    }

    fun resetQuery() {
        currentKey = _keyWords.random()
        currentPage = 1
        totalPage = 1
        isNewQuery = true
        needToScrollToTop = true
        fetchData()
    }

    fun fetchData() {
        if (isLoading) return//如果当前正在加载，则新的加载不处理

        if (currentPage > totalPage) {
            _dataStatusLive.value = DATA_STATUS_NO_MORE
            return//如果当前的页是最后的页数，则返回
        }
        isLoading = true//将正在加载的标志位置true
        val stringRequest: StringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                //_photoListLive.value = Gson().fromJson(it,Pixabay::class.java).hits.toList();
                with(Gson().fromJson(it, Pixabay::class.java)) {
                    //根据返回的数据总数除以每页的数据数量
                    totalPage = ceil(totalHits.toDouble() / perPage).toInt()
                    if (isNewQuery) {
                        _photoListLive.value = hits.toList()
                    } else {
                        //新旧数据合并
                        _photoListLive.value =
                            arrayListOf(_photoListLive.value!!, hits.toList()).flatten()
                    }
                }
                _dataStatusLive.value = DATA_STATUS_CAN_LOAD_MORE
                isLoading = false
                isNewQuery = false
                currentPage++
            },
            Response.ErrorListener {
                Log.e(TAG, "fetchData: ", it)
                isLoading = false
                _dataStatusLive.value = DATA_STATUS_NETWORK_ERROR
            }
        )

        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }
}





















