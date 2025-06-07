package com.example.stunting.ui.nav_drawer_patient_fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.R
import com.example.stunting.databinding.ActivityNavDrawerMainPatientBinding
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory

class NavDrawerMainActivityPatient : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavDrawerMainPatientBinding
    private val viewModel by viewModels<NavDrawerMainActivityPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavDrawerMainPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavDrawerMainActivityPatient.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_nav_drawer_main_activity_patient)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Ubah warna teks logout
        val menu = navView.menu
        val logoutItem = menu.findItem(R.id.nav_logout)
        val spannable = SpannableString(logoutItem.title)
        spannable.setSpan(ForegroundColorSpan(Color.RED), 0, spannable.length, 0)
        logoutItem.title = spannable

        setupAnimationRotationContent()
        getMenuNavigationView()
    }

    private fun getMenuNavigationView() {
        val menuView = binding.navView.menu
        val home = menuView.findItem(R.id.nav_home)
        val settings = menuView.findItem(R.id.nav_settings)
        val logout = menuView.findItem(R.id.nav_logout)

        logout.setOnMenuItemClickListener {
            val sweetAlertDialog = SweetAlertDialog(this@NavDrawerMainActivityPatient, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.setTitleText(getString(R.string.title_logout))
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setCancelText(getString(R.string.text_cancel))
            sweetAlertDialog.setConfirmText(getString(R.string.text_yes))
            sweetAlertDialog.setConfirmClickListener {
                Toast.makeText(this, "Berhasil keluar akun", Toast.LENGTH_LONG).show()
                viewModel.logout()
                val intent = Intent(this@NavDrawerMainActivityPatient , MainActivity::class.java)
                intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@NavDrawerMainActivityPatient).toBundle())
            }
            sweetAlertDialog.show()
            true
        }
    }

    private fun setupAnimationRotationContent() {
        val homeFragment = binding.appBarNavDrawerMainActivityPatient.contentNavDrawerMainActivityPatient.contentHome

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val scaleFactor = 1 - (0.1f - slideOffset)
                val moveFactor = drawerView.width * slideOffset
                homeFragment.apply {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    translationX = moveFactor
                }
            }

            override fun onDrawerOpened(drawerView: View) { }

            override fun onDrawerClosed(drawerView: View) { }

            override fun onDrawerStateChanged(newState: Int) { }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_nav_drawer_patient, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_nav_drawer_main_activity_patient)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_activity_to_nav_drawer_main_activity_patient"
        const val EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_user_patient_id_to_nav_drawer_main_activity_patient"
    }
}