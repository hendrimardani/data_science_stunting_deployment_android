package com.example.stunting.ui.opening_user_profile_patient_form

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityOpeningUserProfilePatientFormBinding
import com.example.stunting.ui.ViewModelFactory

class OpeningUserProfilePatientFormActivity : AppCompatActivity() {
    private var _binding: ActivityOpeningUserProfilePatientFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<OpeningUserProfilePatientFormViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userPatientId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOpeningUserProfilePatientFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM, 0)

        getUserProfilePatients()

        binding.llBack.visibility = View.INVISIBLE
        binding.llNext.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE
        binding.llBack.setOnClickListener {
            binding.viewFlipper.showPrevious()
            binding.llBack.visibility = View.INVISIBLE
            binding.llNext.visibility = View.VISIBLE
            binding.btnSave.visibility = View.GONE
        }
        binding.llNext.setOnClickListener {
            binding.viewFlipper.showNext()
            binding.llBack.visibility = View.VISIBLE
            binding.llNext.visibility = View.INVISIBLE
//            binding.btnSave.visibility = View.VISIBLE
        }
    }

    private fun getUserProfilePatients() {
    }

    companion object {
        private val TAG = OpeningUserProfilePatientFormActivity::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM = "extra_user_patient_id_to_opening_user_profile_patient_form"
    }
}