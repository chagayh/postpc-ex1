package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val editText: EditText? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reverseLayout = false
        val adapter = ItemAdapter()

        adapter.setItems(createItems())
        val textView: TextView = findViewById(R.id.textView);
        val editText: EditText = findViewById(R.id.editText);
        val button: Button = findViewById(R.id.button);
        if (savedInstanceState != null) {
            textView.text = savedInstanceState.getString("EditText text");
        }
        setItems(textView, editText, button);
    }

    private fun setItems(textView: TextView, editText: EditText, button: Button) {
        textView.paintFlags;

        button.setOnClickListener {
            textView.text = editText.text;
            editText.setText("");
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (editText != null) {
            outState.putString("EditText text",  editText.text.toString())
        };
    }
}
