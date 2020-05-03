package com.example.myapplication

import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ItemHolder(view: View): RecyclerView.ViewHolder(view){
    val text: TextView = view.findViewById(R.id.todo_text)
    val img: ImageView = view.findViewById(R.id.check_box_img)
}