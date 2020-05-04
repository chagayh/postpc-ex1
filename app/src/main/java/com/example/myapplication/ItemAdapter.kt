package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter: RecyclerView.Adapter<ItemHolder>() {
    private val _items: MutableList<Item> = ArrayList()

    fun setItems(items: List<Item>) {
        _items.clear()
        _items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
                        .inflate(R.layout.item_one_todo, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = _items[position]
        holder.text.setText(item.text)
        holder.img.setImageResource(TODO("enter the check box image"))
    }

}