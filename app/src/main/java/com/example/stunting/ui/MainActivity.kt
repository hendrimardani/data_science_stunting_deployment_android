package com.example.stunting.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.R
import com.example.stunting.adapter.ListCegahStuntingAdapter
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.example.stunting.resouce_data.CegahStuntingData
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var overlay: View

    private val list = ArrayList<CegahStuntingData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Toolbar
        setToolBar()

        // bottomSheetCoordinatorLayout
        setupBottomSheet()
        setupViews()

        // Get from resources
        binding.rvCegahStunting.setHasFixedSize(true)
        list.addAll(getListCegahStuntingData())
        showRecyclerList()

        binding.apply {
            cvBumil.setOnClickListener {
                val intent = Intent(this@MainActivity, BumilActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle())
            }
            cvCalonPengantin.setOnClickListener {
                val intent = Intent(this@MainActivity, CalonPengantinActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle())
            }
            cvRemajaPutri.setOnClickListener {
                val intent = Intent(this@MainActivity, RemajaPutriActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle())
            }
            cvLayananKeluarga.setOnClickListener {
                val intent = Intent(this@MainActivity, LayananKeluargaActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle())
            }
            cvAnak.setOnClickListener {
                val intent = Intent(this@MainActivity, AnakActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle())
            }
            fab.setOnClickListener {
                val intent = Intent(this@MainActivity, KonsultasiActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle())
            }
        }
    }

    private fun showRecyclerList() {
        binding.rvCegahStunting.layoutManager = LinearLayoutManager(this)
        val listCegahStuntingAdapter = ListCegahStuntingAdapter(list)
        binding.rvCegahStunting.adapter = listCegahStuntingAdapter
    }

    @SuppressLint("Recycle")
    private fun getListCegahStuntingData(): ArrayList<CegahStuntingData> {
        val titles = resources.getStringArray(R.array.data_titles)
        val images = resources.obtainTypedArray(R.array.data_images)
        val listCegahStuntingData = ArrayList<CegahStuntingData>()

        for (i in 0..titles.size - 1) {
            val items = CegahStuntingData(titles[i], images.getResourceId(i, -1))
            listCegahStuntingData.add(items)
        }
        return listCegahStuntingData
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            // Keluar dari aplikasi jika CoordinatorLayout tidak terlihat
            super.onBackPressed()
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = findViewById<View>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        overlay = findViewById(R.id.overlay)

        bottomSheetBehavior.apply {
            isHideable = true

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            overlay.visibility = View.VISIBLE
                            overlay.alpha = 1f
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            overlay.visibility = View.GONE
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            overlay.visibility = View.VISIBLE
                            overlay.alpha = 1f
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            overlay.visibility = View.GONE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset > 0) {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = slideOffset
                    } else {
                        overlay.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun setupViews() {
        binding.cvCegahStunting.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        // Setup overlay click behavior
        overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_about -> customDialogAbout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun customDialogAbout() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomAboutBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
    }
}