package com.example.stunting.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.BuildConfig
import com.example.stunting.R
import com.example.stunting.adapter.KonsultasiAdapter
import com.example.stunting.database.DatabaseApp
import com.example.stunting.database.messages.MessageDao
import com.example.stunting.database.messages.MessageEntity
import com.example.stunting.databinding.ActivityKonsultasiBinding
import com.example.stunting.databinding.DialogInfoKonsultasiBinding
import com.example.stunting.functions_helper.Functions.getDateTimePrimaryKey
import com.example.stunting.functions_helper.Functions.toastInfo
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.QuotaExceededException
import com.google.ai.client.generativeai.type.UnknownException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToastStyle
import kotlin.math.max


class KonsultasiActivity : AppCompatActivity() {
    private var _binding: ActivityKonsultasiBinding? = null
    private val binding get() = _binding!!
    private var _messageDao: MessageDao? = null
    private val messageDao get() = _messageDao!!

    val messageList = ArrayList<MessageEntity>()
    var countItem = 0

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityKonsultasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Supaya tidak ada margin di atas app bar dan dibawah
        setCoordinatorFitsSystemWindows()

        // Toolbar
        setToolBar()

        _messageDao = (application as DatabaseApp).dbMessage.messageDao()

        // isWordAvailable
        wordAvailable()

        try {
            binding.ivKonsultasi.setOnClickListener {
                val input = binding.etKonsultasi.text
                if (input!!.isEmpty()) {
                    toastInfo(
                        this@KonsultasiActivity,
                        getString(R.string.title_input_failed), getString(R.string.description_input_failed),
                        MotionToastStyle.ERROR
                    )
                } else {
                    val progressBar = SweetAlertDialog(this@KonsultasiActivity, SweetAlertDialog.PROGRESS_TYPE)
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
                this@KonsultasiActivity,
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
        binding.etKonsultasi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()){
                    binding.ivKonsultasi.setColorFilter(getColor(R.color.gray))
                } else {
                    binding.ivKonsultasi.setColorFilter(getColor(R.color.blueSecond))
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
        menuInflater.inflate(R.menu.menu_konsultasi, menu)
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
            this@KonsultasiActivity, getString(R.string.title_chat_deleted),
            getString(R.string.description_chat_deleted), MotionToastStyle.SUCCESS
        )
        finish()
    }

    private fun addRecord(input: String, isSent: Boolean) {
        val date = getDateTimePrimaryKey()
        lifecycleScope.launch {
            messageDao.insert(
                MessageEntity(date = date, text = input, isSent = isSent)
            )
        }
    }

    private fun getAll(messageDao: MessageDao) {
        lifecycleScope.launch {
            messageDao.fetchAllMessage().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(messageList: ArrayList<MessageEntity>) {
        if (messageList.isNotEmpty()) {
            val konsultasiAdapter = KonsultasiAdapter(messageList)
            // Count item list
            countItem = messageList.size
            binding.rvKonsultasi.layoutManager = LinearLayoutManager(this)
            binding.rvKonsultasi.adapter = konsultasiAdapter
            // To scrolling automatic when data entered
            binding.rvKonsultasi.smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            binding.rvKonsultasi
                .layoutManager!!.smoothScrollToPosition(binding
                    .rvKonsultasi, null, countItem - 1)

        }
    }

    private fun customeDialogInfo() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogInfoKonsultasiBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.tvOk.setOnClickListener {
            // Close
            customDialog.dismiss()
        }
        // Display dialog
        customDialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun generativeModel(prompt: String) {
        val generativeModel = GenerativeModel(modelName = "gemini-2.0-flash-exp", apiKey = BuildConfig.API_KEY)
        val progressBar = SweetAlertDialog(this@KonsultasiActivity, SweetAlertDialog.PROGRESS_TYPE)
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
                    this@KonsultasiActivity,
                    getString(R.string.title_error_internet_request_api),
                    getString(R.string.description_error_internet_request_api),
                    MotionToastStyle.ERROR
                )
            } catch (e : QuotaExceededException) {
                toastInfo(
                    this@KonsultasiActivity,
                    getString(R.string.title_error_quotaExceeded_request_api),
                    getString(R.string.description_error_quotaExceeded_request_api),
                    MotionToastStyle.ERROR
                )
            }
        }
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbKonsultasi)
        supportActionBar!!.title = getString(R.string.app_neural_network)
        // Change font style text
        binding.tbKonsultasi.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.neural_network)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbKonsultasi.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _messageDao = null
    }
}