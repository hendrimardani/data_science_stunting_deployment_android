package com.example.stunting.ui.chatbot

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.BuildConfig
import com.example.stunting.R
import com.example.stunting.adapter.ChatbotAdapter
import com.example.stunting.database.no_api.DatabaseApp
import com.example.stunting.database.with_api.entities.message_chatbot.MessageChatbotDao
import com.example.stunting.database.with_api.entities.message_chatbot.MessageChatbotsEntity
import com.example.stunting.databinding.ActivityChatbotBinding
import com.example.stunting.databinding.DialogInfoChatbotBinding
import com.example.stunting.functions_helper.Functions.getDateTimePrimaryKey
import com.example.stunting.functions_helper.Functions.toastInfo
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.QuotaExceededException
import com.google.ai.client.generativeai.type.UnknownException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToastStyle
import kotlin.math.max


class ChatbotActivity : AppCompatActivity() {
    private var _binding: ActivityChatbotBinding? = null
    private val binding get() = _binding!!
    private var _messageDao: MessageChatbotDao? = null
    private val messageDao get() = _messageDao!!

    val messageList = ArrayList<MessageChatbotsEntity>()
    var countItem = 0

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Supaya tidak ada margin di atas app bar dan dibawah
        setCoordinatorFitsSystemWindows()

        setToolBar()

        _messageDao = (application as DatabaseApp).dbApp.messageChatbotDao()

        textWatcher()
        try {
            binding.btnSend.setOnClickListener {
                val input = binding.tietMessage.text
                if (input!!.isEmpty()) {
                    toastInfo(
                        this@ChatbotActivity,
                        getString(R.string.title_input_failed), getString(R.string.description_input_failed),
                        MotionToastStyle.ERROR
                    )
                } else {
                    val progressBar = SweetAlertDialog(this@ChatbotActivity, SweetAlertDialog.PROGRESS_TYPE)
                    progressBar.setTitleText(getString(R.string.title_loading))
                    progressBar.setContentText(getString(R.string.description_loading))
                        .progressHelper.barColor = Color.parseColor("#73D1FA")
                    progressBar.setCancelable(false)
                    progressBar.show()

                    lifecycleScope.launch {
                        delay(1000)
                        addRecord(input.toString(), true)
                        generativeModel(input.toString())

                        input.clear()
                        progressBar.dismiss()

                        setupListOfDataIntoRecyclerView(messageList)
                    }
                }
            }
        } catch (e: SQLiteConstraintException) {
            toastInfo(
                this@ChatbotActivity,
                getString(R.string.title_error_internet_request_api),
                getString(R.string.description_error_internet_request_api),
                MotionToastStyle.ERROR
            )
        }
        dialogCustomInfo()

        getAll(messageDao)
    }

    private fun textWatcher() {
        val textWatcher = object : TextWatcher {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun afterTextChanged(s: Editable?) {
                val message = binding.tietMessage.text.toString().trim()

                binding.btnSend.isEnabled = message.isNotEmpty()

                if (binding.btnSend.isEnabled) {
                    binding.btnSend.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ChatbotActivity, R.color.white)
                    )
                    binding.frameLayoutBtnSend.background = getDrawable(R.drawable.shape_circle)
                } else {
                    binding.btnSend.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ChatbotActivity, R.color.light_gray)
                    )
                    binding.frameLayoutBtnSend.background = getDrawable(R.drawable.shape_circle_disabled)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.tietMessage.addTextChangedListener(textWatcher)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chatbot, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_konsultasi_delete_chat -> deleteChat()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteChat() {
        lifecycleScope.launch {
            messageDao.deleteAll()
        }
        toastInfo(
            this@ChatbotActivity, getString(R.string.title_chat_deleted),
            getString(R.string.description_chat_deleted), MotionToastStyle.SUCCESS
        )
        finish()
    }

    private fun addRecord(input: String, isSender: Boolean) {
        val date = getDateTimePrimaryKey()
        lifecycleScope.launch {
            messageDao.insert(
                MessageChatbotsEntity(date = date, text = input, isSender = isSender)
            )
        }
    }

    private fun getAll(messageDao: MessageChatbotDao) {
        lifecycleScope.launch {
            messageDao.fetchAllMessage().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(messageList: ArrayList<MessageChatbotsEntity>) {
        if (messageList.isNotEmpty()) {
            val chatbotAdapter = ChatbotAdapter(messageList)

            countItem = messageList.size
            binding.rvMessages.layoutManager = LinearLayoutManager(this)
            binding.rvMessages.adapter = chatbotAdapter
            binding.rvMessages.smoothScrollToPosition(countItem - 1)
            binding.rvMessages
                .layoutManager!!.smoothScrollToPosition(binding
                    .rvMessages, null, countItem - 1)

        }
    }

    private fun dialogCustomInfo() {
        val dialogBinding = DialogInfoChatbotBinding.inflate(layoutInflater)
        val customDialog = Dialog(this)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvOk.setOnClickListener { customDialog.dismiss() }
        customDialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun generativeModel(prompt: String) {
        val generativeModel = GenerativeModel(modelName = "gemini-2.0-flash-exp", apiKey = BuildConfig.API_CHATBOT)
        val progressBar = SweetAlertDialog(this@ChatbotActivity, SweetAlertDialog.PROGRESS_TYPE)
            progressBar.setTitleText(getString(R.string.title_loading))
            progressBar.setContentText(getString(R.string.description_loading))
                .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.show()
        lifecycleScope.launch {
            delay(1000)
            try {
                val response = generativeModel.generateContent(prompt).text.toString()
                addRecord(response, false)
                progressBar.dismiss()
            } catch (e: UnknownException) {
                toastInfo(
                    this@ChatbotActivity,
                    getString(R.string.title_error_internet_request_api),
                    getString(R.string.description_error_internet_request_api),
                    MotionToastStyle.ERROR
                )
            } catch (e : QuotaExceededException) {
                toastInfo(
                    this@ChatbotActivity,
                    getString(R.string.title_error_quotaExceeded_request_api),
                    getString(R.string.description_error_quotaExceeded_request_api),
                    MotionToastStyle.ERROR
                )
            }
        }
    }


    private fun setToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = getString(R.string.app_neural_network)
        binding.toolbar.setTitleTextAppearance(this, R.style.Theme_Stunting)
        supportActionBar!!.setIcon(R.drawable.ic_neural_network)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setCoordinatorFitsSystemWindows() {
        // Fungsi ini untuk menghapus margin dibagian atas dan bawah ketika menerapkan CoordinatorLayout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
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
        _messageDao = null
    }
}