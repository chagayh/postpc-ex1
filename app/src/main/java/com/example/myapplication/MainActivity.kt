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

    private fun updateItemsList(action : String, item : Item, isDone: Boolean) {
        when (action) {
            ADD_KEY -> appContext.todoListManagerDB.addItem(item)
            REMOVE_KEY -> appContext.todoListManagerDB.deleteItem(item)
            DONE_KEY -> {
                item.done = isDone
                appContext.todoListManagerDB.editItem(item, item)
            }
        }
        adapter.setItems(appContext.todoListManagerDB.getItemsList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setComponents() {
        button.setOnClickListener {
            if (editText.text.toString() == "") {
                Toast.makeText(appContext, "you can't create an empty TODO item, oh silly!", Toast.LENGTH_SHORT).show()
            } else {
                val item = Item(editText.text.toString(), false, DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                updateItemsList(ADD_KEY, item, false)
                editText.text.clear()
            }
        }

        adapter.itemClickListener = (object : ItemClickListener {
            override fun onItemClicked(item: Item) {
                if (!item.done) {
                    val msg = String.format("TODO %s is now DONE. BOOM!", item.text)
                    updateItemsList(DONE_KEY, item, true)
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                }
//                else {
//
//                }
            }
        })

//        adapter.itemLongClickListener = (object : ItemLongClickListener {
//            override fun onLongItemClicked(item: Item) {
//                val builder = AlertDialog.Builder(this@MainActivity)
//                with(builder)
//                {
//                    setTitle("Delete Alert")
//                    setMessage("Are You sure you want to delete? ")
//                    setPositiveButton("Of curse") { _: DialogInterface, _: Int ->
//                        updateItemsList(REMOVE_KEY, item)
//                    }
//                    setNegativeButton("No Way") { _: DialogInterface, _: Int -> }
//                    show()
//                }
//            }
//        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EditText text", editText.text.toString())
    }
}
