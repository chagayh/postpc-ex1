package com.example.myapplication

import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ItemAdapter
    private lateinit var itemsList: ArrayList<Item>
    private lateinit var appContext: TodoApp

    companion object {
        private const val ADD_KEY = "add"
        private const val REMOVE_KEY = "remove"
        private const val COMPLETE_KEY = "complete"
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        itemsList = appContext.todoListManagerDB.getItemsList()
        editText.setText(savedInstanceState!!.getString("EditText text"))
//        if (savedInstanceState != null) {
//            editText.setText(savedInstanceState.getString("EditText text"))
//            val savedItemsList = savedInstanceState.getStringArray("savedItemsList")
//            val isDoneArray = savedInstanceState.getBooleanArray("isDoneArray")
//            for (i in savedItemsList!!.indices) {
//                if (isDoneArray != null) {
//                    val item = Item(savedItemsList[i], isDoneArray[i], )
//                    itemsList.add(item)
//                }
//            }
//            appContext.todoListManager.setItemsList(itemsList)
//            appContext.todoListManager.storeItemsList()
//        }
    }

    private fun markComplete(item: Item, done: Boolean) {
        val newItem = Item(item.text, done, item.timeStamp, item.lastModified, item.firestoreDocumentId)
        appContext.todoListManagerDB.editItem(item, newItem)
    }

    private fun updateItemsList(action : String, item : Item, done: Boolean) {
        when (action) {
            ADD_KEY -> appContext.todoListManagerDB.addItem(item)
            REMOVE_KEY -> appContext.todoListManagerDB.deleteItem(item)
            COMPLETE_KEY -> markComplete(item, done)
        }
        itemsList = appContext.todoListManagerDB.getItemsList() // TODO - check how long takes to complete
        adapter.setItems(itemsList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setComponents() {
        button.setOnClickListener {
            if (editText.text.toString() == "") {
                Toast.makeText(appContext, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show()
            } else {
                val item = Item(editText.text.toString(), false, DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                updateItemsList(ADD_KEY, item)
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

        adapter.itemLongClickListener = (object : ItemLongClickListener {
            override fun onLongItemClicked(item: Item) {
                val builder = AlertDialog.Builder(this@MainActivity)
                with(builder)
                {
                    setTitle("Delete Alert")
                    setMessage("Are You sure you want to delete? ")
                    setPositiveButton("Of curse") { _: DialogInterface, _: Int ->
                        updateItemsList(REMOVE_KEY, item)
                    }
                    setNegativeButton("No Way") { _: DialogInterface, _: Int -> }
                    show()
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
