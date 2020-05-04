package com.example.myapplication

import android.widget.ImageView

data class Item (
    val text: String,
    val img: Int = R.drawable.check_box_empty_24dp
)

fun createItems(): List<Item> {
    val list = mutableListOf<Item>()

    return list
}