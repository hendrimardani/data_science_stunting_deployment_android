package com.example.stunting.ui.opening_user_profile_patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.R
import com.example.stunting.databinding.ActivityOpeningUserProfilePatientBinding
import com.example.stunting.ui.opening_user_profile_patient_form.OpeningUserProfilePatientFormActivity
import com.example.stunting.ui.opening_user_profile_patient_form.OpeningUserProfilePatientFormActivity.Companion.EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM
import com.ncorti.slidetoact.SlideToActView


class OpeningUserProfilePatientActivity : AppCompatActivity() {
    private var _binding: ActivityOpeningUserProfilePatientBinding? = null
    private val binding get() = _binding!!

    private val FADE_OUT_DURATION = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOpeningUserProfilePatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userPatientId = intent?.getIntExtra(
            EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT, 0
        )

        binding.slideToActView.onSlideToActAnimationEventListener =
            object : SlideToActView.OnSlideToActAnimationEventListener {
                override fun onSlideCompleteAnimationStarted(view: SlideToActView, threshold: Float) {
                    binding.tvIsiSekarang.alpha = 0f
                    binding.lavArrowRight.alpha = 0f
                    binding.tvIsiSekarang.visibility = View.GONE
                    binding.lavArrowRight.visibility = View.GONE

                    view.resetSlider()
                }

                override fun onSlideCompleteAnimationEnded(view: SlideToActView) {
                    startFadeOutAnimation()
                    val intent = Intent(
                        this@OpeningUserProfilePatientActivity,
                        OpeningUserProfilePatientFormActivity::class.java
                    )
                    intent.putExtra(EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM, userPatientId)
                    startActivity(
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@OpeningUserProfilePatientActivity
                        ).toBundle()
                    )
                }

                override fun onSlideResetAnimationStarted(view: SlideToActView) {
                    cancelAndResetElementAnimations()
                }

                override fun onSlideResetAnimationEnded(view: SlideToActView) { }
            }
    }

    private fun startFadeOutAnimation() {
        if (binding.tvIsiSekarang.alpha > 0f) {
            binding.tvIsiSekarang.animate()
                .alpha(0f)
                .setDuration(FADE_OUT_DURATION)
                .setInterpolator(AccelerateInterpolator()) // Membuat fade out terasa lebih cepat di akhir
                .start()
        }

        if (binding.lavArrowRight.alpha > 0f) {
            binding.lavArrowRight.animate()
                .alpha(0f)
                .setDuration(FADE_OUT_DURATION)
                .setInterpolator(AccelerateInterpolator())
                .start()
        }
    }

    private fun cancelAndResetElementAnimations() {
        // Hentikan animasi yang sedang berjalan pada elemen
        binding.tvIsiSekarang.animate().cancel()
        binding.lavArrowRight.animate().cancel()

        // Kembalikan ke kondisi awal
        showInitialElementsState()
    }

    private fun showInitialElementsState() {
        binding.tvIsiSekarang.visibility = View.VISIBLE
        binding.lavArrowRight.visibility = View.VISIBLE
        binding.tvIsiSekarang.alpha = 1.0f
        binding.lavArrowRight.alpha = 1.0f
    }

    companion object {
        const val EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT = "extra_user_patient_id_to_opening_user_profile_patient"
    }
}