package com.example.doda.Screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doda.Adapter.Drawing_Adapter
import com.example.doda.CreateDrawing
import com.example.doda.DrawingInfo
import com.example.doda.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        val add_project = findViewById<ExtendedFloatingActionButton>(R.id.add_project)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = mutableListOf<DrawingInfo>()
        // This loop will create 20 Views containing
        // the image with the count of view
//        for (i in 1..20) {
//            data.add(DrawingInfo("url","4bhk Construction","flat",5))
//        }

        // This will pass the ArrayList to our Adapter
        val adapter = Drawing_Adapter(data,this)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        add_project.setOnClickListener {
            val intent = Intent(this, CreateDrawing::class.java)
            startActivity(intent)
        }

        val imagesRef = FirebaseDatabase.getInstance().reference.child("projects")
        imagesRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val imageData = dataSnapshot.getValue(DrawingInfo::class.java)
                    imageData?.let {
                        data.add(it)
                    }
                }

                // Update the RecyclerView adapter with the retrieved image data
                //adapter.imageList = imageList
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

}
