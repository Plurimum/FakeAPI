package com.example.fakeapi.asynctasks

import android.os.AsyncTask
import android.widget.ProgressBar
import com.example.fakeapi.MainActivity
import com.example.fakeapi.application.FakeApp
import kotlinx.android.synthetic.main.activity_main.*

class DBClearTask(private val activity: MainActivity) : AsyncTask<Unit, Unit, Unit>() {

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        activity.apply {
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    override fun doInBackground(vararg params: Unit?) {
        FakeApp.instance.postDB.getFakeDao().deleteAll()
    }
}