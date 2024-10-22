package com.example.stunting.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.BuildConfig
import com.example.stunting.R
import com.example.stunting.adapter.KonsultasiAdapter
import com.example.stunting.database.DatabaseApp
import com.example.stunting.database.layanan_keluarga.LayananKeluargaDao
import com.example.stunting.database.messages.MessageDao
import com.example.stunting.database.messages.MessageEntity
import com.example.stunting.databinding.ActivityKonsultasiBinding
import com.example.stunting.functions_helper.Functions.getDateTimePrimaryKey
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class KonsultasiActivity : AppCompatActivity() {
    private var _binding: ActivityKonsultasiBinding? = null
    private val binding get() = _binding!!
    private var _messageDao: MessageDao? = null
    private val messageDao get() = _messageDao!!

    val messageList = ArrayList<MessageEntity>()
    var countItem = 0
    var id = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityKonsultasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _messageDao = (application as DatabaseApp).dbMessage.messageDao()

        binding.ivKonsultasi.setOnClickListener {
            val input = binding.etKonsultasi.text.toString()
            // Add record
            addRecord(input, true)

            generativeModel(input)
            setupListOfDataIntoRecyclerView(messageList)
        }

        // Get all items
        getAll(messageDao)
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

    private fun generativeModel(prompt: String) {
        val generativeModel = GenerativeModel(
            // For text-only input, use the gemini-pro model
            modelName = "gemini-1.5-flash",
            // Accessmpt your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = BuildConfig.API_KEY
        )

        lifecycleScope.launch {
            val response = generativeModel.generateContent(prompt).text.toString()
            addRecord(response, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}