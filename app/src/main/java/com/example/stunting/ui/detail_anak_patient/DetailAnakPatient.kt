package com.example.stunting.ui.detail_anak_patient

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityDetailAnakPatientBinding

class DetailAnakPatient : AppCompatActivity() {
    private var _binding: ActivityDetailAnakPatientBinding? = null
    private val binding get() = _binding!!
    private var childrenPatientId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDetailAnakPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        childrenPatientId = intent?.getIntExtra(EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY, 0)

    }

    companion object {
        private val TAG = DetailAnakPatient::class.java.simpleName
        const val EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY = "extra_children_patient_id_to_detail_anak_patient_activity"
    }
}