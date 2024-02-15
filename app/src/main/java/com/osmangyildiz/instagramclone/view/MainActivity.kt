package com.osmangyildiz.instagramclone.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.osmangyildiz.instagramclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = Firebase.auth
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val intent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signInClicked(view: View) {
        val emailInfo = binding.emailText.text.toString().trim()
        val passwordInfo = binding.passwordText.text.toString().trim()

        if (emailInfo.isNotEmpty() && passwordInfo.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailInfo, passwordInfo).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "E-mail or password field cannot be empty", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun signUpClicked(view: View) {
        val emailInfo = binding.emailText.text.toString().trim()
        val passwordInfo = binding.passwordText.text.toString().trim()

        if (emailInfo.isNotEmpty() && passwordInfo.isNotEmpty()) {

            firebaseAuth.createUserWithEmailAndPassword(emailInfo, passwordInfo)
                .addOnSuccessListener {
                    val intent = Intent(
                        this@MainActivity, FeedActivity::class.java
                    )
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                }

        } else {
            Toast.makeText(this, "E-mail or password field cannot be empty", Toast.LENGTH_LONG)
                .show()
        }

    }
}