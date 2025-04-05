package com.example.stunting.ui.group_chat

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityChatbotBinding
import com.example.stunting.databinding.ActivityGroupChatBinding
import kotlin.math.max

class GroupChatActivity : AppCompatActivity() {
    private var _binding: ActivityGroupChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Supaya tidak ada margin di atas app bar dan dibawah
        setCoordinatorFitsSystemWindows()

        // Toolbar
        setToolBar()
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

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbGroupchat)
        supportActionBar!!.title = getString(R.string.app_neural_network)
        // Change font style text
        binding.tbGroupchat.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.ic_neural_network)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbGroupchat.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
//        _messageDao = null
    }
}