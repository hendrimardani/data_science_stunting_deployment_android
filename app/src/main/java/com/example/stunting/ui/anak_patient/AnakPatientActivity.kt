package com.example.stunting.ui.anak_patient

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityAnakPatientBinding
import com.example.stunting.databinding.ActivityBumilPatientBinding
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.bumil_patient.BumilPatientActivity.Companion.EXTRA_CATEGORY_SERVICE_ID_TO_BUMIL_PATIENT_ACTIVITY
import com.example.stunting.ui.bumil_patient.BumilPatientActivity.Companion.EXTRA_USER_PATIENT_ID_TO_BUMIL_PATIENT_ACTIVITY
import com.example.stunting.ui.bumil_patient.BumilPatientViewModel
import com.example.stunting.utils.NetworkLiveData
import com.example.stunting.utils.table_view.TableViewAdapter

class AnakPatientActivity : AppCompatActivity() {
    private var _binding: ActivityAnakPatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnakPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var networkLiveData: NetworkLiveData

    private var userPatientId: Int? = null
    private var categryServiceId: Int? = null
    private var tableViewAdapter = TableViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAnakPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_BUMIL_PATIENT_ACTIVITY, 0)
        categryServiceId = intent?.getIntExtra(EXTRA_CATEGORY_SERVICE_ID_TO_BUMIL_PATIENT_ACTIVITY, 0)
    }

    companion object {
        const val EXTRA_USER_PATIENT_ID_TO_ANAK_PATIENT_ACTIVITY = "extra_user_patient_id_to_anak_patient_activity"
        const val EXTRA_CATEGORY_SERVICE_ID_TO_ANAK_PATIENT_ACTIVITY = "extra_category_service_id_to_anak_patient_activity"
    }
}