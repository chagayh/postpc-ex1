package com.example.myapplication

data class Item(
    var text: String = "",
    var done: Boolean = false,
    var timeStamp: String = "",
    var lastModified: String = timeStamp,
    var firestoreDocumentId: String? = null
)