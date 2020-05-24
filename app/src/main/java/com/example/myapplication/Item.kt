package com.example.myapplication

data class Item(
    var text: String,
    var isDone: Boolean,
    var timeStamp: String,
    var lastModified: String = timeStamp,   // TODO - check
    var firestoreDocumentId: String? = null
)