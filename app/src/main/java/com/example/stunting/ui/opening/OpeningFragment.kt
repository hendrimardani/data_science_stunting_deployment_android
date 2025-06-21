package com.example.stunting.ui.opening

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.stunting.R
import com.example.stunting.databinding.FragmentOpeningBinding
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.login.LoginFragment
import com.example.stunting.ui.nav_drawer_fragment.NavDrawerMainActivity
import com.example.stunting.ui.sign_up.SignUpActivity
import android.content.SharedPreferences
import android.util.Log
import com.example.stunting.datastore.chatting.UserModel
import com.example.stunting.ui.nav_drawer_fragment.NavDrawerMainActivity.Companion.EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY
import com.example.stunting.ui.nav_drawer_fragment.NavDrawerMainActivity.Companion.EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_TAP_TARGET_VIEW_ACTIVED_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT

class OpeningFragment : Fragment() {
    private var _binding: FragmentOpeningBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var sharedPref: SharedPreferences
    private var isActivityDestroyed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpeningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSession().observe(viewLifecycleOwner) { userModel ->
//            Log.d(TAG, "onOpeningFragment : Apakah sudah login?: ${userModel.isLogin}")
//            Log.d(TAG, "onOpeningFragment : Nama anda: ${userModel.nama}")
            if (userModel.isLogin) {
                when (userModel.role) {
                    "pasien" -> gotoPasienActivity(userModel)
                    "petugas" -> gotoPetugasActivity(userModel)
                    "admin" -> { /*TODO */ }
                }
            }
        }

        sharedPref = requireActivity().getSharedPreferences("motion_pref", 0)
        // Jika kembali dari LoginFragment, masuk ke slide terakhir
        if (sharedPref.getBoolean("isReturning", false)) {
            val lastSlide = sharedPref.getInt("last_slide", R.id.menu1) // Default ke slide 1
            binding.motionLayout.post {
                binding.motionLayout.transitionToState(lastSlide)
            }
            sharedPref.edit().putBoolean("isReturning", false).apply()
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(requireActivity(), SignUpActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
        }

        binding.btnSignIn.setOnClickListener { goToLoginFragment() }

        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) { }

            override fun onTransitionChange(motionLayout: MotionLayout, startId: Int, endId: Int, progress: Float) { }

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                sharedPref.edit().putInt("last_slide", currentId).apply()
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) { }
        })
    }

    private fun gotoPetugasActivity(userModel: UserModel) {
        val intent = Intent(requireActivity(), NavDrawerMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY, TAG)
        intent.putExtra(EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY, userModel)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun gotoPasienActivity(userModel: UserModel) {
        val intent = Intent(requireActivity(), NavDrawerMainActivityPatient::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, TAG)
        intent.putExtra(EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, userModel)
        intent.putExtra(EXTRA_TAP_TARGET_VIEW_ACTIVED_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, false)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun goToLoginFragment() {
        sharedPref.edit().putBoolean("isReturning", true).apply()

        val loginFragment = LoginFragment()
        parentFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.container_fragment, loginFragment)
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        isActivityDestroyed = true
    }

    companion object {
        private val TAG = OpeningFragment::class.java.simpleName
    }
}
