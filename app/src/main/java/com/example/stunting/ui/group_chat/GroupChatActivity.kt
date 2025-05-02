package com.example.stunting.ui.group_chat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.GroupChatAdapter
import com.example.stunting.databinding.ActivityGroupChatBinding
import com.example.stunting.ui.DetailGroupActivity
import com.example.stunting.ui.DetailGroupActivity.Companion.EXTRA_GROUP_ID_TO_DETAIL_GROUP_CHAT
import com.example.stunting.ui.DetailGroupActivity.Companion.EXTRA_USER_ID_TO_DETAIL_GROUP_CHAT
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory
import kotlin.math.max

class GroupChatActivity : AppCompatActivity() {
    private var _binding: ActivityGroupChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null
    private var groupId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent?.getIntExtra(EXTRA_USER_ID_TO_GROUP_CHAT, 0)
        groupId = intent?.getIntExtra(EXTRA_GROUP_ID_TO_GROUP_CHAT, 0)

        val groupChatAdapter = GroupChatAdapter(userId!!)

        getUserGroupRelationByGroupId(groupId)
        getMessages()
        getMessageByGroupId(groupId!!, groupChatAdapter)

        textWatcher()

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(
                this@GroupChatActivity, LinearLayoutManager.VERTICAL, false
            )
            setHasFixedSize(true)
            adapter = groupChatAdapter
        }

        binding.toolbar.setOnClickListener {
            val intent = Intent(this, DetailGroupActivity::class.java)
            intent.putExtra(EXTRA_USER_ID_TO_DETAIL_GROUP_CHAT, userId)
            intent.putExtra(EXTRA_GROUP_ID_TO_DETAIL_GROUP_CHAT, groupId)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnSend.setOnClickListener {
            val isiPesan = binding.tietMessage.text.toString().trim()
            addMessage(userId!!, groupId!!, groupChatAdapter, isiPesan)
        }

        // Supaya tidak ada margin di atas app bar dan dibawah
        setCoordinatorFitsSystemWindows()
    }

    private fun addMessage(userId: Int, groupId: Int, groupChatAdapter: GroupChatAdapter, isiPesan: String) {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.addMessage(userId, groupId, isiPesan).observe(this@GroupChatActivity) { result ->
            if (result != null) {
                when (result) {

                    is ResultState.Loading -> { progressBar.show() }
                    is ResultState.Error ->{
                        progressBar.dismiss()
//                            Log.d(TAG, "onGroupChatActivity getMessageByGroupId Error  : ${result.error}")
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                            Log.d(TAG, "onGroupChatActivity addMessage Success : ${result.data}")
                        getMessageByGroupId(groupId!!, groupChatAdapter)
                        getMessages()
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getUserGroupRelationByGroupId(groupId: Int?) {
        viewModel.getUserGroupRelationByGroupId(groupId!!).observe(this) { result ->
            result.forEach { item ->
                val gambarProfile = item.groupsEntity.gambarProfile
                val namaGroup =  item.groupsEntity.namaGroup
                setToolBar(gambarProfile, namaGroup)
            }
        }
    }

    private fun getMessages() {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getMessages().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onGroupChatActivity getMessages Error  : ${result.error}")
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onGroupChatActivity getMessages Success : ${result.data}")
                        if (result.data.isNotEmpty()) {
                            binding.rvMessages.scrollToPosition(result.data.size - 1)
                            binding.rvMessages.layoutManager!!.smoothScrollToPosition(
                                binding.rvMessages, null, result.data.size - 1
                            )
                        }
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun getMessageByGroupId(groupId: Int, groupChatAdapter: GroupChatAdapter) {
        viewModel.getMessageRelationByGroupId(groupId).observe(this) { result ->
//            Log.d(TAG, "onGroupChatActivity getMessageRelationByGroupId suceess ${result.size}")
            groupChatAdapter.submitList(result)
            if (result.isNotEmpty()) {
                binding.rvMessages.scrollToPosition(result.size - 1)
                binding.rvMessages.layoutManager!!.smoothScrollToPosition(
                    binding.rvMessages, null, result.size - 1
                )
            }
            binding.tietMessage.text?.clear()
        }
    }

    private fun textWatcher() {
        val textWatcher = object : TextWatcher {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun afterTextChanged(s: Editable?) {
                val message = binding.tietMessage.text.toString().trim()

                binding.btnSend.isEnabled = message.isNotEmpty()

                if (binding.btnSend.isEnabled) {
                    binding.btnSend.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@GroupChatActivity, R.color.white)
                    )
                    binding.frameLayoutBtnSend.background = getDrawable(R.drawable.shape_circle)
                } else {
                    binding.btnSend.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@GroupChatActivity, R.color.light_gray)
                    )
                    binding.frameLayoutBtnSend.background = getDrawable(R.drawable.shape_circle_disabled)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.tietMessage.addTextChangedListener(textWatcher)
    }

    private fun setToolBar(gambarProfile: String?, nama: String?) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = nama
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        if (gambarProfile != null) {
            Glide.with(this)
                .asDrawable()
                .load(gambarProfile)
                .override(100, 100)
                .circleCrop()
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.toolbar.logo = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {  }
                })
        } else {
            Glide.with(this)
                .load(getDrawable(R.drawable.ic_avatar_group_chat))
                .override(100, 100)
                .circleCrop()
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.toolbar.logo = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {  }
                })
        }
    }

    private fun setCoordinatorFitsSystemWindows() {
        // Fungsi ini untuk menghapus margin dibagian atas dan bawah ketika menerapkan CoordinatorLayout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View>(R.id.main)
        ) { view: View, insets: WindowInsetsCompat ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Atur padding untuk menyesuaikan keyboard (IME) dan status bar
            view.setPadding(
                0, 0, 0,
                max(imeInsets.bottom.toDouble(), systemBarsInsets.bottom.toDouble()).toInt()
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        // Supaya ketika ditekan tombol back muncul lagi data dari database
        getMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = GroupChatActivity::class.java.simpleName
        const val EXTRA_USER_ID_TO_GROUP_CHAT = "extra_user_id_to_group_chat"
        const val EXTRA_GROUP_ID_TO_GROUP_CHAT = "extra_group_id_to_group_chat"
    }
}