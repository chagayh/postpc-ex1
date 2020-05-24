package com.example.myapplication

import android.app.Application

class TodoApp: Application() {
    lateinit var todoListManagerDB: TodoListManagerDB

    override fun onCreate() {
        super.onCreate()
        todoListManagerDB = TodoListManagerDB()
    }
}