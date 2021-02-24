package com.example.fakeapi.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fakeapi.*
import com.example.fakeapi.MainActivity.Companion.CUR_USER_ID
import com.example.fakeapi.MainActivity.Companion.NEW_POST_KEY
import com.example.fakeapi.MainActivity.Companion.NEW_POST_RESULT_CODE
import com.example.fakeapi.data.Post
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlin.math.absoluteValue
import kotlin.random.Random

class AddPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        post_button.setOnClickListener {
            if (!enter_title.text.isNullOrBlank() && !enter_body.text.isNullOrBlank()) {

                val newId: Int = Random.nextInt().absoluteValue
                val newTitle: String = enter_title.text.toString()
                val newBody: String = enter_body.text.toString()

                val newPost = Post(CUR_USER_ID, newId, newTitle, newBody)
                setResult(
                    NEW_POST_RESULT_CODE,
                    Intent(this@AddPostActivity, MainActivity::class.java).apply {
                        putExtra(NEW_POST_KEY, newPost)
                    })
                finish()
            }
        }
    }
}