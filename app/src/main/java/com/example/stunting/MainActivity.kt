package com.example.stunting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar
        setToolBar()

        binding.cvBumil.setOnClickListener {
//            val intent = Intent(this, BumilActivity::class.java)
//            startActivity(intent)
            showBottomSheetDialog()
        }
        binding.cvCalonPengantin.setOnClickListener {
            val intent = Intent(this, CalonPengantinActivity::class.java)
            startActivity(intent)
        }
        binding.cvRemajaPutri.setOnClickListener {
            val intent = Intent(this, RemajaPutriActivity::class.java)
            startActivity(intent)
        }
        binding.cvLayananKeluarga.setOnClickListener {
            val intent = Intent(this, LayananKeluargaActivity::class.java)
            startActivity(intent)
        }
        binding.cvAnak.setOnClickListener {
            val intent = Intent(this, AnakActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbMain)
        supportActionBar!!.title = "Stunting"
        // Change font style text
        binding.tbMain.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.icon_stunting)
//        // Enable back button if you're in a child activity
//        if (supportActionBar != null) {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        }
//        binding.tbMain.setNavigationOnClickListener {
//            onBackPressed()
//        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_bottom_sheet_bumil, null)
        bottomSheetDialog.setContentView(view)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.swipe_up_view))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        view.findViewById<View>(R.id.swipe_up_view).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        bottomSheetDialog.show()
    }
}