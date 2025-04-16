package com.example.stunting.ui.group_chat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.GroupChatAdapter
import com.example.stunting.databinding.ActivityGroupChatBinding
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
    private var activityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent?.getIntExtra(EXTRA_USER_ID_TO_GROUP_CHAT, 0)
        groupId = intent?.getIntExtra(EXTRA_GROUP_ID_TO_GROUP_CHAT, 0)
        activityName = intent?.getStringExtra(EXTRA_NAMA_TO_GROUP_CHAT)
//        Log.d(TAG, "onGroupChatActivity group id : ${getExtraGroupId}")

        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            progressBar.setTitleText(getString(R.string.title_loading))
            progressBar.setContentText(getString(R.string.description_loading))
                .progressHelper.barColor = Color.parseColor("#73D1FA")
            progressBar.setCancelable(false)

        viewModel.resultMessages.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error ->{
                        progressBar.dismiss()
                        Log.d(TAG, "onGroupChatActivity getMessages Error  : ${result.error}")
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
                        Log.d(TAG, "onGroupChatActivity getMessages Success : ${result.data}")
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

        val groupChatAdapter = GroupChatAdapter(userId!!)
        textWatcher()

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@GroupChatActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = groupChatAdapter
        }

        binding.btnSend.setOnClickListener {
            val isiPesan = binding.tietMessage.text.toString().trim()
            viewModel.addMessage(userId!!, groupId!!, isiPesan).observe(this@GroupChatActivity) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> { }
                        is ResultState.Error ->{
//                        Log.d(TAG, "onGroupChatActivity getMessageByGroupId Error  : ${result.error}")
                        }
                        is ResultState.Success -> {
                        Log.d(TAG, "onGroupChatActivity addMessage Success : ${result.data}")
                            getMessageByGroupId(groupId!!, groupChatAdapter)
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

        // Supaya tidak ada margin di atas app bar dan dibawah
        setCoordinatorFitsSystemWindows()

        setToolBar(activityName!!)
    }

    private fun getMessageByGroupId(groupId: Int, groupChatAdapter: GroupChatAdapter) {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getMessageRelationByGroupId(groupId).observe(this) { result ->
            Log.d(TAG, "onGroupChatActivity getMessageRelationByGroupId suceess ${result.size}")
            result.forEach { item ->
                Log.d(TAG, "onGroupChatActivity getMessageRelationByGroupId suceess ${item}")
            }
            groupChatAdapter.submitList(result)
        }
    }

    private fun setToolBar(nama: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = nama
        binding.toolbar.setTitleTextAppearance(this, R.style.Theme_Stunting)
        supportActionBar!!.setIcon(R.drawable.ic_group_50)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = GroupChatActivity::class.java.simpleName
        const val EXTRA_USER_ID_TO_GROUP_CHAT = "extra_user_id_to_group_chat"
        const val EXTRA_GROUP_ID_TO_GROUP_CHAT = "extra_group_id_to_group_chat"
        const val EXTRA_NAMA_TO_GROUP_CHAT = "extra_nama_to_group_chat"
    }
}