package com.example.fakeapi.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fakeapi.activities.MainActivity.Companion.TABLE_NAME
import com.example.fakeapi.data.Post

@Dao
interface FakeDAO {

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): List<Post>

    @Insert
    fun insertPost(post: Post)

    @Delete
    fun deletePost(post: Post)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()
}