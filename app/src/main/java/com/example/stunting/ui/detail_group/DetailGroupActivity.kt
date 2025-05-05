package com.example.stunting.ui.detail_group

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.stunting.MyDeskripsiGroupEditText.Companion.MAX_CHARACTER_DESKRIPSI_GROUP
import com.example.stunting.MyNamaGroupEditText.Companion.MAX_CHARACTER_NAMA_GROUP
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.adapter.DetailGroupAnggotaAdapter
import com.example.stunting.adapter.DetailGroupTambahAnggotaAdapter
import com.example.stunting.adapter.Interface.OnItemInteractionListener
import com.example.stunting.database.with_api.entities.groups.GroupsEntity
import com.example.stunting.database.with_api.entities.user_profile.UserProfileWithSelection
import com.example.stunting.databinding.ActivityDetailGroupBinding
import com.example.stunting.databinding.DialogBottomSheetFotoBinding
import com.example.stunting.databinding.DialogCustomEditGroupBinding
import com.example.stunting.databinding.DialogCustomTambahAnggotaBinding
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.utils.Functions.getImageUri
import com.example.stunting.utils.Functions.reduceFileImage
import com.example.stunting.utils.Functions.uriToFile
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File

class DetailGroupActivity : AppCompatActivity() {
    private var _binding: ActivityDetailGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null
    private var groupId: Int? = null
    private var currentImageProfileUri: Uri? = null
    private var currentImageBannerUri: Uri? = null

    private val detailGroupAnggotaAdapter = DetailGroupAnggotaAdapter()
    private var roleValueRadioButton = ""
    private var isEditGambarProfile: Boolean = false

    private var _bindingFotoBottomSheetDialog: DialogBottomSheetFotoBinding? = null
    private val bindingFotoBottomSheetDialog get() = _bindingFotoBottomSheetDialog!!

    private val launcherIntentCameraProfile = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImageProfile()
        } else {
            currentImageProfileUri = null
        }
    }

    private val launcherIntentCameraBanner = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImageBanner()
        } else {
            currentImageBannerUri = null
        }
    }

    private val launcherGalleryProfile = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentImageProfileUri = uri
            showImageProfile()
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_LONG).show()
        }
    }

    private val launcherGalleryBanner = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentImageBannerUri = uri
            showImageBanner()
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDetailGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent?.getIntExtra(EXTRA_USER_ID_TO_DETAIL_GROUP_CHAT, 0)
        groupId = intent?.getIntExtra(EXTRA_GROUP_ID_TO_DETAIL_GROUP_CHAT, 0)

        getUserGroups()
        getUserGroupRelationByGroupId(groupId!!)
        getUserGroupRelationByUserIdGroupId(userId!!, groupId!!)

        binding.flEditProfile.setOnClickListener {
            isEditGambarProfile = true
            showBottomSheetDialog()
        }

        binding.ivEditBanner.setOnClickListener {
            isEditGambarProfile = false
            showBottomSheetDialog()
        }

        binding.flIconEditBodyGroup.setOnClickListener { showDialogCustomEditGroupBinding(groupId!!, userId!!) }

        binding.rvAnggota.apply {
            layoutManager = LinearLayoutManager(
                this@DetailGroupActivity, LinearLayoutManager.VERTICAL, false
            )
            setHasFixedSize(true)
            adapter = detailGroupAnggotaAdapter
        }

        binding.tvTambahAnggota.setOnClickListener { showDialogCustomTambahAnggotaBinding() }
    }

    private fun startGalleryBanner() {
        launcherGalleryBanner.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun startGalleryProfile() {
        launcherGalleryProfile.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun startCameraBanner() {
        currentImageBannerUri = getImageUri(this)
        launcherIntentCameraBanner.launch(currentImageBannerUri)
    }

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
                    startCameraBanner()
                    bottomSheetDialog.dismiss()
                }
            }
            cvGallery.setOnClickListener {
                if (isEditGambarProfile) {
                    startGalleryProfile()
                    bottomSheetDialog.dismiss()
                } else {
                    startGalleryBanner()
                    bottomSheetDialog.dismiss()
                }
            }
        }
    }

    private fun prepareUpdateGroupById(
        groupId: Int, userId: Int, imageView: ImageView, uri: Uri, isEditGambarProfile: Boolean, groupsEntity: GroupsEntity
    ) {
        if (isEditGambarProfile) {
            val namaGroup = groupsEntity.namaGroup
            val deskripsi = groupsEntity.deskripsi
            val imageProfileFile = uriToFile(uri, this).reduceFileImage()

            val progressBar = SweetAlertDialog(this@DetailGroupActivity, SweetAlertDialog.PROGRESS_TYPE)
            progressBar.setTitleText(getString(R.string.title_loading))
            progressBar.setContentText(getString(R.string.description_loading))
                .progressHelper.barColor = Color.parseColor("#73D1FA")
            progressBar.setCancelable(false)

            viewModel.updateGroupById(
                groupId, userId, namaGroup, deskripsi, imageProfileFile, null
            )
            viewModel.updateGroupByIdResult.observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
                        Toast.makeText(
                            this@DetailGroupActivity, "Gagal diubah", Toast.LENGTH_LONG
                        ).show()
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
                        imageView.setImageURI(uri)
                        Toast.makeText(this@DetailGroupActivity, "Berhasil diubah", Toast.LENGTH_LONG).show()
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@DetailGroupActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        } else {
            val namaGroup = groupsEntity.namaGroup
            val deskripsi = groupsEntity.deskripsi
            val imageBannerFile = uriToFile(uri, this).reduceFileImage()

            val progressBar = SweetAlertDialog(this@DetailGroupActivity, SweetAlertDialog.PROGRESS_TYPE)
            progressBar.setTitleText(getString(R.string.title_loading))
            progressBar.setContentText(getString(R.string.description_loading))
                .progressHelper.barColor = Color.parseColor("#73D1FA")
            progressBar.setCancelable(false)

            viewModel.updateGroupById(
                groupId, userId, namaGroup, deskripsi, null, imageBannerFile
            )
            viewModel.updateGroupByIdResult.observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
                        Toast.makeText(
                            this@DetailGroupActivity, "Gagal diubah", Toast.LENGTH_LONG
                        ).show()
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
                        imageView.setImageURI(uri)
                        Toast.makeText(this@DetailGroupActivity, "Berhasil diubah", Toast.LENGTH_LONG).show()
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@DetailGroupActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showImageBanner() {
        currentImageBannerUri?.let { uri ->
            viewModel.getUserGroupRelationByGroupId(groupId!!).observe(this) { userGroupRelation ->
                userGroupRelation.forEach { item ->
                    val groups = item.groupsEntity
                    prepareUpdateGroupById(groupId!!, userId!!, binding.ivEditBanner, uri, false, groups)
                }
            }
        }
    }

    private fun showImageProfile() {
        currentImageProfileUri?.let { uri ->
            viewModel.getUserGroupRelationByGroupId(groupId!!).observe(this) { userGroupRelation ->
                userGroupRelation.forEach { item ->
                    val groups = item.groupsEntity
                    prepareUpdateGroupById(groupId!!, userId!!, binding.civEditProfile, uri,true, groups)
                }
            }
        }
    }

    private fun textWatcherDialogCustomEditGroup(view: DialogCustomEditGroupBinding) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val namaGroup = view.tietNamaGroup.text.toString()
                val deskripsiGroup = view.tietDeskripsiGroup.text.toString()

                val isNamaGroupValid = namaGroup.length >= MAX_CHARACTER_NAMA_GROUP
                val isDeskripsiGroupValid = deskripsiGroup.length >= MAX_CHARACTER_DESKRIPSI_GROUP

                view.btnEdit.isEnabled = namaGroup.isNotEmpty() && deskripsiGroup.isNotEmpty() &&
                        !isNamaGroupValid && !isDeskripsiGroupValid

                if (view.btnEdit.isEnabled == true) {
                    view.btnEdit.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(this@DetailGroupActivity, R.color.blueSecond)
                    )
                } else {
                    view.btnEdit.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(this@DetailGroupActivity, R.color.buttonDisabledColor)
                    )
                    view.btnEdit.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@DetailGroupActivity, R.color.white)
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        view.tietNamaGroup.addTextChangedListener(textWatcher)
        view.tietDeskripsiGroup.addTextChangedListener(textWatcher)
    }

    private fun showDialogCustomEditGroupBinding(groupId: Int, userId: Int) {
        val view = DialogCustomEditGroupBinding.inflate(layoutInflater)
        val viewDialog = Dialog(this)

        viewDialog.setContentView(view.root)
        viewDialog.setCanceledOnTouchOutside(false)
        viewDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        textWatcherDialogCustomEditGroup(view)

        view.btnEdit.setOnClickListener {
            val namaGroup = view.tietNamaGroup.text.toString().trim()
            val deskripsi = view.tietDeskripsiGroup.text.toString().trim()

            // Ubah group tanpa gambar
            updateGroupByIdNoGambar(
                groupId, userId, namaGroup, deskripsi, null, null, view, viewDialog
            )
        }
        view.btnCancel.setOnClickListener { viewDialog.dismiss() }
        viewDialog.show()
    }

    private fun updateGroupByIdNoGambar(
        groupId: Int, userId: Int, namaGroup: String?, deskripsi: String?,
        gambarProfile: File?, gambarBanner: File?, view: DialogCustomEditGroupBinding, viewDialog: Dialog
    ) {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.updateGroupById(
            groupId, userId, namaGroup, deskripsi, gambarProfile, gambarBanner
        )
        viewModel.updateGroupByIdResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> progressBar.dismiss()
                    is ResultState.Success -> {
                        progressBar.dismiss()
                        viewDialog.dismiss()

                        val message = result.data?.message.toString()
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        Toast.makeText(this@DetailGroupActivity, "Berhasil diubah", Toast.LENGTH_LONG).show()

                        // Update kembali dari database
                        getUserGroups()
                        getUserGroupRelationByGroupId(groupId)

                        view.tietNamaGroup.text?.clear()
                        view.tietDeskripsiGroup.text?.clear()
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@DetailGroupActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getUserGroupRelationByUserIdGroupId(userId: Int, groupId: Int) {
        viewModel.getUserGroupRelationByUserIdGroupId(userId, groupId).observe(this) { result ->
            if (result?.userGroupEntity != null) {
                val userGroup = result.userGroupEntity
                if (userGroup.role == "admin") {
                    binding.flIconEditProfile.visibility = View.VISIBLE
                    binding.ivIconEditProfile.visibility = View.VISIBLE
                    binding.flIconEditBanner.visibility = View.VISIBLE
                    binding.ivIconEditBanner.visibility = View.VISIBLE
                    binding.tvTambahAnggota.visibility = View.VISIBLE
                } else {
                    binding.flIconEditProfile.visibility = View.GONE
                    binding.ivIconEditProfile.visibility = View.GONE
                    binding.flIconEditBanner.visibility = View.GONE
                    binding.ivIconEditBanner.visibility = View.GONE
                    binding.tvTambahAnggota.visibility = View.GONE
                }
            }
        }
    }

    private fun getUserProfilesFromDatabase(detailGroupTambahAnggotaAdapter: DetailGroupTambahAnggotaAdapter) {
        viewModel.getUserProfilesFromDatabase().observe(this) { result ->
            // Convert ke UserProfileWithSelection
            val listWithSelection = result.map {
                UserProfileWithSelection(userProfileWithUserRelation = it)
            }
            detailGroupTambahAnggotaAdapter.submitList(listWithSelection)
        }
    }

    private fun showDialogCustomTambahAnggotaBinding() {
        val view = DialogCustomTambahAnggotaBinding.inflate(layoutInflater)
        val viewDialog = Dialog(this)

        viewDialog.setContentView(view.root)
        viewDialog.setCanceledOnTouchOutside(false)
        viewDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        val detailGroupTambahAnggotaAdapter = DetailGroupTambahAnggotaAdapter(object : OnItemInteractionListener {
            override fun onTextChangedWhileSelected(isAnyItemSelected: Boolean) {
                if (isAnyItemSelected) {
                    view.btnAdd.isEnabled = true
                    view.btnAdd.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(this@DetailGroupActivity, R.color.blueSecond)
                    )
                } else {
                    view.btnAdd.isEnabled = false
                    view.btnAdd.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(this@DetailGroupActivity, R.color.buttonDisabledColor)
                    )
                    view.btnAdd.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@DetailGroupActivity, R.color.white)
                    )
                }
            }
        })

        view.rvAnggota.apply {
            layoutManager = LinearLayoutManager(this@DetailGroupActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = detailGroupTambahAnggotaAdapter
        }

        getUserProfilesFromDatabase(detailGroupTambahAnggotaAdapter)

        view.btnAdd.setOnClickListener {
            val roleSelectedId = view.rgRole.checkedRadioButtonId
            if (roleSelectedId != -1) {
                val selectedRadioButton = viewDialog.findViewById<RadioButton>(roleSelectedId)
                roleValueRadioButton = selectedRadioButton.text.toString()
            }

            val idUserList = detailGroupTambahAnggotaAdapter.getSelectedIds()
            addUserByGroupId(groupId!!, idUserList, roleValueRadioButton)
            viewDialog.dismiss()
        }

        view.btnCancel.setOnClickListener { viewDialog.dismiss() }
        viewDialog.show()
    }

    private fun addUserByGroupId(groupId: Int, idUserList: List<Int>, role: String) {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.addUserByGroupId(groupId, idUserList, role).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
                        Toast.makeText(this, result.error, Toast.LENGTH_LONG).show()
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
                        Toast.makeText(this, "${idUserList.size} anggota berhasil ditambahkan", Toast.LENGTH_LONG).show()
                        getUserGroups()
                        getUserGroupRelationByGroupId(groupId)
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@DetailGroupActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getUserGroups() {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getUserGroups().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> progressBar.dismiss()
                    is ResultState.Success -> progressBar.dismiss()
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@DetailGroupActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUserGroupRelationByGroupId(groupId: Int) {
        viewModel.getUserGroupRelationByGroupId(groupId!!).observe(this) { userGroupRelation ->
            val jumlahAnggota = userGroupRelation.size

            userGroupRelation.forEach { item ->
                val groups = item.groupsEntity

                if (groups.gambarProfile != null) {
                    Glide.with(this@DetailGroupActivity)
                        .load(groups.gambarProfile)
                        .into(binding.civEditProfile)
                } else {
                    Glide.with(this@DetailGroupActivity)
                        .load(R.drawable.ic_avatar_group_chat)
                        .into(binding.civEditProfile)
                }

                val urlBanner = groups.gambarBanner
                Glide.with(this)
                    .load(urlBanner)
                    .into(binding.ivEditBanner)

                binding.tvNamaGroup.text = groups.namaGroup
                binding.tvDeskripsi.text = groups.deskripsi
                binding.tvJumlahAnggota.text = "${jumlahAnggota} Anggota"
            }
            detailGroupAnggotaAdapter.submitList(userGroupRelation)
        }
    }

    companion object {
        const val EXTRA_USER_ID_TO_DETAIL_GROUP_CHAT = "extra_user_id_to_detail_group_chat"
        const val EXTRA_GROUP_ID_TO_DETAIL_GROUP_CHAT = "extra_group_id_to_detail_group_chat"
    }
}