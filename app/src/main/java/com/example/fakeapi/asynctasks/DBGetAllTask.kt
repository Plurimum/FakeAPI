package com.example.fakeapi.asynctasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.ProgressBar
import com.example.fakeapi.MainActivity
import com.example.fakeapi.MainActivity.Companion.FIRST_LAUNCH_KEY
import com.example.fakeapi.MainActivity.Companion.MAIN_PREFS_NAME
import com.example.fakeapi.R
import com.example.fakeapi.application.FakeApp
import com.example.fakeapi.data.Post
import kotlinx.android.synthetic.main.activity_main.*

class DBGetAllTask(private val activity: MainActivity) : AsyncTask<Unit, Unit, List<Post>>() {

    override fun onPostExecute(result: List<Post>?) {
        super.onPostExecute(result)
        if (!result.isNullOrEmpty()) {
            activity.apply {
                postList.clear()
                postList.addAll(result)
                update()
                showAlert(activity.resources.getString(R.string.bd_posts))
            }
        } else {
            if (!activity.getSharedPreferences(MAIN_PREFS_NAME, Context.MODE_PRIVATE)
                    .contains(FIRST_LAUNCH_KEY)
            ) {
                activity.getSharedPreferences(MAIN_PREFS_NAME, Context.MODE_PRIVATE).edit()
                    .putBoolean(
                        FIRST_LAUNCH_KEY, true
                    ).apply()
                activity.getAllPosts()
            } else {
                activity.showAlert(activity.resources.getString(R.string.bd_empty))
                activity.progressBar.visibility = ProgressBar.INVISIBLE
            }
        }
        activity.update()
    }

    override fun doInBackground(vararg params: Unit?): List<Post>? {
        return FakeApp.instance.postDB.getFakeDao().getAll()
    }

}