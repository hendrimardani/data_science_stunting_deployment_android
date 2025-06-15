package com.example.stunting.ui.opening_user_profile_patient_ready

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.stunting.R
import com.example.stunting.databinding.ActivityOpeningUserProfilePatientReadyBinding
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_TAP_TARGET_VIEW_ACTIVED_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OpeningUserProfilePatientReadyActivity : AppCompatActivity() {
    private var _binding: ActivityOpeningUserProfilePatientReadyBinding? = null
    private val binding get() = _binding!!

    private var userPatientId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOpeningUserProfilePatientReadyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_READY, 0)
        startTypeWriterEffect()
    }

    private fun startTypeWriterEffect() {
        val textToType = "SEDANG MENYIAPKAN..."
        binding.tvSedangMenyiapkan.text = ""
        binding.lavRocket.visibility = View.VISIBLE
        binding.tvSedangMenyiapkan.visibility = View.VISIBLE
        binding.lavSucess.visibility = View.GONE

        lifecycleScope.launch {
            for (char in textToType) {
                binding.tvSedangMenyiapkan.append(char.toString())
                delay(250L)
            }
            binding.lavRocket.visibility = View.GONE
            binding.tvSedangMenyiapkan.visibility = View.GONE
            binding.lavSucess.visibility = View.VISIBLE
            delay(1200L)
            val intent = Intent(this@OpeningUserProfilePatientReadyActivity, NavDrawerMainActivityPatient::class.java)
            intent.putExtra(EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, TAG)
            intent.putExtra(EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, userPatientId)
            intent.putExtra(EXTRA_TAP_TARGET_VIEW_ACTIVED_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, true)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@OpeningUserProfilePatientReadyActivity).toBundle())
            finish()
        }
    }

    companion object {
        private val TAG = OpeningUserProfilePatientReadyActivity::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_READY = "extra_user_patient_id_to_opening_user_profile_patient_ready"
    }
}