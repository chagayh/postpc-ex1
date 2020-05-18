package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ItemAdapter
    private lateinit var itemsList: ArrayList<Item>
    private lateinit var appContext: TodoApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext as TodoApp  // Application context

        val reverseLayout = false
        createItemsList(savedInstanceState)
        adapter = ItemAdapter()
        adapter.setItems(itemsList)
        items_recycler.adapter = adapter
        items_recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, reverseLayout)
        setComponents()
    }

    private fun createItemsList(savedInstanceState: Bundle?) {
        itemsList = ArrayList<Item>()
        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString("EditText text"))
            val savedItemsList = savedInstanceState.getStringArray("savedItemsList")
            val isDoneArray = savedInstanceState.getBooleanArray("isDoneArray")
            for (i in savedItemsList!!.indices) {
                if (isDoneArray != null) {
                    val item = Item(savedItemsList[i], isDoneArray[i])
                    itemsList.add(item)
                }
            }
            appContext.todoListManager.setItemsList(itemsList)
            appContext.todoListManager.storeItemsList()
        }
    }

    private fun setComponents() {
        button.setOnClickListener {
            if (editText.text.toString() == "") {
                Toast.makeText(appContext, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show()
            } else {
                val item = Item(editText.text.toString(), false)
                itemsList.add(item)
                adapter.setItems(itemsList)
                appContext.todoListManager.setItemsList(itemsList)
                appContext.todoListManager.storeItemsList()
                editText.text.clear()
            }
        }

        adapter.itemClickListener = (object : ItemClickListener {
            override fun onItemClicked(item: Item) {
                if (!item.isDone) {
                    val msg = String.format("TODO %s is now DONE. BOOM!", item.text)
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    item.isDone = true
                    adapter.setItems(itemsList)
                    appContext.todoListManager.setItemsList(itemsList)
                    appContext.todoListManager.storeItemsList()
                }
            }
        })
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
