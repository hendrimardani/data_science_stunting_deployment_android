package com.example.stunting.ui.nav_drawer_patient_fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientRelation
import com.example.stunting.databinding.ActivityNavDrawerMainPatientBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.example.stunting.datastore.chatting.UserModel
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.nav_drawer_patient_fragment.daftar_anak.NavDaftarAnakPatientFragment.Companion.EXTRA_USER_PATIENT_ID_TO_NAV_DAFTAR_ANAK_PATIENT_FRAGMENT
import com.example.stunting.ui.nav_drawer_patient_fragment.home.NavHomePatientFragment.Companion.EXTRA_USER_PATIENT_ID_TO_NAV_HOME_PATIENT_FRAGMENT
import com.example.stunting.ui.nav_drawer_patient_fragment.user_profile.NavUserProfilePatientFragment.Companion.EXTRA_USER_PATIENT_ID_TO_NAV_USER_PROFILE_PATIENT_FRAGMENT
import com.example.stunting.utils.NetworkLiveData
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView


class NavDrawerMainActivityPatient : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavDrawerMainPatientBinding
    private val viewModel by viewModels<NavDrawerMainActivityPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var networkLiveData: NetworkLiveData
    private var userPatientId: Int? = null
    private var getExtraFragment: String? = null
    private var isTaptTargetViewActived: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavDrawerMainPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavDrawerMainActivityPatient.toolbarPatient)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home_patient, R.id.nav_user_profile_patient, R.id.nav_daftar_anak_patient,
                R.id.nav_user_login_patient, R.id.nav_pengaturan_patient
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, 0)
        getExtraFragment = intent.getStringExtra(EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT)
        isTaptTargetViewActived = intent?.getBooleanExtra(EXTRA_TAP_TARGET_VIEW_ACTIVED_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, false)

        // Ubah warna teks logout
        val menu = navView.menu
        val logoutItem = menu.findItem(R.id.nav_logout_patient)
        val spannable = SpannableString(logoutItem.title)
        spannable.setSpan(ForegroundColorSpan(Color.RED), 0, spannable.length, 0)
        logoutItem.title = spannable

        networkLiveData = NetworkLiveData(application)
        networkLiveData.observe(this) { isConnected ->
            if (isConnected) {
                getUserProfilePatientsFromApi()
                Toast.makeText(this, getString(R.string.text_connected), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.text_no_connected), Toast.LENGTH_SHORT).show()
            }
        }
        setupAnimationRotationContent()
        getUserProfilePatientsFromApi()
        getDataExtra()
        getMenuNavigationView()
    }

    // TapTargetView
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isTaptTargetViewActived!!) {
            val toolbar = binding.appBarNavDrawerMainActivityPatient.toolbarPatient
            val aboutMenu = toolbar.findViewById<View>(R.id.menu_main_about)

            if (aboutMenu != null) {
                val sequence = TapTargetSequence(this)
                    .targets(
                        TapTarget.forToolbarNavigationIcon(
                            toolbar,
                            "Geser ke kanan atau di tap",
                            "Daftar menu yang tersedia seperti profil, pengaturan."
                        )
                            .outerCircleColor(R.color.bluePrimary)
                            .targetCircleColor(R.color.white)
                            .titleTextSize(20)
                            .descriptionTextSize(15)
                            .textTypeface(Typeface.DEFAULT_BOLD)
                            .tintTarget(true)
                            .cancelable(true),

                        TapTarget.forView(
                            aboutMenu,
                            "Tentang Pembuat Aplikasi",
                            "Tap untuk melihat informasi tentang aplikasi."
                        )
                            .outerCircleColor(R.color.bluePrimary)
                            .targetCircleColor(R.color.white)
                            .titleTextSize(20)
                            .descriptionTextSize(15)
                            .tintTarget(true)
                            .cancelable(true)
                    )
                    .listener(object : TapTargetSequence.Listener {
                        override fun onSequenceFinish() { }

                        override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) { }

                        override fun onSequenceCanceled(lastTarget: TapTarget) { }
                    })
                sequence.start()
            } else {
//                Log.e("TapTarget", "View dari menu_main_about tidak ditemukan.")
            }
        }
    }

    private fun getDataExtra() {
        if (getExtraFragment == "OpeningUserProfilePatientReadyActivity") {
            userPatientId = intent.getIntExtra(
                EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, 0)
            sendDataToNavHomePatientFragment(userPatientId!!)
            getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId!!)
        } else if (getExtraFragment == "OpeningFragment" || getExtraFragment == "LoginFragment") {
            val userModel = intent.getParcelableExtra<UserModel>(
                EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
            )!!
//            Log.d(TAG, "onNavDrawerMainActivityPatient from OpeningActivity : ${userModel}")
            userPatientId = userModel.id.toInt()
            sendDataToNavHomePatientFragment(userPatientId!!)
            getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId!!)
        }
    }

    private fun sendDataToNavHomePatientFragment(userPatientId: Int) {
        val bundle = Bundle().apply {
            putInt(EXTRA_USER_PATIENT_ID_TO_NAV_HOME_PATIENT_FRAGMENT, userPatientId)
        }
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)
        navController.navigate(R.id.nav_home_patient, bundle)
    }

    private fun getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId: Int) {
        viewModel.getUserProfilePatientRelationByUserPatientIdFromLocal(userPatientId).observe(this) { userProfileWithUserRelation ->
//            Log.d(TAG, "onNavDrawerMainActivity getUserProfileWithUserById() : ${userProfileWithUserRelation.userProfile}")
            if (userProfileWithUserRelation != null) {
                getHeaderView(userProfileWithUserRelation)
            }
        }
    }

    private fun getUserProfilePatientsFromApi() {
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
                        val intent = Intent(this@NavDrawerMainActivityPatient, ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getMenuNavigationView() {
        val menuView = binding.navView.menu
        val home = menuView.findItem(R.id.nav_home_patient)
        val userProfile = menuView.findItem(R.id.nav_user_profile_patient)
        val daftarAnak = menuView.findItem(R.id.nav_daftar_anak_patient)
        val userLogin = menuView.findItem(R.id.nav_user_login_patient)
        val pengaturan = menuView.findItem(R.id.nav_pengaturan_patient)
        val logout = menuView.findItem(R.id.nav_logout_patient)

        home.setOnMenuItemClickListener {
            val bundle = Bundle().apply {
                putInt(EXTRA_USER_PATIENT_ID_TO_NAV_HOME_PATIENT_FRAGMENT, userPatientId!!)
            }
            val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)
            navController.navigate(R.id.nav_home_patient, bundle)
            false
        }
        userProfile.setOnMenuItemClickListener {
            val bundle = Bundle().apply {
                putInt(EXTRA_USER_PATIENT_ID_TO_NAV_USER_PROFILE_PATIENT_FRAGMENT, userPatientId!!)
            }
            val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)
            navController.navigate(R.id.nav_user_profile_patient, bundle)
            false
        }
        daftarAnak.setOnMenuItemClickListener {
            val bundle = Bundle().apply {
                putInt(EXTRA_USER_PATIENT_ID_TO_NAV_DAFTAR_ANAK_PATIENT_FRAGMENT, userPatientId!!)
            }
            val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)
            navController.navigate(R.id.nav_daftar_anak_patient, bundle)
            false
        }
//        userLogin.setOnMenuItemClickListener {
//
//            true
//        }

        logout.setOnMenuItemClickListener {
            val sweetAlertDialog = SweetAlertDialog(this@NavDrawerMainActivityPatient, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.setTitleText(getString(R.string.title_logout))
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setCancelText(getString(R.string.text_cancel))
            sweetAlertDialog.setConfirmText(getString(R.string.text_yes))
            sweetAlertDialog.setConfirmClickListener {
                Toast.makeText(this, "Berhasil keluar akun", Toast.LENGTH_LONG).show()
                viewModel.logout()
                val intent = Intent(this@NavDrawerMainActivityPatient , ContainerMainActivity::class.java)
                intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@NavDrawerMainActivityPatient).toBundle())
            }
            sweetAlertDialog.show()
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getHeaderView(userProfilePatientRelation: UserProfilePatientRelation) {
        // Index 0 karena hanya ada satu header
        val headerView = binding.navView.getHeaderView(0)
        val flProfile = headerView.findViewById<FrameLayout>(R.id.fl_profile)
        val civEditProfile = headerView.findViewById<CircleImageView>(R.id.civ_edit_profile)
//        val ivEditBanner = headerView.findViewById<ImageView>(R.id.iv_edit_banner)
        val name = headerView.findViewById<TextView>(R.id.tv_name_nav_view)
        val email = headerView.findViewById<TextView>(R.id.tv_email_nav_view)
        val role = headerView.findViewById<TextView>(R.id.tv_role_nav_view)

        val usersEntity = userProfilePatientRelation.users
        val userProfilePatientEntity = userProfilePatientRelation.userProfilePatient

//        flProfile.setOnClickListener {
//            isEditGambarProfile = true
//            showBottomSheetDialog()
//        }

//        ivEditBanner.setOnClickListener {
//            isEditGambarProfile = false
//            showBottomSheetDialog()
//        }

        if (userProfilePatientEntity.gambarProfile != null) {
            Glide.with(this)
                .load(userProfilePatientEntity.gambarProfile)
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

        name.text = userProfilePatientEntity.namaBumil
        email.text = usersEntity.email
        role.text = "Anda sebagai ${usersEntity?.role}"
    }

    private fun setupAnimationRotationContent() {
        val homeFragment = binding.appBarNavDrawerMainActivityPatient.contentNavDrawerMainActivityPatient.contentNavDrawerMainActivityPatient

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val scaleFactor = 1.1f - (0.1f - slideOffset)
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

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity_patient)
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
        private val TAG = NavDrawerMainActivityPatient::class.java.simpleName
        const val EXTRA_TAP_TARGET_VIEW_ACTIVED_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_tap_target_view_actived_to_nav_drawer_main_activity_patient"
        const val EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_activity_to_nav_drawer_main_activity_patient"
        const val EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_user_patient_id_to_nav_drawer_main_activity_patient"
        const val EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT = "extra_user_model_to_nav_drawer_main_activity_patient"
    }
}