package com.example.visual_polish

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Four text views for four activities
        val colorAndFont = findViewById(R.id.colorAndFont) as TextView
        val style = findViewById(R.id.style) as TextView
        val responsiveLayouts = findViewById(R.id.responsiveLayouts) as TextView
        val touchSelector = findViewById(R.id.touchSelector) as TextView
        assert(colorAndFont != null)
        colorAndFont.setOnClickListener {
            val numbersIntent = Intent(this@MainActivity, ColorFontActivity::class.java)
            startActivity(numbersIntent)
        }
        assert(style != null)
        style.setOnClickListener {
            val familyIntent = Intent(this@MainActivity, StyleActivity::class.java)
            startActivity(familyIntent)
        }
        assert(responsiveLayouts != null)
        responsiveLayouts.setOnClickListener {
            val familyIntent =
                Intent(this@MainActivity, ResponsiveLayoutActivity::class.java)
            startActivity(familyIntent)
        }
        assert(touchSelector != null)
        touchSelector.setOnClickListener {
            val colorsIntent = Intent(this@MainActivity, SelectorsActivity::class.java)
            startActivity(colorsIntent)
        }
    }
}
