package com.example.myapplication

import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotCompletedItemActivity : AppCompatActivity() {

    private val appContext: TodoApp
        get() = applicationContext as TodoApp
    private val itemContent: EditText
        get() = findViewById(R.id.itemContent)
    private val itemModified: TextView
        get() = findViewById(R.id.itemModified)
    private val applyBtn: Button
        get() = findViewById(R.id.applyBtn)
    private val doneBtn: Button
        get() = findViewById(R.id.doneBtn)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_completed_item)
        setComponents(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setComponents(savedInstanceState: Bundle?) {
        val intent = intent
        val itemId = intent.getStringExtra("item_id")
        if (itemId == null) {
            Log.e("INTENT_EXTRA", "item id = null, can't find item")
            return
        }
        var item: Item? = appContext.todoListManagerDB.getItemById(itemId)
        if (item == null) {
            Log.e("ITEM_EXIST", "item id doesn't exist")
            return
        }
        itemContent.setText(item.text)
        val itemInfo = "TODO was created on -\n${item.timeStamp}\n\n\nitem was last modified on -\n${item.lastModified}"
        itemModified.text = itemInfo

        doneBtn.setOnClickListener {
            updateItem(item!!, item!!.text, true)
            Toast.makeText(applicationContext, "Congrats TODO ${item!!.text} is now DONE", Toast.LENGTH_SHORT).show()
            finish()
        }

        applyBtn.setOnClickListener {
            if (itemContent.text.toString() == "") {
                Toast.makeText(appContext, "you can't change to an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show()
                item = updateItem(item!!, item!!.text, item!!.done)
            } else {
                val oldText = item!!.text
                item = updateItem(item!!, itemContent.text.toString(), item!!.done)
                Toast.makeText(applicationContext, "changed TODO $oldText to ${itemContent.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateItem(oldItem: Item, text: String, done: Boolean) : Item {
        val timeStamp = "Date = ${LocalDate.now()}\nTime = ${LocalTime.now()}"
        val newItem = Item(text, done, oldItem.timeStamp, timeStamp, oldItem.firestoreDocumentId)
        appContext.todoListManagerDB.editItem(oldItem, newItem)
        return newItem
    }
}

