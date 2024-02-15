package com.osmangyildiz.instagramclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osmangyildiz.instagramclone.databinding.RecyclerRowBinding
import com.osmangyildiz.instagramclone.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerEmailView.text = postList[position].email
        holder.binding.recyclerCommentView.text = postList[position].comment
        val imageUrl = postList[position].downloadUrl
        Picasso.get().load(imageUrl).into(holder.binding.recyclerImageView)

    }

    override fun getItemCount(): Int {
        return postList.size
    }

}