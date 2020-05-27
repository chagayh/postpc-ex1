package com.example.myapplication

import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.time.Instant
import java.time.format.DateTimeFormatter

class CompletedItemActivity : AppCompatActivity() {

    private val appContext: TodoApp
        get() = applicationContext as TodoApp
    private val itemContent: EditText
        get() = findViewById(R.id.itemContent)
    private val deleteBtn: Button
        get() = findViewById(R.id.deleteBtn)
    private val unDoneBtn: Button
        get() = findViewById(R.id.unDoneBtn)

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
        val item: Item? = appContext.todoListManagerDB.getItemById(itemId)
        if (item == null) {
            Log.e("ITEM_EXIST", "item id doesn't exist")
            return
        }
        itemContent.setText(item.text)

        unDoneBtn.setOnClickListener {
            updateItem(item, false)
            finish()
        }

        deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@CompletedItemActivity)
            with(builder)
            {
                setTitle("Delete Alert")
                setMessage("Are You sure you want to delete? ")
                setPositiveButton("Of curse") { _: DialogInterface, _: Int ->
                    appContext.todoListManagerDB.deleteItem(item)
                    Toast.makeText(applicationContext, "Deleted TODO ${item.text}", Toast.LENGTH_SHORT).show()
                    finish()
                }
                setNegativeButton("No Way") { _: DialogInterface, _: Int ->
                    updateItem(item, true)
                }
                show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateItem(item: Item, done: Boolean) {
        val newItem = Item(item.text, done, item.timeStamp,
            DateTimeFormatter.ISO_INSTANT.format(Instant.now()), item.firestoreDocumentId)
        appContext.todoListManagerDB.editItem(item, newItem)
    }
}
