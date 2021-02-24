package com.example.fakeapi.asynctasks

import android.os.AsyncTask
import android.widget.ProgressBar
import com.example.fakeapi.MainActivity
import com.example.fakeapi.application.FakeApp
import com.example.fakeapi.data.Post
import kotlinx.android.synthetic.main.activity_main.*

class DBInsertTask(private val activity: MainActivity) : AsyncTask<Post, Unit, Unit>() {

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        activity.apply {
            progressBar.visibility = ProgressBar.INVISIBLE
            update()
        }
    }

    override fun doInBackground(vararg params: Post?) {
        for (post in params)
            if (post != null) {
                FakeApp.instance.postDB.getFakeDao().insertPost(post)

            }
    }
}