package com.osmangyildiz.instagramclone.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.osmangyildiz.instagramclone.R
import com.osmangyildiz.instagramclone.adapter.FeedRecyclerAdapter
import com.osmangyildiz.instagramclone.databinding.ActivityFeedBinding
import com.osmangyildiz.instagramclone.model.Post

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var feedAdapter: FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore
        postArrayList = ArrayList<Post>()

        getData()
        binding.recyclerView.layoutManager = LinearLayoutManager(this@FeedActivity)
        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter
    }

    private fun getData() {
        firebaseFirestore.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents
                        postArrayList.clear()
                        for (elements in documents) {
                            val comment = elements.get("comment") as String
                            val userEmail = elements.get("userEmail") as String
                            val downloadUrl = elements.get("downloadUrl") as String

                            val post = Post(comment, userEmail, downloadUrl)
                            postArrayList.add(post)

                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_post) {
            val intent = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.sign_out) {
            firebaseAuth.signOut()
            val intent = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}