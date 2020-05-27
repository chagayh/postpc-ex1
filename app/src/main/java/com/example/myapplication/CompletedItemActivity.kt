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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CompletedItemActivity : AppCompatActivity() {

    private val appContext: TodoApp
        get() = applicationContext as TodoApp
    private val itemContent: TextView
        get() = findViewById(R.id.itemContent)
    private val deleteBtn: Button
        get() = findViewById(R.id.applyBtn)
    private val unDoneBtn: Button
        get() = findViewById(R.id.doneBtn)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_item)
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
        itemContent.text = item.text

        unDoneBtn.setOnClickListener {
            updateItem(item!!, item!!.text, false)
            finish()
        }

        deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@CompletedItemActivity)
            with(builder)
            {
                setTitle("Delete Alert")
                setMessage("Are You sure you want to delete? ")
                setPositiveButton("Of curse") { _: DialogInterface, _: Int ->
                    appContext.todoListManagerDB.deleteItem(item!!)
                    Toast.makeText(applicationContext, "Deleted TODO ${item!!.text}", Toast.LENGTH_SHORT).show()
                    finish()
                }
                setNegativeButton("No Way") { _: DialogInterface, _: Int ->
                    item = updateItem(item!!, item!!.text, item!!.done)
                }
                show()
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
