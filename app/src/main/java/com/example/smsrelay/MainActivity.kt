package com.example.smsrelay

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val sp = getSharedPreferences("cfg", MODE_PRIVATE)
        val l = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 96, 48, 48) }
        val code = EditText(this).apply { hint = "Extension code"; setText(sp.getString("code", "")) }
        val btn = Button(this).apply { text = "Save & Start" }
        btn.setOnClickListener {
            sp.edit().putString("code", code.text.toString().trim().uppercase()).apply()
            requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), 1)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
        l.addView(code); l.addView(btn)
        setContentView(l)
    }
}
