package com.example.stunting.ui.detail_group

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.adapter.DetailGroupAnggotaAdapter
import com.example.stunting.adapter.DetailGroupTambahAnggotaAdapter
import com.example.stunting.adapter.Interface.OnItemInteractionListener
import com.example.stunting.database.with_api.entities.user_profile.UserProfileWithSelection
import com.example.stunting.databinding.ActivityDetailGroupBinding
import com.example.stunting.databinding.DialogCustomTambahAnggotaBinding
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory

class DetailGroupActivity : AppCompatActivity() {
    private var _binding: ActivityDetailGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null
    private var groupId: Int? = null
    private val detailGroupAnggotaAdapter = DetailGroupAnggotaAdapter()
    private var roleValueRadioButton = ""


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

        binding.swipeRefreshLayout.setOnRefreshListener {
            getUserGroups()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.flIconEditBodyGroup.setOnClickListener {

//            val namaGroup = binding.tvNamaGroup.text.toString().trim()
//            val deskripsi = binding.tvDeskripsi.text.toString().trim()

//            updateGroupById(userId, groupId, namaGroup, deskripsi, null, null)
//            private fun updateGroupById(
//                userId: Int, groupId: Int, namaGroup:String?, deskripsi: String?, gambarProfile: File?, gambarBanner: File?
//            ) {
//
//            }
        }

        binding.rvAnggota.apply {
            layoutManager = LinearLayoutManager(
                this@DetailGroupActivity, LinearLayoutManager.VERTICAL, false
            )
            setHasFixedSize(true)
            adapter = detailGroupAnggotaAdapter
        }

        binding.tvTambahAnggota.setOnClickListener { showDialogCustomTambahAnggotaBinding() }
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
        viewModel.getUserGroupRelationByGroupId(groupId!!).observe(this) { result ->
            val jumlahAnggota = result.size

            result.forEach { item ->
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
            detailGroupAnggotaAdapter.submitList(result)
        }
    }

    companion object {
        const val EXTRA_USER_ID_TO_DETAIL_GROUP_CHAT = "extra_user_id_to_detail_group_chat"
        const val EXTRA_GROUP_ID_TO_DETAIL_GROUP_CHAT = "extra_group_id_to_detail_group_chat"
    }
}