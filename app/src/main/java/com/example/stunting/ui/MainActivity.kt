package com.example.stunting.ui

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.databinding.DialogBottomSheetCegahStuntingBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var _dialogBottomSheetCegahStunting: DialogBottomSheetCegahStuntingBinding? = null
    private val dialogBottomSheetCegahStunting get() = _dialogBottomSheetCegahStunting!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()
        // Binding bottom sheet dialog cegah stunting
        _dialogBottomSheetCegahStunting = DialogBottomSheetCegahStuntingBinding.inflate(layoutInflater)

        binding.apply {
            cvBumil.setOnClickListener {
                val intent = Intent(this@MainActivity, BumilActivity::class.java)
                startActivity(intent)
            }
            cvCalonPengantin.setOnClickListener {
                val intent = Intent(this@MainActivity, CalonPengantinActivity::class.java)
                startActivity(intent)
            }
            cvRemajaPutri.setOnClickListener {
                val intent = Intent(this@MainActivity, RemajaPutriActivity::class.java)
                startActivity(intent)
            }
            cvLayananKeluarga.setOnClickListener {
                val intent = Intent(this@MainActivity, LayananKeluargaActivity::class.java)
                startActivity(intent)
            }
            cvAnak.setOnClickListener {
                val intent = Intent(this@MainActivity, AnakActivity::class.java)
                startActivity(intent)
            }
            cvCegahStunting.setOnClickListener {
                showBottomSheetDialogPencegahanStunting()
            }
            fab.setOnClickListener {
                startActivity(Intent(this@MainActivity, KonsultasiActivity::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about -> {
                // Menu About
                customDialogAbout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun customDialogAbout() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomAboutBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        // Set text
        dialogBinding.tvDescription.text = getString(R.string.description_about)
        // Link to linkedin
        dialogBinding.ivLinkedin.setOnClickListener {
            val url = getString(R.string.description_about_linkedIn)
            linkToWebBrowser(url)
        }
        // Link to WA
        dialogBinding.ivWa.setOnClickListener {
            val url = getString(R.string.description_about_wa)
            linkToWebBrowser(url)
        }
        // Link to Email
        dialogBinding.ivEmail.setOnClickListener {
            val url = getString(R.string.description_about_email)
            linkToWebBrowser(url)
        }
        // Link to Instagral
        dialogBinding.ivIg.setOnClickListener {
            val url = getString(R.string.description_about_ig)
            linkToWebBrowser(url)
        }
        dialogBinding.tvOk.setOnClickListener {
            // Close
            customDialog.dismiss()
        }
        // Display dialog
        customDialog.show()
    }

    private fun linkToWebBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun showBottomSheetDialogPencegahanStunting() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = dialogBottomSheetCegahStunting.root

        dialogBottomSheetCegahStunting.tv1.text = getString(R.string.text_1)
        dialogBottomSheetCegahStunting.tv2.text = getString(R.string.text_2)
        dialogBottomSheetCegahStunting.tv3.text = getString(R.string.text_3)
        dialogBottomSheetCegahStunting.tv4.text = getString(R.string.text_4)
        dialogBottomSheetCegahStunting.tv5.text = getString(R.string.text_5)
        dialogBottomSheetCegahStunting.tv6.text = getString(R.string.text_6)
        dialogBottomSheetCegahStunting.tv7.text = getString(R.string.text_7)
        dialogBottomSheetCegahStunting.tv8.text = getString(R.string.text_8)
        dialogBottomSheetCegahStunting.tv9.text = getString(R.string.text_9)
        dialogBottomSheetCegahStunting.tv10.text = getString(R.string.text_10)
        dialogBottomSheetCegahStunting.tv11.text = getString(R.string.text_11)
        dialogBottomSheetCegahStunting.tv12.text = getString(R.string.text_12)
        dialogBottomSheetCegahStunting.tv13.text = getString(R.string.text_13)
        dialogBottomSheetCegahStunting.tv14.text = getString(R.string.text_14)
        dialogBottomSheetCegahStunting.tv15.text = getString(R.string.text_15)
        dialogBottomSheetCegahStunting.tv16.text = getString(R.string.text_16)
        dialogBottomSheetCegahStunting.tv17.text = getString(R.string.text_17)
        dialogBottomSheetCegahStunting.tv18.text = getString(R.string.text_18)

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbMain)
        supportActionBar!!.title = getString(R.string.app_name)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _dialogBottomSheetCegahStunting = null
    }
}