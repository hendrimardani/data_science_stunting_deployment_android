package com.example.stunting.ui.navigation_drawer_fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
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
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.user_profile.UserWithUserProfile
import com.example.stunting.databinding.ActivityNavigationDrawerMainBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.example.stunting.datastore.chatting.UserModel
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.navigation_drawer_fragment.home.NavHomeFragment.Companion.EXTRA_USER_ID_TO_NAV_HOME_FRAGMENT

class NavDrawerMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavigationDrawerMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationDrawerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavigationDrawerMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_logout
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

        getDataExtra()
        getUsers()
        getMenuNavigationView()
    }

    private fun getDataExtra() {
        val getExtraFragment = intent.getStringExtra(EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY)

        if (getExtraFragment == "LoginFragment") {
            userId = intent.getIntExtra(EXTRA_USER_ID_TO_NAV_DRAWER_MAIN_ACTIVITY, 0)
            Log.d(TAG, "onNavDrawerMainActivity userId from LoginFragment : ${userId}")
            sendDataToNavHomeFragment(userId)
            getUserWithUserProfileById(userId!!)
        } else if (getExtraFragment == "OpeningFragment") {     // Langsung masuk
            val userModel = intent.getParcelableExtra<UserModel>(EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY)!!
//            Log.d(TAG, "onNavDrawerMainActivity from OpeningActivity : ${userModel}")
            userId = userModel.id.toInt()
            sendDataToNavHomeFragment(userId)
            getUserWithUserProfileById(userId!!)
        }
    }

    private fun sendDataToNavHomeFragment(userId: Int?) {
        val bundle = Bundle().apply {
            putInt(EXTRA_USER_ID_TO_NAV_HOME_FRAGMENT, userId!!)
        }
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main)
        navController.navigate(R.id.nav_home, bundle)
    }

    private fun getUserWithUserProfileById(userId: Int) {
        viewModel.getUserWithUserProfileById(userId).observe(this) { userWithUserProfile ->
//            Log.d(TAG, "onNavDrawerMainActivity from OpeningFragment getUserWithUserProfileById : ${userWithUserProfile}")
            if (userWithUserProfile != null) getHeaderView(userWithUserProfile)
        }
    }

    private fun getUsers() {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getUsers().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> progressBar.dismiss()
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onNavDrawerMainActivity from LoginFragment getUsers : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@NavDrawerMainActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getMenuNavigationView() {
        val menuView = binding.navView.menu
        val home = menuView.findItem(R.id.nav_home)
        val settings = menuView.findItem(R.id.nav_settings)
        val logout = menuView.findItem(R.id.nav_logout)

        logout.setOnMenuItemClickListener {
            val sweetAlertDialog = SweetAlertDialog(this@NavDrawerMainActivity, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.setTitleText(getString(R.string.title_logout))
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setCancelText(getString(R.string.text_cancel))
            sweetAlertDialog.setConfirmText(getString(R.string.text_yes))
            sweetAlertDialog.setConfirmClickListener {
                Toast.makeText(this, "Berhasil keluar akun", Toast.LENGTH_LONG).show()
                viewModel.logout()
                val intent = Intent(this@NavDrawerMainActivity , MainActivity::class.java)
                intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@NavDrawerMainActivity).toBundle())
            }
            sweetAlertDialog.show()
            true
        }
    }

    private fun getHeaderView(userWithUserProfile: UserWithUserProfile?) {
        // Index 0 karena hanya ada satu header
        val headerView = binding.navView.getHeaderView(0)
        val imageNavView = headerView.findViewById<ImageView>(R.id.iv_nav_view)
        val nameNavView = headerView.findViewById<TextView>(R.id.tv_name_nav_view)
        val emailNavView = headerView.findViewById<TextView>(R.id.tv_email_nav_view)

        nameNavView.text = userWithUserProfile?.profile?.nama.toString().trim()
        emailNavView.text = userWithUserProfile?.users?.email.toString().trim()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_about -> customDialogAbout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun customDialogAbout() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomAboutBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvDescription.text = getString(R.string.description_about)
        dialogBinding.ivLinkedin.setOnClickListener {
            val url = getString(R.string.description_about_linkedIn)
            linkToWebBrowser(url)
        }
        dialogBinding.ivWa.setOnClickListener {
            val url = getString(R.string.description_about_wa)
            linkToWebBrowser(url)
        }
        dialogBinding.ivEmail.setOnClickListener {
            val url = getString(R.string.description_about_email)
            linkToWebBrowser(url)
        }
        dialogBinding.ivIg.setOnClickListener {
            val url = getString(R.string.description_about_ig)
            linkToWebBrowser(url)
        }
        dialogBinding.tvOk.setOnClickListener { customDialog.dismiss() }
        customDialog.show()
    }

    private fun linkToWebBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    companion object {
        private val TAG = NavDrawerMainActivity::class.java.simpleName
        const val EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY = "extra_fragment_to_nav_drawer_main_activity"
        const val EXTRA_USER_ID_TO_NAV_DRAWER_MAIN_ACTIVITY = "extra_user_id_to_nav_drawer_main_activity"
        const val EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY = "extra_user_model_to_nav_drawer_main_activity"
    }
}