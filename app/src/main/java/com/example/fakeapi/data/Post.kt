package com.example.fakeapi.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fakeapi.MainActivity.Companion.TABLE_NAME
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_NAME)
data class Post(
    @ColumnInfo(name = "user_id") @field:Json(name = "userId") var userId: Int = 0,
    @PrimaryKey @field:Json(name = "id") var postId: Int = 0,
    @ColumnInfo(name = "title_text") @field:Json(name = "title") var postTitle: String = "",
    @ColumnInfo(name = "body_text") @field:Json(name = "body") var postBody: String = ""
) : Parcelable
