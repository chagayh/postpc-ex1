package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    private lateinit var button: Button
//    private lateinit var textView: TextView
//    private lateinit var editText: EditText
    private lateinit var adapter: ItemAdapter
    private lateinit var itemsList: MutableList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reverseLayout = false

        createItemsList(savedInstanceState)
        adapter = ItemAdapter()
        adapter.setItems(itemsList)
        items_recycler.adapter = adapter
        items_recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, reverseLayout)

        setComponents()
    }

    private fun createItemsList(savedInstanceState: Bundle?) {
        itemsList = mutableListOf<Item>()
        if (savedInstanceState != null) {
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
            editText.setText(savedInstanceState.getString("EditText text"))
            val savedItemsList = savedInstanceState.getStringArray("savedItemsList")
            val isDoneArray = savedInstanceState.getBooleanArray("isDoneArray")
            for (i in savedItemsList!!.indices) {
                if (isDoneArray != null) {
                    val item = Item(savedItemsList[i], isDoneArray[i])
                    itemsList.add(item)
                }
            }
        }
    }

    private fun setComponents() {
        button.setOnClickListener {
            if (editText.text.toString() == "") {
                Toast.makeText(this, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show()
            } else {
                val item = Item(editText.text.toString(), false)
                adapter.addItem(item)
                itemsList.add(item)
                editText.text.clear()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EditText text", editText.text.toString())
        val savedItemsList = arrayOfNulls<String>(itemsList.size)
        val isDoneArray = BooleanArray(itemsList.size)
        for (i in 0 until itemsList.size) {
            savedItemsList[i] = itemsList[i].text
            isDoneArray[i] = itemsList[i].isDone
        }
        outState.putStringArray("savedItemsList", savedItemsList)
        outState.putBooleanArray("isDoneArray", isDoneArray)
    }
}
