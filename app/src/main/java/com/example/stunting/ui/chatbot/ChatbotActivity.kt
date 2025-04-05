package com.example.stunting.ui.chatbot

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.example.stunting.database.with_api.message_chatbot.MessageChatbotDao
import com.example.stunting.database.with_api.message_chatbot.MessageChatbotsEntity
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

        // Toolbar
        setToolBar()

        _messageDao = (application as DatabaseApp).dbApp.messageChatbotDao()

        // isWordAvailable
        wordAvailable()

        try {
            binding.ivChatbot.setOnClickListener {
                val input = binding.etChatbot.text
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
                        // Add record
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
        // Dialog information
        customeDialogInfo()

        // Get all items
        getAll(messageDao)
    }

    private fun wordAvailable() {
        // Matikan ikon jika tidak ada input
        binding.etChatbot.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()){
                    binding.ivChatbot.setColorFilter(getColor(R.color.gray))
                } else {
                    binding.ivChatbot.setColorFilter(getColor(R.color.white))
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })
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

    private fun addRecord(input: String, isSent: Boolean) {
        val date = getDateTimePrimaryKey()
        lifecycleScope.launch {
            messageDao.insert(
                MessageChatbotsEntity(date = date, text = input, isSent = isSent)
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
            // Count item list
            countItem = messageList.size
            binding.rvChatbot.layoutManager = LinearLayoutManager(this)
            binding.rvChatbot.adapter = chatbotAdapter
            // To scrolling automatic when data entered
            binding.rvChatbot.smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            binding.rvChatbot
                .layoutManager!!.smoothScrollToPosition(binding
                    .rvChatbot, null, countItem - 1)

        }
    }

    private fun customeDialogInfo() {
        val dialogBinding = DialogInfoChatbotBinding.inflate(layoutInflater)
        val customDialog = Dialog(this)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvOk.setOnClickListener {
            // Close
            customDialog.dismiss()
        }
        // Display dialog
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
        // Call object actionBar
        setSupportActionBar(binding.tbChatbot)
        supportActionBar!!.title = getString(R.string.app_neural_network)
        // Change font style text
        binding.tbChatbot.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.ic_neural_network)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbChatbot.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _messageDao = null
    }
}