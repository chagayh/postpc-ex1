package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot


class TodoListManagerDB (private val context : Context) {
    // another way to do it was letting class Pet have an "id" field and use a HashMap like in the previous example
    private val allItems = ArrayList<Item>()

    // kotlin's init is like java's constructor code.
    // code will execute when creating an instance of this class
    init {
        createLiveQuery()
        Log.d(SIZE_TAG, allItems.size.toString())
    }

    companion object {
        private const val ITEM_COLLECTION_PATH: String = "todo_list"
        private const val LOG_TAG: String = "FirestoreItemManager"
        private const val SIZE_TAG: String = "List_Size"
    }

    fun getItemsList(): ArrayList<Item> {
        Log.e("ITEM_LIST", "in get item list")
        // return a copy of the local list
        // why copy? bcs we don't want anyone to start adding/removing pets from our private list
        return ArrayList(allItems)
    }

    fun setIsDone(item: Item, done: Boolean) {
        val firestore = FirebaseFirestore.getInstance()
        val documentId = item.firestoreDocumentId
        if (documentId == null) {
            // we don't know where to look! so can't update the document from firestore
            Log.e(LOG_TAG, "can't update item in firestore, no document-id!$item")
            return
        }
        val document = firestore.collection(ITEM_COLLECTION_PATH).document(documentId)
        document
            .update("done", done)
            .addOnSuccessListener { Log.d(LOG_TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(LOG_TAG, "Error updating document", e) }
    }

    fun addItem(item: Item) {
        if (item in allItems) {
            Log.w("ItemManager", "ignoring, Item already in local arrayList!")
        }

        // add to local list
        allItems.add(item)

        // add to firebase
        val firestore = FirebaseFirestore.getInstance()
        // letting firestore generate a document-id for us
        val document = firestore.collection(ITEM_COLLECTION_PATH).document()
        item.firestoreDocumentId = document.id

        document.set(item)
        // ".set(object)" starts a task to create (or override) the data for this document.
        // if we want, we can listen to the task by chaining
        // ".addOnSuccessListener()" and/or ".addOnFailureListener()"
    }


    // edit an existing pet
    // notice that because we don't have an "id" field, we need the old object so we can find it in the list
    fun editItem(oldItem: Item, newItem: Item) {
        /// update in local list
        val index: Int = allItems.indexOf(oldItem)
        Log.e("EDIT_ITEM", "${newItem.done}")
        Log.e("EDIT_ITEM", "${oldItem.done}")
        if (index == -1) {
            Log.e(LOG_TAG, "can't edit item: could not find old pet!")
            return
        }

        allItems.remove(oldItem)
        allItems.add(index, newItem)

        // update in firebase
        val documentId = oldItem.firestoreDocumentId
        if (documentId == null) {
            // we don't know where to look! so can't update the document from firestore
            Log.e(LOG_TAG, "can't update item in firestore, no docucment-id!$oldItem")
            return
        }

        newItem.firestoreDocumentId = documentId // preserve it as we're about to override oldItem in firestore
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(ITEM_COLLECTION_PATH).document(documentId).set(newItem)
    }

    fun deleteItem(item: Item) {
        // delete from local list
        allItems.remove(item)

        // delete from firebase
        val documentId = item.firestoreDocumentId
        if (documentId == null) {
            // we don't know where to look! so can't delete the document from firestore
            Log.e(LOG_TAG, "can't delete pet, no docucment-id to delete in firestore!$item")
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(ITEM_COLLECTION_PATH).document(documentId).delete()
            // ".delete()" starts a task to delete this document.
            // if we want, we can listen to the task by chaining
            // ".addOnSuccessListener()" and/or ".addOnFailureListener()"
            // let's do it, for the sport:
            .addOnSuccessListener {
                Log.d(LOG_TAG, "item " + item + "was successfully deleted from firestore!")
            }
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "got exception :( when trying to delete item " + item + ": " + exception.message)
            }
    }

    // after calling this method, there will be a live query that firestore will trigger
    // every time the collection "pets" is changed
    // (e.g. when a document in this collection gets created, deleted, or getting its data changed)
    private fun createLiveQuery(){

        val firestore = FirebaseFirestore.getInstance()

        val referenceToCollection = firestore.collection(ITEM_COLLECTION_PATH)
        // we could also add "constraints" to this reference
        // for example, if we wanted a live query for {all the items in collection "pets" that
        // have their string field "animalType" equals "dog"}
        // we could have written this:
        // {val referenceToCollection = firestore.collection("pets").whereEqualTo("animalType", "dog")}
        // but we want to get all the documents in this collection without any constraint


        // the code in ".addSnapshotListener {}" will execute in the future, first time when firestore
        // will finish downloading the collection to the phone,
        // and then each time when documents in the collection get changed
        val liveQuery = referenceToCollection.addSnapshotListener { value, exception ->
            if (exception != null) {
                // problems...
                Log.d(LOG_TAG, "exception in snapshot :(" + exception.message)
                return@addSnapshotListener
            }

            if (value == null) {
                // no data...
                Log.d(LOG_TAG, "value is null :(")
                return@addSnapshotListener
            }

            Log.e(LOG_TAG, "reached here? we got data! yay :)")
            // reached here? we got data! yay :)
            // let's refresh the local arrayList
            this.allItems.clear()
            for (document: QueryDocumentSnapshot in value) {
                val item = document.toObject(Item::class.java) // convert to item
                this.allItems.add(item)
            }
        }
        referenceToCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent("hello")
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
        }

        // NOTICE: the live-query (also called "snapshot") stored in the variable "liveQuery",
        // will continue to listen until you will call "liveQuery.remove()"
        // you can just ignore the variable and the live-query will continue listening forever
        // (or at least until your application process will be killed by the OS)
    }
}