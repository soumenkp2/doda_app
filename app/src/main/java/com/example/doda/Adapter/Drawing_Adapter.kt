package com.example.doda.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doda.DrawingInfo
import com.example.doda.R
import com.example.doda.Screens.MainActivity
import com.squareup.picasso.Picasso


class Drawing_Adapter(private val mList: List<DrawingInfo>, context : Context) : RecyclerView.Adapter<Drawing_Adapter.ViewHolder>() {

    private lateinit var context: Context;

    init
    {
        this.context = context;
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.drawing_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        //holder.pic.setImageResource(ItemsViewModel.pic?.let { context?.resources?.getDrawable(it) ?: (90) } as Int)

//        Glide.with(holder.pic.context)
//            .load(ItemsViewModel.pic.toString())
//            .into(holder.pic)

        Picasso.with(holder.itemView.context).load(ItemsViewModel.pic.toString()).into(holder.pic)

        //Toast.makeText(context,ItemsViewModel.pic,Toast.LENGTH_SHORT).show()

        holder.name.text = ItemsViewModel.name.toString()
        holder.board.setOnClickListener(View.OnClickListener {

            val b = Bundle()
            b.putString("name",ItemsViewModel.name.toString())
            b.putString("uri",ItemsViewModel.pic.toString())

            val intent = Intent(context, com.example.doda.Screens.drawing::class.java)
            intent.putExtras(b)
            context.startActivity(intent)

        })

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val pic: ImageView = itemView.findViewById(R.id.drawing_image)
        val name: TextView = itemView.findViewById(R.id.drawing_name)
        val desc: TextView = itemView.findViewById(R.id.view_desc)
        val board :TextView = itemView.findViewById(R.id.view_board_btn)
    }
}