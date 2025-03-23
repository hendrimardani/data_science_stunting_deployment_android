package com.example.stunting.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        animationStart()

    }

    private fun animationStart() {
        ObjectAnimator.ofFloat(binding.iv2, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val view1TranslationX = ObjectAnimator.ofFloat(binding.v1, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val view1TranslationY = ObjectAnimator.ofFloat(binding.v1, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        // Jalankan X dan Y secara bersamaan
        AnimatorSet().apply {
            playTogether(view1TranslationX, view1TranslationY)
            start()
        }

        val view2TranslationX = ObjectAnimator.ofFloat(binding.v2, View.TRANSLATION_X, -30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val view2TranslationY = ObjectAnimator.ofFloat(binding.v2, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        // Jalankan X dan Y secara bersamaan
        AnimatorSet().apply {
            playTogether(view2TranslationX, view2TranslationY)
            start()
        }

        val view3TranslationX = ObjectAnimator.ofFloat(binding.v3, View.TRANSLATION_X, 30f, -30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val view3TranslationY = ObjectAnimator.ofFloat(binding.v3, View.TRANSLATION_Y, 30f, -30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        // Jalankan X dan Y secara bersamaan
        AnimatorSet().apply {
            playTogether(view3TranslationX, view3TranslationY)
            start()
        }

        val view4TranslationX = ObjectAnimator.ofFloat(binding.v4, View.TRANSLATION_X, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val view4TranslationY = ObjectAnimator.ofFloat(binding.v4, View.TRANSLATION_Y, 30f, -30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        // Jalankan X dan Y secara bersamaan
        AnimatorSet().apply {
            playTogether(view4TranslationX, view4TranslationY)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}