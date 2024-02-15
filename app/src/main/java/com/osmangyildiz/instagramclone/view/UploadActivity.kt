package com.osmangyildiz.instagramclone.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.osmangyildiz.instagramclone.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var permissionHandler: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedPicture: Uri? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore
        firebaseStorage = Firebase.storage
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let { picture ->
                            binding.imageView.setImageURI(picture)
                        }
                    }
                }
            }
        permissionHandler =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(this@UploadActivity, "Permission needed", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    fun selectImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@UploadActivity, Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give permission") {
                        permissionHandler.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
            } else {
                permissionHandler.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    fun shareButtonClicked(view: View) {
        val autoUUID = UUID.randomUUID()

        val reference = firebaseStorage.reference
        val imageReference = reference.child("images").child("$autoUUID")
        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                imageReference.downloadUrl.addOnSuccessListener {

                    val downloadUrl = it.toString()
                    val postMap = hashMapOf<String, Any>()
                    if (firebaseAuth.currentUser != null) {
                        postMap["downloadUrl"] = downloadUrl
                        postMap["userEmail"] = firebaseAuth.currentUser!!.email!!
                        postMap["comment"] = binding.commentText.text.toString()
                        postMap["date"] = Timestamp.now()

                        firebaseFirestore.collection("Posts").add(postMap).addOnSuccessListener {
                        finish()
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this@UploadActivity, exception.localizedMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }.addOnFailureListener(this@UploadActivity) { exception ->
                Toast.makeText(this@UploadActivity, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }

        }
    }

}