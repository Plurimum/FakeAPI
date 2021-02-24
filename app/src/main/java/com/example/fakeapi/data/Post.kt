package com.example.fakeapi.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    @field:Json(name = "userId")var userId: Int = 0,
    @field:Json(name = "id")var postId: Int = 0,
    @field:Json(name = "title")var postTitle: String = "",
    @field:Json(name = "body")var postBody: String = ""
) : Parcelable