package com.example.stunting.ui.opening_user_profile_patient

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.ui.opening_user_profile_patient_form.OpeningUserProfilePatientFormActivity
import com.example.stunting.ui.opening_user_profile_patient_form.OpeningUserProfilePatientFormActivity.Companion.EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM
import com.ncorti.slidetoact.SlideToActView


class OpeningUserProfilePatientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opening_user_profile_patient)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userPatientId = intent?.getIntExtra(
            EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT, 0)
        val slideToActView = findViewById<SlideToActView>(R.id.slide_to_act_view)
        slideToActView.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                val intent = Intent(this@OpeningUserProfilePatientActivity, OpeningUserProfilePatientFormActivity::class.java)
                intent.putExtra(EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM, userPatientId)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@OpeningUserProfilePatientActivity).toBundle())
                // Reset jika perlu digunakan kembali
                slideToActView.resetSlider()
            }
        }
    }

    companion object {
        const val EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT = "extra_user_patient_id_to_opening_user_profile_patient"
    }
}