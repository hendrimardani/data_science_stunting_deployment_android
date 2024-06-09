package com.example.stunting

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityBumilBinding

class BumilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBumilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBumilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

       binding.rgBumil.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_ya_bumil -> {
                    Toast.makeText(this@BumilActivity, "TERKLIK YA", Toast.LENGTH_LONG).show()
                }
                R.id.rb_tidak_bumil -> {
                    Toast.makeText(this@BumilActivity, "TERKLIK TIDAK", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbBumil)
        supportActionBar!!.title = "Stunting"
        // Change font style text
        binding.tbBumil.setTitleTextAppearance(this, R.style.Theme_Stunting)

        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbBumil.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}