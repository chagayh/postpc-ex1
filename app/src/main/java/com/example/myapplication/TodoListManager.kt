package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class TodoListManager(context: Context) {
    private val appContext: Context = context
    private var itemsList: ArrayList<Item>
    private val gson: Gson
    private val spForTodoList: SharedPreferences

    companion object {
        private const val SP_TODO_LIST_FILE_NAME: String = "sp_todo_list"
        private const val KEY_TODO_LIST: String = "key_todo_list"
        private const val SIZE_TAG: String = "List Size"
    }

    init {
        spForTodoList = appContext.getSharedPreferences(SP_TODO_LIST_FILE_NAME, Context.MODE_PRIVATE);
        gson = Gson()
        itemsList = ArrayList<Item>()
        loadItemsList()
        Log.d(SIZE_TAG, itemsList.size.toString())
    }

    private fun loadItemsList(){
        val listAsJason: String? = spForTodoList.getString(KEY_TODO_LIST, null)
        if (listAsJason != null) {
            val listType = object : TypeToken<ArrayList<Item>>(){}.type
            itemsList.addAll(gson.fromJson(listAsJason, listType))    // TODO check if correct
        }
    }

    fun setItemsList(items: ArrayList<Item>) {
        itemsList = items
    }

    fun getItemsList() : ArrayList<Item> {
        return itemsList
    }

    fun storeItemsList(){
        val listAsJson: String = gson.toJson(itemsList)
        val edit: SharedPreferences.Editor = spForTodoList.edit()
        edit.putString(KEY_TODO_LIST, listAsJson).
                apply()
    }
}