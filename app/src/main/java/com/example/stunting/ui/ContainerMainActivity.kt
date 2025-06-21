package com.example.stunting.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.stunting.R
import com.example.stunting.databinding.ActivityContainerMainBinding
import com.example.stunting.ui.login.LoginFragment
import com.example.stunting.ui.opening.OpeningFragment

class ContainerMainActivity : AppCompatActivity() {
    private var _binding: ActivityContainerMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityContainerMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val fragmentName = intent.getStringExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY)
            val fragment = when (fragmentName) {
                "LoginFragment" -> LoginFragment()
                else -> OpeningFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_fragment, fragment)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY = "extra_fragment_to_container_main_activity"
    }
}