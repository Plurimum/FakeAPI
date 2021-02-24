package com.example.fakeapi.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fakeapi.R
import com.example.fakeapi.adapters.PostAdapter
import com.example.fakeapi.application.FakeApp
import com.example.fakeapi.asynctasks.DBClearTask
import com.example.fakeapi.asynctasks.DBDeleteTask
import com.example.fakeapi.asynctasks.DBGetAllTask
import com.example.fakeapi.asynctasks.DBInsertTask
import com.example.fakeapi.data.Post
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        const val NEW_POST_KEY = "NEW_POST_KEY"
        const val POST_LIST_KEY = "POST_LIST_KEY"
        const val CUR_USER_ID = 1
        const val NEW_POST_REQUEST_CODE = 1
        const val NEW_POST_RESULT_CODE = 2
        const val QUERY_POST = "POST"
        const val QUERY_DELETE = "DELETE"
        const val DATABASE_NAME = "post_database"
        const val TABLE_NAME = "fake_posts"
        const val FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY"
        const val MAIN_PREFS_NAME = "MAIN_PREFS_NAME"
    }

    var postList: ArrayList<Post> = arrayListOf()

    private lateinit var listAdapter: PostAdapter

    private var getAllTask: DBGetAllTask? = null
    private var insertTask: DBInsertTask? = null
    private var deleteAllTask: DBClearTask? = null
    private var deleteTask: DBDeleteTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (savedInstanceState != null) {
            postList =
                savedInstanceState.getParcelableArrayList<Post>(POST_LIST_KEY) as ArrayList<Post>
        }
        if (postList.isNullOrEmpty()) {
            getAllPostsFromBD()
        }

        listAdapter = PostAdapter(postList) {
            deletePost(it)
        }
        main_recycler.apply {
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

    fun getAllPosts() {
        progressBar.visibility = ProgressBar.VISIBLE
        FakeApp.instance.fakeAPIService.getAllPosts().enqueue(PostListCallback())
    }

    private fun postNewPost(data: Post) {
        FakeApp.instance.fakeAPIService.loadNewPost(data).enqueue(PostCallback(QUERY_POST))
    }

    private fun deletePost(post: Post) {
        FakeApp.instance.fakeAPIService.deletePostById(post.userId)
            .enqueue(PostCallback(QUERY_DELETE, post))
    }

    private fun getAllPostsFromBD() {
        getAllTask?.cancel(true)
        getAllTask = DBGetAllTask(this)
        getAllTask?.execute()
    }

    private fun insertPostInDatabase(vararg post: Post) {
        progressBar.visibility = ProgressBar.VISIBLE
        insertTask?.cancel(true)
        insertTask = DBInsertTask(this)
        insertTask?.execute(*post)
    }

    private fun reloadAPI() {
        progressBar.visibility = ProgressBar.VISIBLE
        deleteAllTask?.cancel(true)
        deleteAllTask = DBClearTask(this)
        deleteAllTask?.execute()
    }

    private fun deletePostFromDB(post: Post) {
        progressBar.visibility = ProgressBar.VISIBLE
        deleteTask?.cancel(true)
        deleteTask = DBDeleteTask(this)
        deleteTask?.execute(post)
    }

    fun update() {
        main_recycler.recycledViewPool.clear()
        listAdapter.notifyDataSetChanged()
    }

    fun showAlert(msg: String) {
        progressBar.visibility = ProgressBar.INVISIBLE
        Snackbar.make(main_layout, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(POST_LIST_KEY, postList)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        postList = savedInstanceState.getParcelableArrayList<Post>(POST_LIST_KEY) as ArrayList<Post>
        progressBar.visibility = ProgressBar.INVISIBLE
        update()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> when (resultCode) {
                2 -> {
                    val newPost: Post = (data?.getParcelableExtra(NEW_POST_KEY) ?: return) ?: return
                    postNewPost(newPost)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        getAllPosts()
        return true
    }

    inner class PostListCallback : Callback<List<Post>> {
        override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
            if (response.body() == null) {
                showAlert("${resources.getString(R.string.bad_connection)} \n${response.code()}")
                return
            }
            reloadAPI()
            val result = response.body()
            postList.clear()
            postList.addAll(result as ArrayList)
            insertPostInDatabase(*postList.toTypedArray())
            update()
            showAlert("${resources.getString(R.string.new_posts)} \n${response.code()}")
        }

        override fun onFailure(call: Call<List<Post>>, t: Throwable) {
            showAlert(
                "${resources.getString(R.string.fail)}\n${t.localizedMessage ?: resources.getString(
                    R.string.bad_connection
                )}"
            )
            update()
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
                    result?.postId = postList.size * 2 + 1
                    postList.add(result!!)
                    insertPostInDatabase(result)
                    showAlert("${resources.getString(R.string.new_posts)} \n${response.code()}")
                }
                QUERY_DELETE -> {
                    if (postList.contains(post)) {
                        postList.remove(post)
                        deletePostFromDB(post)
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
            when (type) {
                QUERY_POST -> {
                    post.postId = postList.size * 2 + 1
                    postList.add(post)
                    insertPostInDatabase(post)
                }
                QUERY_DELETE -> {
                    if (postList.contains(post)) {
                        postList.remove(post)
                        deletePostFromDB(post)
                    }
                }
            }
            update()
        }

    }
}
