package com.example.stunting.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityOpeningBinding

class OpeningActivity : AppCompatActivity() {
    private var _binding: ActivityOpeningBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOpeningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fullScreen()

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this@OpeningActivity, SignUpActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@OpeningActivity).toBundle())
        }

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this@OpeningActivity, LoginActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@OpeningActivity).toBundle())
        }
    }

    private fun fullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}