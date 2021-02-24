package com.example.fakeapi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeapi.R
import com.example.fakeapi.data.Post
import kotlinx.android.synthetic.main.post_item.view.*

class PostAdapter(
    private val postList: List<Post>,
    private val onClick: ((Post) -> Unit)
) : RecyclerView.Adapter<PostAdapter.PostHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val holder = PostHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_item,
                parent,
                false
            )
        )
        holder.root.delete_button.setOnClickListener {
            onClick(postList[holder.adapterPosition])
        }
        return holder
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) =
        holder.bind(postList[position])

    override fun getItemCount() = postList.size

    inner class PostHolder(val root: View) : RecyclerView.ViewHolder(root) {
        fun bind(post: Post) {
            with(root) {
                title.text = post.postTitle
                body.text = post.postBody
            }
        }
    }

}