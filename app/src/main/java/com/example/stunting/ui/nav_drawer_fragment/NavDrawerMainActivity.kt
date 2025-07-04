package com.example.stunting.ui.nav_drawer_fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.stunting.database.with_api.entities.user_profile.UserProfileWithUserRelation
import com.example.stunting.databinding.ActivityNavDrawerMainBinding
import com.example.stunting.databinding.DialogBottomSheetFotoBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.example.stunting.datastore.chatting.UserModel
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.nav_drawer_fragment.home.NavHomeFragment.Companion.EXTRA_USER_ID_TO_NAV_HOME_FRAGMENT
import com.example.stunting.utils.Functions.getImageUri
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView

class NavDrawerMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavDrawerMainBinding
    private val viewModel by viewModels<NavDrawerMainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null
    private var isEditGambarProfile: Boolean = false

    private var _bindingFotoBottomSheetDialog: DialogBottomSheetFotoBinding? = null
    private val bindingFotoBottomSheetDialog get() = _bindingFotoBottomSheetDialog!!

    private var currentImageProfileUri: Uri? = null
    private var currentImageBannerUri: Uri? = null

    private val launcherIntentCameraProfile = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
//            showImageProfile()
        } else {
            currentImageProfileUri = null
        }
    }

//    private val launcherIntentCameraBanner = registerForActivityResult(
//        ActivityResultContracts.TakePicture()
//    ) { isSuccess ->
//        if (isSuccess) {
//            showImageBanner()
//        } else {
//            currentImageBannerUri = null
//        }
//    }

    private val launcherGalleryProfile = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentImageProfileUri = uri
//            showImageProfile()
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_LONG).show()
        }
    }

//    private val launcherGalleryBanner = registerForActivityResult(
//        ActivityResultContracts.PickVisualMedia()
//    ) { uri ->
//        if (uri != null) {
//            currentImageBannerUri = uri
//            showImageBanner()
//        } else {
//            Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_LONG).show()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavDrawerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavigationDrawerMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        _bindingFotoBottomSheetDialog = DialogBottomSheetFotoBinding.inflate(layoutInflater)

        // Ubah warna teks logout
        val menu = navView.menu
        val logoutItem = menu.findItem(R.id.nav_logout)
        val spannable = SpannableString(logoutItem.title)
        spannable.setSpan(ForegroundColorSpan(Color.RED), 0, spannable.length, 0)
        logoutItem.title = spannable

        getUserProfiles()
        getDataExtra()
        getMenuNavigationView()
    }

//    private fun updateUserProfileById(
//        userId: Int, imageView: ImageView, isEditGambarProfile: Boolean,
//        userProfileWithUserRelation: UserProfileWithUserRelation
//    ) {
//        if (isEditGambarProfile) {
//            currentImageProfileUri?.let { uri ->
//                val nama = userProfileWithUserRelation.userProfile?.nama
//                val nik = userProfileWithUserRelation.userProfile?.nik
//                val umur = userProfileWithUserRelation.userProfile?.umur
//                val alamat = userProfileWithUserRelation.userProfile?.alamat
//                val jenisKelamin = userProfileWithUserRelation.userProfile?.jenisKelamin
//                val tglLahir = userProfileWithUserRelation.userProfile?.tglLahir
//                val imageProfileFile = uriToFile(uri, this).reduceFileImage()
//
//                val progressBar = SweetAlertDialog(this@NavDrawerMainActivity, SweetAlertDialog.PROGRESS_TYPE)
//                progressBar.setTitleText(getString(R.string.title_loading))
//                progressBar.setContentText(getString(R.string.description_loading))
//                    .progressHelper.barColor = Color.parseColor("#73D1FA")
//                progressBar.setCancelable(false)
//
//                viewModel.updateUserProfileById(
//                    userId, nama, nik, umur, alamat, jenisKelamin, tglLahir, imageProfileFile, null
//                )
//                viewModel.updateUserProfileByIdResult.observe(this) { result ->
//                    if (result != null) {
//                        when (result) {
//                            is ResultState.Loading -> progressBar.show()
//                            is ResultState.Error -> {
//                                progressBar.dismiss()
//                                Toast.makeText(
//                                    this@NavDrawerMainActivity, "Gagal diubah", Toast.LENGTH_LONG
//                                ).show()
//                            }
//                            is ResultState.Success -> {
//                                progressBar.dismiss()
//                                imageView.setImageURI(uri)
//                                Toast.makeText(this@NavDrawerMainActivity, "Berhasil diubah", Toast.LENGTH_LONG).show()
//                            }
//                            is ResultState.Unauthorized -> {
//                                viewModel.logout()
//                                val intent = Intent(this@NavDrawerMainActivity, MainActivity::class.java)
//                                intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
//                                startActivity(intent)
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            currentImageBannerUri?.let { uri ->
//                val nama = userProfileWithUserRelation.userProfile?.nama
//                val nik = userProfileWithUserRelation.userProfile?.nik
//                val umur = userProfileWithUserRelation.userProfile?.umur
//                val alamat = userProfileWithUserRelation.userProfile?.alamat
//                val jenisKelamin = userProfileWithUserRelation.userProfile?.jenisKelamin
//                val tglLahir = userProfileWithUserRelation.userProfile?.tglLahir
//                val imageBannerFile = uriToFile(uri, this).reduceFileImage()
//
//                val progressBar = SweetAlertDialog(this@NavDrawerMainActivity, SweetAlertDialog.PROGRESS_TYPE)
//                progressBar.setTitleText(getString(R.string.title_loading))
//                progressBar.setContentText(getString(R.string.description_loading))
//                    .progressHelper.barColor = Color.parseColor("#73D1FA")
//                progressBar.setCancelable(false)
//
//                viewModel.updateUserProfileById(
//                    userId, nama, nik, umur, alamat, jenisKelamin, tglLahir, null, imageBannerFile
//                )
//                viewModel.updateUserProfileByIdResult.observe(this) { result ->
//                    if (result != null) {
//                        when (result) {
//                            is ResultState.Loading -> progressBar.show()
//                            is ResultState.Error -> {
//                                progressBar.dismiss()
//                                Toast.makeText(this@NavDrawerMainActivity, "Gagal diubah", Toast.LENGTH_LONG).show()
//                            }
//                            is ResultState.Success -> {
//                                progressBar.dismiss()
//                                imageView.setImageURI(uri)
//                                Toast.makeText(this@NavDrawerMainActivity, "Berhasil diubah", Toast.LENGTH_LONG).show()
//                            }
//                            is ResultState.Unauthorized -> {
//                                viewModel.logout()
//                                val intent = Intent(this@NavDrawerMainActivity, MainActivity::class.java)
//                                intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
//                                startActivity(intent)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

//    private fun startGalleryBanner() {
//        launcherGalleryBanner.launch(
//            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//        )
//    }

    private fun startGalleryProfile() {
        launcherGalleryProfile.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

//    private fun startCameraBanner() {
//        currentImageBannerUri = getImageUri(this)
//        launcherIntentCameraBanner.launch(currentImageBannerUri)
//    }

    private fun startCameraProfile() {
        currentImageProfileUri = getImageUri(this)
        launcherIntentCameraProfile.launch(currentImageProfileUri)
    }

    private fun showBottomSheetDialog() {
        // Check if the view already has a parent
        _bindingFotoBottomSheetDialog = DialogBottomSheetFotoBinding.inflate(layoutInflater)
        val viewBottomSheetDialog: View = bindingFotoBottomSheetDialog.root

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

        bindingFotoBottomSheetDialog.apply {
            cvCamera.setOnClickListener {
                if (isEditGambarProfile) {
                    startCameraProfile()
                    bottomSheetDialog.dismiss()
                } else {
//                    startCameraBanner()
//                    bottomSheetDialog.dismiss()
                }
            }
            cvGallery.setOnClickListener {
                if (isEditGambarProfile) {
                    startGalleryProfile()
                    bottomSheetDialog.dismiss()
                } else {
//                    startGalleryBanner()
//                    bottomSheetDialog.dismiss()
                }
            }
        }
    }

//    private fun showImageBanner() {
//        currentImageBannerUri?.let { uri ->
//            val headerView = binding.navView.getHeaderView(0)
//            val editBanner = headerView.findViewById<ImageView>(R.id.iv_edit_banner)
//
//            viewModel.getUserProfileWithUserById(userId!!).observeOnce(this) { userProfileWithUserRelation ->
//                updateUserProfileById(userId!!, editBanner, false, userProfileWithUserRelation)
//            }
//        }
//    }

//    private fun showImageProfile() {
//        currentImageProfileUri?.let { uri ->
//            val headerView = binding.navView.getHeaderView(0)
//            val editProfile = headerView.findViewById<ImageView>(R.id.civ_edit_profile)
//
//            viewModel.getUserProfileWithUserById(userId!!).observeOnce(this) { userProfileWithUserRelation ->
//                updateUserProfileById(userId!!, editProfile, true, userProfileWithUserRelation)
//            }
//        }
//    }

    private fun getDataExtra() {
        val getExtraFragment = intent.getStringExtra(EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY)

        if (getExtraFragment == "LoginFragment") {
            userId = intent.getIntExtra(EXTRA_USER_ID_TO_NAV_DRAWER_MAIN_ACTIVITY, 0)
//            Log.d(TAG, "onNavDrawerMainActivity userId from LoginFragment : ${userId}")
            sendDataToNavHomeFragment(userId!!)
            getUserProfileRelationByUserIdFromLocal(userId!!)
        } else if (getExtraFragment == "OpeningFragment") {
            val userModel = intent.getParcelableExtra<UserModel>(EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY)!!
//            Log.d(TAG, "onNavDrawerMainActivity from OpeningActivity : ${userModel}")
            userId = userModel.id.toInt()
            sendDataToNavHomeFragment(userId!!)
            getUserProfileRelationByUserIdFromLocal(userId!!)
        }
    }

    private fun sendDataToNavHomeFragment(userId: Int) {
        val bundle = Bundle().apply {
            putInt(EXTRA_USER_ID_TO_NAV_HOME_FRAGMENT, userId)
        }
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity)
        navController.navigate(R.id.nav_home, bundle)
    }

    private fun getUserProfileRelationByUserIdFromLocal(userId: Int) {
        viewModel.getUserProfileRelationByUserIdFromLocal(userId).observe(this) { userProfileRelation ->
//            Log.d(TAG, "onNavDrawerMainActivity getUserProfileWithUserById() : ${userProfileWithUserRelation.userProfile}")
            if (userProfileRelation != null) {
                getHeaderView(userProfileRelation)
            }
        }
    }

    private fun getUserProfiles() {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getUserProfilesFromApiResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onNavDrawerMainActivity from LoginFragment getUserProfilesResult : ${result.error}")
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onNavDrawerMainActivity from LoginFragment getUserProfilesResult : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@NavDrawerMainActivity, ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
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
                val intent = Intent(this@NavDrawerMainActivity , ContainerMainActivity::class.java)
                intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@NavDrawerMainActivity).toBundle())
            }
            sweetAlertDialog.show()
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getHeaderView(userProfileRelation: UserProfileWithUserRelation?) {
        // Index 0 karena hanya ada satu header
        val headerView = binding.navView.getHeaderView(0)
        val flProfile = headerView.findViewById<FrameLayout>(R.id.fl_profile)
        val civEditProfile = headerView.findViewById<CircleImageView>(R.id.civ_edit_profile)
//        val ivEditBanner = headerView.findViewById<ImageView>(R.id.iv_edit_banner)
        val name = headerView.findViewById<TextView>(R.id.tv_name_nav_view)
        val email = headerView.findViewById<TextView>(R.id.tv_email_nav_view)
        val role = headerView.findViewById<TextView>(R.id.tv_role_nav_view)

        val usersEntity = userProfileRelation?.users
        val userProfileEntity = userProfileRelation?.userProfile

        flProfile.setOnClickListener {
            isEditGambarProfile = true
            showBottomSheetDialog()
        }

//        ivEditBanner.setOnClickListener {
//            isEditGambarProfile = false
//            showBottomSheetDialog()
//        }

        if (userProfileEntity?.gambarProfile != null) {
            Glide.with(this)
                .load(userProfileEntity.gambarProfile)
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

        name.text = userProfileEntity?.nama
        email.text = usersEntity?.email
        role.text = "Anda sebagai ${usersEntity?.role}"
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer_main_activity)
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

    override fun onDestroy() {
        super.onDestroy()
        _bindingFotoBottomSheetDialog = null
    }

    companion object {
        private val TAG = NavDrawerMainActivity::class.java.simpleName
        const val EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY = "extra_fragment_to_nav_drawer_main_activity"
        const val EXTRA_USER_ID_TO_NAV_DRAWER_MAIN_ACTIVITY = "extra_user_id_to_nav_drawer_main_activity"
        const val EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY = "extra_user_model_to_nav_drawer_main_activity"
    }
}