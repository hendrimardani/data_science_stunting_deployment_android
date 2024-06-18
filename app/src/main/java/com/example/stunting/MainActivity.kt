package com.example.stunting

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.databinding.DialogBottomSheetCegahStuntingBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogBottomSheetCegahStunting: DialogBottomSheetCegahStuntingBinding

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
        // Binding bottom sheet dialog cegah stunting
        dialogBottomSheetCegahStunting = DialogBottomSheetCegahStuntingBinding.inflate(layoutInflater)

        binding.cvBumil.setOnClickListener {
            val intent = Intent(this, BumilActivity::class.java)
            startActivity(intent)
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
        binding.cvCegahStunting.setOnClickListener {
            showBottomSheetDialogPencegahanStunting()
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
        dialogBinding.tvDescription.text =
            "Jika anda mengalami permasalah pada aplikasi anda silahkan hubungi pembuat aplikasi " +
                    "dibawah ini, atau jika ingin ada yang ditanyakan karena proses pembuatan aplikasi" +
                    " ini masih tahap percobaan dan belum sebenuhnya sempurna.\nSilahkan hubungi Kotak dibawah ini :"
        // Link to linkedin
        dialogBinding.ivLinkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/hendri-mardani-1b6ba61a8/"
            linkToWebBrowser(url)
        }
        // Link to WA
        dialogBinding.ivWa.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=6281388372075"
            linkToWebBrowser(url)
        }
        // Link to Email
        dialogBinding.ivEmail.setOnClickListener {
            val url = "https://mail.google.com/mail/u/0/#inbox?compose=DmwnWrRvxmkxPlXNnrdQHFnTpMFwwGQFsRbBzClXRqRrMbKBBgxdXmgPxVTVNKmFGBHJTdgpsnsV"
            linkToWebBrowser(url)
        }
        // Link to Instagral
        dialogBinding.ivIg.setOnClickListener {
            val url = "https://www.instagram.com/hendri.mardani/"
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

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

//        dialogBottomSheetCegahStunting.rvPencegahanStunting.layoutManager = LinearLayoutManager(this)
//        dialogBottomSheetCegahStunting.rvPencegahanStunting
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
}