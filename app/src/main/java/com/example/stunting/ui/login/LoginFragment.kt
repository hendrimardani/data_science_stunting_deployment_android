package com.example.stunting.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyPasswordEditText.Companion.MIN_CHARACTER_PASSWORD
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataLogin
import com.example.stunting.database.with_api.response.PasienUser
import com.example.stunting.database.with_api.response.PetugasUser
import com.example.stunting.databinding.FragmentLoginBinding
import com.example.stunting.datastore.chatting.UserModel
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.opening_user_profile_patient.OpeningUserProfilePatientActivity
import com.example.stunting.ui.opening_user_profile_patient.OpeningUserProfilePatientActivity.Companion.EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.nav_drawer_fragment.NavDrawerMainActivity
import com.example.stunting.ui.nav_drawer_fragment.NavDrawerMainActivity.Companion.EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY
import com.example.stunting.ui.nav_drawer_fragment.NavDrawerMainActivity.Companion.EXTRA_USER_ID_TO_NAV_DRAWER_MAIN_ACTIVITY
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.example.stunting.ui.sign_up.SignUpActivity
import com.example.stunting.utils.NetworkLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var networkLiveData: NetworkLiveData
    private var sweetAlertDialog: SweetAlertDialog? = null

    private var userModel: UserModel? = null
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkLiveData = NetworkLiveData(requireActivity().application)
        networkLiveData.observe(requireActivity()) { isConnected ->
            if (isConnected) {
                showNoInternet(false)
                Toast.makeText(requireActivity(), getString(R.string.text_connected), Toast.LENGTH_SHORT).show()
                navToSignUpActivity()
                animationStart()
                textWatcher()
                loginButton()
            } else {
                showNoInternet(true)
                Toast.makeText(requireActivity(), getString(R.string.text_no_connected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navToSignUpActivity() {
        binding.tvDaftar.setOnClickListener {
            val intent = Intent(requireActivity(), SignUpActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
        }
    }

    private fun getChildrenPatientByUserPatientIdFromApi(userPatientId: Int) {
        viewModel.getChildrenPatientByUserPatientIdFromApi(userPatientId).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> { }
                    is ResultState.Error -> { }
                    is ResultState.Success -> {
                        if (result.data.isEmpty()) {
                            // Jika belum pernah tambah data anak
//                            Log.d(TAG, "onGetChildrenPatientByUserPatientId belum pernah tambah data anak success : ${result}")
                            val intent = Intent(requireActivity(), OpeningUserProfilePatientActivity::class.java)
                            intent.putExtra(EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT, userPatientId)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
                            requireActivity().finish()
                        } else {
                            // Jika sudah pernah tambah data anak
//                            Log.d(TAG, "onGetChildrenPatientByUserPatientId success : ${result.data}")
                            val intent = Intent(requireActivity(), NavDrawerMainActivityPatient::class.java)
                            intent.putExtra(EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, TAG)
                            intent.putExtra(EXTRA_USER_MODEL_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT, userModel)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
                            requireActivity().finish()
                        }
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(requireActivity(), ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showNoInternet(isNotInternetAvaliable: Boolean) {
        if (isNotInternetAvaliable) {
            binding.cl1.visibility = View.INVISIBLE
            binding.cvLogin.visibility = View.INVISIBLE
            binding.v1.visibility = View.INVISIBLE
            binding.v2.visibility = View.INVISIBLE
            binding.v3.visibility = View.INVISIBLE
            binding.v4.visibility = View.INVISIBLE

            binding.layoutNoInternet.clNoInternet.visibility = View.VISIBLE
        } else {
            binding.cl1.visibility = View.VISIBLE
            binding.cvLogin.visibility = View.VISIBLE
            binding.v1.visibility = View.VISIBLE
            binding.v2.visibility = View.VISIBLE
            binding.v3.visibility = View.VISIBLE
            binding.v4.visibility = View.VISIBLE

            binding.layoutNoInternet.clNoInternet.visibility = View.INVISIBLE
        }
    }

    private fun textWatcher() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = binding.tietEmail.text.toString().trim()
                val password = binding.tietPassword.text.toString().trim()

                val pattern = getString(R.string.pattern)
                val isEmailValid = email.contains(Regex(pattern))
                val isPasswordValid = password.length >= MIN_CHARACTER_PASSWORD

                binding.btnLogin.isEnabled = isEmailValid && isPasswordValid

                if (binding.btnLogin.isEnabled) {
                    binding.btnLogin.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.blueSecond))
                } else {
                    binding.btnLogin.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.buttonDisabledColor))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.tietEmail.addTextChangedListener(textWatcher)
        binding.tietPassword.addTextChangedListener(textWatcher)
    }

    private fun loginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()

            val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
            progressBar.setTitleText(getString(R.string.title_loading))
            progressBar.setContentText(getString(R.string.description_loading))
                .progressHelper.barColor = Color.parseColor("#73D1FA")
            progressBar.setCancelable(false)

            viewModel.login(email, password).observe(requireActivity()) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> progressBar.show()
                        is ResultState.Error -> {
                            progressBar.dismiss()
                            showSweetAlertDialog(result.error, 1)
                        }
                        is ResultState.Success -> {
                            progressBar.dismiss()
                            val dataLogin = result.data?.dataLogin
                            val role = result?.data?.dataLogin?.role
//                            Log.d(TAG, "onLogin role : ${role}")

                            if (role == "pasien") {
                                // Role pasien
                                val user = result.data.dataLogin.dataLoginUser
                                showSweetAlertDialog(result.data?.message.toString(), 2, role, user, dataLogin)
                            } else {
                                // Role petugas
                                val user = result.data?.dataLogin?.dataLoginUser
                                showSweetAlertDialog(result.data?.message.toString(), 2, role, user, dataLogin)
                            }
                        }
                        is ResultState.Unauthorized -> {
                            viewModel.logout()
                            val intent = Intent(requireActivity(), ContainerMainActivity::class.java)
                            intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                            startActivity(intent)
                        } 
                    }
                }
            }
        }
    }

    private fun showSweetAlertDialog(
        message: String, type: Int, role: String? = null, dataLoginUser: JsonObject? = null, dataLogin: DataLogin? = null
    ) {
        if (type == 1) {
            val sweetAlertDialog = SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog.setTitleText(getString(R.string.title_validation_error))
            sweetAlertDialog.setContentText(message)
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.show()
        } else if (type == 2) {
            if (role == "pasien") {
                val pasienUser = gson.fromJson(dataLoginUser, PasienUser::class.java)
                val userPatientId = pasienUser?.userPatientId
                val namaBumil = pasienUser?.namaBumil.toString()
                val token = dataLogin?.token.toString()
                userModel = UserModel(userPatientId.toString(), namaBumil, role, token)

                val sweetAlertDialog = SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                sweetAlertDialog.setTitleText(getString(R.string.title_message_success))
                sweetAlertDialog.setContentText(message)
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.setConfirmText(getString(R.string.ok))

                sweetAlertDialog.setConfirmClickListener { dialog ->
                    viewModel.saveSession(userModel!!)
                    dialog.dismiss()
//                    Log.d(TAG, "onLoginSucces: ${pasienUser.namaBumil}")
                    getChildrenPatientByUserPatientIdFromApi(userPatientId!!)
                }
                sweetAlertDialog.show()
            } else {
                val petugasUser = gson.fromJson(dataLoginUser, PetugasUser::class.java)
                val userId = petugasUser?.userId
                val nama = petugasUser?.nama.toString()
                val token = dataLogin?.token.toString()
                userModel = UserModel(userId.toString(), nama, role!!, token)

                val sweetAlertDialog = SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                sweetAlertDialog.setTitleText(getString(R.string.title_message_success))
                sweetAlertDialog.setContentText(message)
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.setConfirmText(getString(R.string.ok))

                sweetAlertDialog.setConfirmClickListener { dialog ->
                    viewModel.saveSession(userModel!!)
                    dialog.dismiss()
//                    Log.d(TAG, "onLoginSucces: ${petugasUser.nama}")

                    val intent = Intent(requireActivity(), NavDrawerMainActivity::class.java)
                    intent.putExtra(EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY, TAG)
                    intent.putExtra(EXTRA_USER_ID_TO_NAV_DRAWER_MAIN_ACTIVITY, userId)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                sweetAlertDialog.show()
            }
        }
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
        if (sweetAlertDialog != null) sweetAlertDialog = null
    }

    companion object {
        private val TAG = LoginFragment::class.java.simpleName
    }
}