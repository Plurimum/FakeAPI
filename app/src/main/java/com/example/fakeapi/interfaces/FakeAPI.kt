package com.example.fakeapi.interfaces

import com.example.fakeapi.data.Post
import retrofit2.Call
import retrofit2.http.*

interface FakeAPI {

    @GET("posts/")
    fun getAllPosts(
    ): Call<List<Post>>

    @POST("posts/")
    fun loadNewPost(
        @Body data: Post
    ): Call<Post>

    @DELETE("posts/{id}")
    fun deletePostById(
        @Path("id") id: Int
    ): Call<Post>

}