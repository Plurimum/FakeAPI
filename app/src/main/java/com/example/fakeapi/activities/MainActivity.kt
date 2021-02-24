package com.example.fakeapi

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fakeapi.activities.AddPostActivity
import com.example.fakeapi.adapters.PostAdapter
import com.example.fakeapi.application.FakeApp
import com.example.fakeapi.data.Post
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.collections.ArrayList


const val NEW_POST_KEY = "NEW_POST_KEY"
const val POST_LIST_KEY = "POST_LIST_KEY"
const val CUR_USER_ID = 1
const val NEW_POST_REQUEST_CODE = 1
const val NEW_POST_RESULT_CODE = 2
const val QUERY_POST = "POST"
const val QUERY_DELETE = "DELETE"

class MainActivity : AppCompatActivity() {

    private var postList: ArrayList<Post> = arrayListOf()

    private lateinit var listAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            postList =
                savedInstanceState.getParcelableArrayList<Post>(POST_LIST_KEY) as ArrayList<Post>
        }
        if (postList.isNullOrEmpty()) getPostsByUserId()
        else {
            progressBar.visibility = ProgressBar.INVISIBLE
        }

        listAdapter = PostAdapter(postList) {
            deletePost(it)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter
        }
        update()
        add_button.setOnClickListener {
            startActivityForResult(
                Intent(this@MainActivity, AddPostActivity::class.java),
                NEW_POST_REQUEST_CODE
            )
        }
    }

    private fun getPostsByUserId() {
        FakeApp.instance.fakeAPIService.getPostByUserId(CUR_USER_ID).enqueue(PostListCallback())
    }

    private fun postNewPost(data: Post) {
        FakeApp.instance.fakeAPIService.loadNewPost(data).enqueue(PostCallback(QUERY_POST))
    }

    private fun deletePost(post: Post) {
        FakeApp.instance.fakeAPIService.deletePostById(post.userId)
            .enqueue(PostCallback(QUERY_DELETE, post))
    }

    private fun update() {
        listAdapter.notifyDataSetChanged()
    }

    private fun showAlert(msg: String) {
        progressBar.visibility = ProgressBar.INVISIBLE
        AlertDialog.Builder(this).apply {
            setMessage(msg)
            setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(POST_LIST_KEY, postList)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        postList = savedInstanceState.getParcelableArrayList<Post>(POST_LIST_KEY) as ArrayList<Post>
        update()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> when (resultCode) {
                2 -> {
                    val newPost: Post = (data?.getParcelableExtra(NEW_POST_KEY) ?: return) ?: return
                    Log.i("new one", newPost.toString())
                    postNewPost(newPost)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    inner class PostListCallback : Callback<List<Post>> {
        override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
            if (response.body() == null) {
                showAlert("${resources.getString(R.string.bad_connection)} \n${response.code()}")
                return
            }
            val result = response.body()
            postList.clear()
            postList.addAll(result as ArrayList)
            listAdapter.notifyDataSetChanged()
            showAlert("${resources.getString(R.string.new_posts)} \n${response.code()}")
        }

        override fun onFailure(call: Call<List<Post>>, t: Throwable) {
            showAlert(
                "${resources.getString(R.string.fail)}\n${t.localizedMessage ?: resources.getString(
                    R.string.bad_connection
                )}"
            )
        }
    }

    inner class PostCallback(private val type: String, private val post: Post = Post()) :
        Callback<Post> {

        override fun onResponse(call: Call<Post>, response: Response<Post>) {
            if (response.body() == null) {
                showAlert("${resources.getString(R.string.bad_connection)} \n${response.code()}")
                return
            }
            val result = response.body()
            when (type) {
                QUERY_POST -> {
                    postList.add(result!!)
                    showAlert("${resources.getString(R.string.new_posts)} \n${response.code()}")
                }
                QUERY_DELETE -> {
                    if (postList.contains(post)) {
                        postList.remove(post)
                        showAlert("${resources.getString(R.string.del_posts)} \n${response.code()}")
                    }
                }
            }
            update()
        }

        override fun onFailure(call: Call<Post>, t: Throwable) {
            showAlert(
                "${resources.getString(R.string.fail)}\n${t.localizedMessage ?: resources.getString(
                    R.string.bad_connection
                )}"
            )
        }

    }
}
