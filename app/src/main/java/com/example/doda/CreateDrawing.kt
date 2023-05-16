package com.example.doda

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

import android.util.Base64.encodeToString
import androidx.core.net.toUri

class CreateDrawing : AppCompatActivity() {

    private var image_url : String = ""
    private var bitmap : Bitmap ?= null
    private lateinit var name : EditText
    private lateinit var desc : EditText
    private lateinit var add : TextView
    private lateinit var plus : ImageView
    private lateinit var prev_image : RelativeLayout
    private lateinit var curr_image : ImageView

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_drawing)

        name = findViewById(R.id.name_txt)
        desc = findViewById(R.id.desc_txt)
        add = findViewById(R.id.add_btn)

        plus = findViewById(R.id.addimg_btn)
        prev_image = findViewById(R.id.img_parent)
        curr_image = findViewById(R.id.cover_pic)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        plus.setOnClickListener {
            launchGallery()
        }

        add.setOnClickListener {
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val imageByteArray = stream.toByteArray()

            val DrawingInfo = DrawingInfo("",name.text.toString(),desc.text.toString())
            uploadData(imageByteArray, DrawingInfo)
        }









    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            image_url = data.data.toString()

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                curr_image.setImageBitmap(bitmap)


                curr_image.visibility = View.VISIBLE
                prev_image.visibility = View.GONE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadData(imageByteArray : ByteArray, drawingInfo: DrawingInfo){
        val database = FirebaseDatabase.getInstance()
        val storage = FirebaseStorage.getInstance()

        val storageRef: StorageReference = storage.reference
        val imageRef: StorageReference = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val databaseRef: DatabaseReference = database.reference.child("projects").child(name.text.toString())

        // Upload the image to Firebase Storage
        imageRef.putFile(image_url.toUri())
            .addOnSuccessListener {
                // Get the image download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Save the image data to Firebase Realtime Database
                    drawingInfo.pic = uri.toString()
                    databaseRef.setValue(drawingInfo)
                        .addOnSuccessListener {
                            // Image upload and data save are successful
                            // You can perform any additional actions here
                        }
                        .addOnFailureListener { exception ->
                            // Failed to save image data to Firebase Realtime Database
                            // Handle the error
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Failed to upload image to Firebase Storage
                // Handle the error
            }

    }

    private fun launchGallery() {
        Log.d("tag","click")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
}