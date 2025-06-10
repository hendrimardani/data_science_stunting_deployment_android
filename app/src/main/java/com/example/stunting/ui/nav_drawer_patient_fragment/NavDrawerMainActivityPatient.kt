package com.example.stunting.ui.nav_drawer_patient_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
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
import com.bumptech.glide.Glide
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientWithUserRelation
import com.example.stunting.databinding.ActivityNavDrawerMainPatientBinding
import com.example.stunting.datastore.chatting.UserModel
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.nav_drawer_patient_fragment.home.NavHomePatientFragment.Companion.EXTRA_USER_PATIENT_ID_TO_NAV_HOME_FRAGMENT
import de.hdodenhof.circleimageview.CircleImageView

class NavDrawerMainActivityPatient : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavDrawerMainPatientBinding
    private val viewModel by viewModels<NavDrawerMainActivityPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userPatientId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavDrawerMainPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavDrawerMainActivityPatient.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home_patient, R.id.nav_settings_patient
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, 0)

        // Ubah warna teks logout
        val menu = navView.menu
        val logoutItem = menu.findItem(R.id.nav_logout_patient)
        val spannable = SpannableString(logoutItem.title)
        spannable.setSpan(ForegroundColorSpan(Color.RED), 0, spannable.length, 0)
        logoutItem.title = spannable

        setupAnimationRotationContent()
        getUserProfiles()
        getUserProfilePatients()
        getDataExtra()
        getMenuNavigationView()
    }

    private fun getDataExtra() {
        val getExtraFragment = intent.getStringExtra(EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT)

        if (getExtraFragment == "LoginFragment") {
            userPatientId = intent.getIntExtra(
                EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, 0)
//            Log.d(TAG, "onNavDrawerMainActivityPatient userPatientId from LoginFragment : ${userPatientId}")
            sendDataToNavHomePatientFragment(userPatientId!!)
            getUserProfilePatientWithUserById(userPatientId!!)
        } else if (getExtraFragment == "OpeningFragment") {
            val userModel = intent.getParcelableExtra<UserModel>(
                EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
            )!!
//            Log.d(TAG, "onNavDrawerMainActivityPatient from OpeningActivity : ${userModel}")
            userPatientId = userModel.id.toInt()
            sendDataToNavHomePatientFragment(userPatientId!!)
            getUserProfilePatientWithUserById(userPatientId!!)
        }
    }

    private fun sendDataToNavHomePatientFragment(userPatientId: Int) {
        val bundle = Bundle().apply {
            putInt(EXTRA_USER_PATIENT_ID_TO_NAV_HOME_FRAGMENT, userPatientId)
        }
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)
        navController.navigate(R.id.nav_home_patient, bundle)
    }

    private fun getUserProfilePatientWithUserById(userPatientId: Int) {
        viewModel.getUserProfilePatientWithUserRelationByIdFromLocal(userPatientId).observe(this) { userProfileWithUserRelation ->
//            Log.d(TAG, "onNavDrawerMainActivity getUserProfileWithUserById() : ${userProfileWithUserRelation.userProfile}")
            if (userProfileWithUserRelation != null) {
                getHeaderView(userProfileWithUserRelation)
            }
        }
    }

    private fun getUserProfilePatients() {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getUserProfilePatientsFromApiResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onNavDrawerMainActivityPatient from LoginFragment getUserProfilePatients : ${result.error}")
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onNavDrawerMainActivityPatient from LoginFragment getUserProfilePatients : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@NavDrawerMainActivityPatient, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getUserProfiles() {
        viewModel.getUserProfilesFromApiResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {  }
                    is ResultState.Error -> {
//                        Log.d(TAG, "onNavDrawerMainActivityPatient from LoginFragment getUserProfiles : ${result.error}")
                    }
                    is ResultState.Success -> {
//                        Log.d(TAG, "onNavDrawerMainActivityPatient from LoginFragment getUserProfiles : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@NavDrawerMainActivityPatient, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getMenuNavigationView() {
        val menuView = binding.navView.menu
        val home = menuView.findItem(R.id.nav_home_patient)
        val settings = menuView.findItem(R.id.nav_settings_patient)
        val logout = menuView.findItem(R.id.nav_logout_patient)

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

    @SuppressLint("SetTextI18n")
    private fun getHeaderView(userProfilePatientWithUserRelation: UserProfilePatientWithUserRelation) {
        // Index 0 karena hanya ada satu header
        val headerView = binding.navView.getHeaderView(0)
        val flProfile = headerView.findViewById<FrameLayout>(R.id.fl_profile)
        val civEditProfile = headerView.findViewById<CircleImageView>(R.id.civ_edit_profile)
//        val ivEditBanner = headerView.findViewById<ImageView>(R.id.iv_edit_banner)
        val name = headerView.findViewById<TextView>(R.id.tv_name_nav_view)
        val email = headerView.findViewById<TextView>(R.id.tv_email_nav_view)
        val role = headerView.findViewById<TextView>(R.id.tv_role_nav_view)

        val userProfile = userProfilePatientWithUserRelation?.userProfilePatient

//        flProfile.setOnClickListener {
//            isEditGambarProfile = true
//            showBottomSheetDialog()
//        }

//        ivEditBanner.setOnClickListener {
//            isEditGambarProfile = false
//            showBottomSheetDialog()
//        }

        if (userProfile?.gambarProfile != null) {
            Glide.with(this)
                .load(userProfile.gambarProfile)
                .into(civEditProfile)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_person_40)
                .into(civEditProfile)
        }

//        val urlBanner = userProfileWithUserRelation?.userProfile?.gambarBanner
//        Glide.with(this)
//            .load(urlBanner)
//            .into(ivEditBanner)

        name.text = userProfilePatientWithUserRelation.userProfilePatient?.namaBumil
        email.text = userProfilePatientWithUserRelation.users.email
        role.text = "Anda sebagai ${userProfilePatientWithUserRelation.users.role}"
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
            findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        private val TAG = NavDrawerMainActivityPatient::class.java.simpleName
        const val EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_activity_to_nav_drawer_main_activity_patient"
        const val EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_user_patient_id_to_nav_drawer_main_activity_patient"
        const val EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_user_model_to_Nnav_drawer_main_activity_patient"
    }
}