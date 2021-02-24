package com.example.fakeapi.application

import android.app.Application
import com.example.fakeapi.interfaces.FakeAPI
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class FakeApp : Application() {

    lateinit var fakeAPIService: FakeAPI

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
    }

}