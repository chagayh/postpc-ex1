package com.example.myapplication

import android.app.Application

class TodoApp: Application() {
    lateinit var todoListManager: TodoListManager

    override fun onCreate() {
        super.onCreate()
        todoListManager = TodoListManager(this)
    }
}