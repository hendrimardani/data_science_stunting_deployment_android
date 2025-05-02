package com.example.stunting.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.adapter.DetailGroupAdapter
import com.example.stunting.databinding.ActivityDetailGroupBinding
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailGroupActivity : AppCompatActivity() {
    private var _binding: ActivityDetailGroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null
    private var groupId: Int? = null
    private val detailGroupAdapter = DetailGroupAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDetailGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent?.getIntExtra(EXTRA_GROUP_ID_TO_DETAIL_GROUP_CHAT, 0)

        getUserGroup()
        getUserGroupRelationByGroupId(groupId)

        binding.swipeRefreshLayout.setOnRefreshListener {
            getUserGroup()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        binding.rvAnggota.apply {
            layoutManager = LinearLayoutManager(this@DetailGroupActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = detailGroupAdapter
        }
    }

    private fun getUserGroup() {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getUserGroup().observe(this) { result ->
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
    private fun getUserGroupRelationByGroupId(groupId: Int?) {
        viewModel.getUserGroupRelationByGroupId(groupId!!).observe(this) { result ->
            val jumlahAnggota = result.size

            result.forEach { item ->
                val userProfile = item.userProfileEntity
                val groups = item.groupsEntity
                val userGroup = item.userGroupEntity

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

                if (groups.gambarProfile != null) {
                    Glide.with(this@DetailGroupActivity)
                        .load(groups.gambarProfile)
                        .into(binding.civEditProfile)
                } else {
                    Glide.with(this@DetailGroupActivity)
                        .load(R.drawable.ic_avatar_group_chat)
                        .into(binding.civEditProfile)
                }
9
                binding.tvNamaGroup.text = groups.namaGroup
                binding.tvDeskripsi.text = groups.deskripsi
                binding.tvJumlahAnggota.text = "${jumlahAnggota} Anggota"
            }

            detailGroupAdapter.submitList(result)
        }
    }




    companion object {
        const val EXTRA_GROUP_ID_TO_DETAIL_GROUP_CHAT = "extra_group_id_to_detail_group_chat"
    }
}