package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView: TextView = findViewById(R.id.textView);
        val editText: EditText = findViewById(R.id.editText);
        val button: Button = findViewById(R.id.button);
        setItems(textView, editText, button);
    }

    private fun setItems(textView: TextView, editText: EditText, button: Button) {
        textView.paintFlags;

        button.setOnClickListener {
            textView.text = editText.text;
            editText.setText("");
        }

    }
}
