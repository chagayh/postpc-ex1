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
        val itemInfo = "TODO was created on ${item.timeStamp}\nitem was last modified on ${item.lastModified}"
        itemModified.text = itemInfo

        doneBtn.setOnClickListener {
            val newItem = Item(
                item!!.text, true, item!!.timeStamp,
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()), item!!.firestoreDocumentId)
            appContext.todoListManagerDB.editItem(item!!, newItem)
            Toast.makeText(applicationContext, "Congrats TODO ${item!!.text} is now DONE", Toast.LENGTH_SHORT).show()
            finish()
        }

        applyBtn.setOnClickListener {
            if (itemContent.text.toString() == "") {
                Toast.makeText(appContext, "you can't change to an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show()
            } else {
                val newItem = Item(itemContent.text.toString(), item!!.done, item!!.timeStamp,
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()), item!!.firestoreDocumentId)
                appContext.todoListManagerDB.editItem(item!!, newItem)
                Toast.makeText(applicationContext, "changed TODO ${item!!.text} to ${itemContent.text}", Toast.LENGTH_SHORT).show()
                item = newItem
            }
        }
    }
}

