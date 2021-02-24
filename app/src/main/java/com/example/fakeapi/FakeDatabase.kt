package com.example.fakeapi

import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import com.example.fakeapi.data.Post
import com.example.fakeapi.interfaces.FakeDAO

@Database(entities = [Post::class], version = 1,exportSchema = true)
abstract class FakeDatabase : RoomDatabase() {
    abstract fun getFakeDao(): FakeDAO
}