package com.example.kotlingallery

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Pixabay(
    val totalHits:Int,
    val total:Int,
    val hits:Array<PhotoItem>
)

@Parcelize data class PhotoItem(
    @SerializedName("webformatURL") val previewUrl:String,
    @SerializedName("id") val photoId:Int,
    @SerializedName("largeImageURL") val fullUrl:String
):Parcelable//在模块的gradle中添加一个属性就不要重写函数了
//androidExtensions{
//    experimental = true
//}
