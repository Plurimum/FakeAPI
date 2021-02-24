package com.example.fakeapi.application

import android.app.Application
import androidx.room.Room
import com.example.fakeapi.FakeDatabase
import com.example.fakeapi.activities.MainActivity.Companion.DATABASE_NAME
import com.example.fakeapi.interfaces.FakeAPI
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class FakeApp : Application() {

    lateinit var fakeAPIService: FakeAPI
    lateinit var postDB: FakeDatabase

    companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com"

        lateinit var instance: FakeApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        fakeAPIService = mRetrofit.create()
        postDB = Room.databaseBuilder(applicationContext, FakeDatabase::class.java, DATABASE_NAME)
            .build()
    }

}