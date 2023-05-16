package com.example.doda.Screens

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.doda.Marker_Info
import com.example.doda.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MarkerDialog(x : Int, y : Int, info : String, name : String) : DialogFragment() {

    var x = -1;
    var y = -1;
    var info = "";
    var name = "";

    init
    {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    private lateinit var txt : EditText
    private lateinit var upload : TextView

    // dialog view is created
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Objects.requireNonNull(dialog)?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        var v : View =  inflater.inflate(R.layout.drawing_specs,null,false)

        txt = v.findViewById(R.id.etComments)
        upload = v.findViewById(R.id.upload)

        upload.setOnClickListener {
            //Toast.makeText(context,name.toString() + "/n" + txt.text.toString(),Toast.LENGTH_SHORT).show()
            upload_mark(x,y,txt.text.toString(),name)
        }










        return v
    }

    private fun upload_mark(x : Int, y : Int, info : String, name : String)
    {
        val database = FirebaseDatabase.getInstance()
        val databaseRef: DatabaseReference = database.reference.child("projects").child(name.toString()).child("Markers").push()

        val marker_info = Marker_Info(x.toString(),y.toString(),info.toString())
        databaseRef.setValue(marker_info)
            .addOnSuccessListener {
                Toast.makeText(context,"Marker settled", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener { exception ->
                dismiss()
                // Failed to save image data to Firebase Realtime Database
                // Handle the error
            }
    }


    //dialog view is ready
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }



}
