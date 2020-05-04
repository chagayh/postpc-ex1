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

    fun addItem(item: Item) {
        _items.add(item)
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
        holder.text.text = item.text
        holder.img.setImageResource(R.drawable.check_box_empty_24dp)
        holder.itemView.setOnClickListener {
//            Toast.makeText(view, "alpha value = " + holder.img.imageAlpha, Toast.LENGTH_SHORT).show()
            if (!item.isDone) {     // isDone = false
                holder.img.setImageResource(R.drawable.ic_check_box_w_v_24dp)
                item.isDone = true
            }
        }
    }
}