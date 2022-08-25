package com.example.gallerydownload

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
    @SerializedName("largeImageURL") val fullUrl:String,
    @SerializedName("webformatHeight") val photoHeight:Int,
    @SerializedName("user") val photoUser:String,
    @SerializedName("likes") val photoLikes:Int,
    @SerializedName("comments") val photoComments:Int
):Parcelable


























