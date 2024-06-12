package com.example.stunting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityAnakBinding

class AnakActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnakBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAnakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        binding.btnSubmitAnak.setOnClickListener {
            /* TODO */
        }
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbAnak)
        supportActionBar!!.title = "Layanan Anak"
        // Change font style text
        binding.tbAnak.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.anak)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbAnak.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}