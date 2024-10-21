package com.example.stunting.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.Messages
import com.example.stunting.R
import com.example.stunting.adapter.AnakAdapter
import com.example.stunting.adapter.KonsultasiAdapter
import com.example.stunting.database.anak.AnakEntity
import com.example.stunting.databinding.ActivityKonsultasiBinding

class KonsultasiActivity : AppCompatActivity() {
    private var _binding: ActivityKonsultasiBinding? = null
    private val binding get() = _binding!!

    val messageList = ArrayList<Messages>()

    var countItem = 0

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

        binding.ivKonsultasi.setOnClickListener {
            messageList.add(Messages(binding.etKonsultasi.text.toString(), true))
            messageList.add(Messages("Hello boss"))
            setupListOfDataIntoRecyclerView(messageList)
        }
    }

    private fun setupListOfDataIntoRecyclerView(messageList: ArrayList<Messages>) {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}