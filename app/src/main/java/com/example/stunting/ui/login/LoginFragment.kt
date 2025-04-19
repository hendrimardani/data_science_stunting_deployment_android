package com.example.stunting.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyPasswordEditText.Companion.MIN_CHARACTER_PASSWORD
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.response.DataLogin
import com.example.stunting.database.with_api.response.DataLoginUserProfile
import com.example.stunting.databinding.FragmentLoginBinding
import com.example.stunting.datastore.chatting.UserModel
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.navigation_drawer_fragment.NavDrawerMainActivity
import com.example.stunting.ui.navigation_drawer_fragment.NavDrawerMainActivity.Companion.EXTRA_FRAGMENT_TO_NAV_DRAWER_MAIN_ACTIVITY
import com.example.stunting.ui.navigation_drawer_fragment.NavDrawerMainActivity.Companion.EXTRA_USER_ID_TO_NAV_DRAWER_MAIN_ACTIVITY
import com.example.stunting.utils.NetworkLiveData

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkLiveData: NetworkLiveData
    private var sweetAlertDialog: SweetAlertDialog? = null

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }


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
                animationStart()
                textWatcher()
                loginButton()
            } else {
                showNoInternet(true)
                Toast.makeText(requireActivity(), getString(R.string.text_no_connected), Toast.LENGTH_SHORT).show()
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

                if (binding.btnLogin.isEnabled == true) {
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
                            val user = result.data?.dataLogin?.dataLoginUserProfile
                            val dataLogin = result.data?.dataLogin
                            showSweetAlertDialog(result.data?.message.toString(), 2, user, dataLogin)
                        }
                        is ResultState.Unauthorized -> {
                            viewModel.logout()
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun showSweetAlertDialog(message: String, type: Int, userProfile: DataLoginUserProfile? = null, dataLogin: DataLogin? = null) {
        val userId = userProfile?.userId
        val nama = userProfile?.nama.toString()
        val token = dataLogin?.token.toString()
        val userModel = UserModel(userId.toString(), nama, token)

        if (type == 1) {
            val sweetAlertDialog = SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog.setTitleText(getString(R.string.title_validation_error))
            sweetAlertDialog.setContentText(message)
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.show()
        } else if (type == 2) {
            val sweetAlertDialog = SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.setTitleText(getString(R.string.title_message_success))
            sweetAlertDialog.setContentText(message)
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmText(getString(R.string.ok))

            viewModel.saveSession(userModel)
            sweetAlertDialog.setConfirmClickListener { dialog ->
                dialog.dismiss()
//                Log.d(TAG, "onLoginSucces: ${user.email}")

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