package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ItemAdapter
    private lateinit var appContext: TodoApp

    companion object {
        private const val ADD_KEY = "add"
        private const val REMOVE_KEY = "remove"
        private const val DONE_KEY = "set_done"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext as TodoApp // Application context
        adapter = ItemAdapter()
        val reverseLayout = false
        val broadcastReceiver = (object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                adapter.setItems(appContext.todoListManagerDB.getItemsList())
            }
        })
        LocalBroadcastManager.getInstance(appContext)
            .registerReceiver(broadcastReceiver, IntentFilter("hello"))
        items_recycler.adapter = adapter
        items_recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, reverseLayout)
        editText.setText(savedInstanceState?.getString("EditText text"))
        setComponents()
    }

    override fun onResume() {
        super.onResume()
        adapter.setItems(appContext.todoListManagerDB.getItemsList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setComponents() {
        button.setOnClickListener {
            if (editText.text.toString() == "") {
                Toast.makeText(appContext, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show()
            } else {
                val timeStamp = "Date = ${LocalDate.now()}\nTime = ${LocalTime.now()}"
                val item = Item(editText.text.toString(), false, timeStamp)
                appContext.todoListManagerDB.addItem(item)
                editText.text.clear()
                adapter.setItems(appContext.todoListManagerDB.getItemsList())
            }
        }

        adapter.itemClickListener = (object : ItemClickListener {
            override fun onItemClicked(item: Item) {
                if (!item.done) {
                    val intent = Intent(this@MainActivity, NotCompletedItemActivity::class.java)
                    intent.putExtra("item_id", item.firestoreDocumentId)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this@MainActivity, CompletedItemActivity::class.java)
                    intent.putExtra("item_id", item.firestoreDocumentId)
                    startActivity(intent)
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EditText text", editText.text.toString())
    }
}
