package com.example.stunting.ui.nav_drawer_patient_fragment

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.stunting.R
import com.example.stunting.databinding.ActivityNavDrawerMainPatientBinding

class NavDrawerMainActivityPatient : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavDrawerMainPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavDrawerMainPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavDrawerMainActivityPatient.toolbar)

        binding.appBarNavDrawerMainActivityPatient.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_nav_drawer_main_activity_patient)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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