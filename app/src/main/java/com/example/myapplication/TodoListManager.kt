package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences


class TodoListManager(context: Context) {
    private val appContext: Context = context

    companion object {
        private const val TODO_LIST_FILE_NAME: String = "todo_list"
    }

    init {
        val spForTodoList: SharedPreferences = appContext.getSharedPreferences(TODO_LIST_FILE_NAME, Context.MODE_PRIVATE);
    }

    fun addToList(item: Item) {

    }

}