package com.example.stunting.ui.sign_up

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyNamaEditText.Companion.MIN_CHARACTER_NAMA
import com.example.stunting.MyPasswordEditText.Companion.MIN_CHARACTER_PASSWORD
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.databinding.ActivitySignUpBinding
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.utils.NetworkLiveData
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem

class SignUpActivity : AppCompatActivity() {
    private var _binding: ActivitySignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var powerMenu: PowerMenu
    private lateinit var networkLiveData: NetworkLiveData

    private var sweetAlertDialog: SweetAlertDialog? = null


    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        networkLiveData = NetworkLiveData(application)
        networkLiveData.observe(this) { isConnected ->
            if (isConnected) {
                getBranchesFromApi()
                showNoInternet(false)
                Toast.makeText(this, getString(R.string.text_connected), Toast.LENGTH_SHORT).show()
                animationStart()
                textWatcher()
                setupPowerMenu()
                setupCabangClicked()
                signUpButton()
            } else {
                showNoInternet(true)
                Toast.makeText(this, getString(R.string.text_no_connected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCabangClicked() {
        binding.tvNamaCabang.setOnClickListener { view ->
            powerMenu.showAsDropDown(view)
        }
    }

    private fun getBranchesFromApi() {
        viewModel.getBranchesResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {  }
                    is ResultState.Error -> {
//                        Log.d(TAG, "onGetBranchesFromApi Error : ${result.error}")
                    }
                    is ResultState.Success -> {
//                        Log.d(TAG, "onGetBranchesFromApi Success : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@SignUpActivity, ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showNoInternet(isNotInternetAvaliable: Boolean) {
        if (isNotInternetAvaliable) {
            binding.vBackground1.visibility = View.INVISIBLE
            binding.ivStuntingApp.visibility = View.INVISIBLE
            binding.lavLogin.visibility = View.INVISIBLE
            binding.v1.visibility = View.INVISIBLE
            binding.v2.visibility = View.INVISIBLE
            binding.cvLogin.visibility = View.INVISIBLE
            binding.vBackground2.visibility = View.INVISIBLE
            binding.v3.visibility = View.INVISIBLE
            binding.v4.visibility = View.INVISIBLE

            binding.layoutNoInternet.clNoInternet.visibility = View.VISIBLE
        } else {
            binding.vBackground1.visibility = View.VISIBLE
            binding.ivStuntingApp.visibility = View.VISIBLE
            binding.lavLogin.visibility = View.VISIBLE
            binding.v1.visibility = View.VISIBLE
            binding.v2.visibility = View.VISIBLE
            binding.cvLogin.visibility = View.VISIBLE
            binding.vBackground2.visibility = View.VISIBLE
            binding.v3.visibility = View.VISIBLE
            binding.v4.visibility = View.VISIBLE

            binding.layoutNoInternet.clNoInternet.visibility = View.INVISIBLE
        }
    }

    private fun textWatcher() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nama = binding.tietNama.text.toString().trim()
                val email = binding.tietEmail.text.toString().trim()
                val namaCabang = binding.tvNamaCabang.text
                val password = binding.tietPassword.text.toString().trim()
                val repeatPassword = binding.tietRepeatPassword.text.toString().trim()

                val pattern = getString(R.string.pattern)
                val isNamaValid = nama.length >= MIN_CHARACTER_NAMA
                val isEmailValid = email.contains(Regex(pattern))
                val namaCabangIsNotEmpty = namaCabang.isNotEmpty()
                val isPasswordValid = password.length >= MIN_CHARACTER_PASSWORD
                val isRepeatPasswordValid = password == repeatPassword

                binding.btnSignUp.isEnabled = isNamaValid && isEmailValid &&
                        namaCabangIsNotEmpty && isPasswordValid && isRepeatPasswordValid

                if (binding.btnSignUp.isEnabled) {
                    binding.btnSignUp.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this@SignUpActivity, R.color.blueSecond))
                } else {
                    binding.btnSignUp.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this@SignUpActivity, R.color.buttonDisabledColor))
                    binding.btnSignUp.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@SignUpActivity, R.color.white))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.tietNama.addTextChangedListener(textWatcher)
        binding.tietEmail.addTextChangedListener(textWatcher)
        binding.tietPassword.addTextChangedListener(textWatcher)
        binding.tietRepeatPassword.addTextChangedListener(textWatcher)
    }


    private fun setupPowerMenu() {
        val listMenuItem = mutableListOf<PowerMenuItem>()

        viewModel.getBranchesFromLocal().observe(this) { branchesList ->
            branchesList.forEach { branchEntity ->
                listMenuItem.add(
                    PowerMenuItem(branchEntity.namaCabang)
                )
            }

            powerMenu = PowerMenu.Builder(this)
                .addItemList(listMenuItem)
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextGravity(Gravity.LEFT)
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setMenuRadius(30f)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.bluePrimary))
                .setOnMenuItemClickListener { position, item ->
                    binding.tvNamaCabang.setText(item.title)
                    binding.tvNamaCabang.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.black))
                    powerMenu.dismiss()
                }
                .build()
        }
    }

    private fun signUpButton() {
        binding.btnSignUp.setOnClickListener {
            val nama = binding.tietNama.text.toString().trim()
            val email = binding.tietEmail.text.toString().trim()
            val namaCabang = binding.tvNamaCabang.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()
            val repeatPassword = binding.tietRepeatPassword.text.toString().trim()

            val progressBar = SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.PROGRESS_TYPE)
            progressBar.setTitleText(getString(R.string.title_loading))
            progressBar.setContentText(getString(R.string.description_loading))
                .progressHelper.barColor = Color.parseColor("#73D1FA")
            progressBar.setCancelable(false)

            viewModel.register(nama, email, namaCabang, password, repeatPassword).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> progressBar.show()
                        is ResultState.Error -> {
                            progressBar.dismiss()
                            showSweetAlertDialog(result.error, 1)
                        }
                        is ResultState.Success -> {
                            progressBar.dismiss()
                            showSweetAlertDialog(result.data!!.message!!, 2)
                        }
                        is ResultState.Unauthorized -> {
                            viewModel.logout()
                            val intent = Intent(this@SignUpActivity, ContainerMainActivity::class.java)
                            intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun showSweetAlertDialog(message: String, type: Int) {
        if (type == 1) {
            sweetAlertDialog = SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog!!.setTitleText(getString(R.string.title_validation_error))
            sweetAlertDialog!!.setContentText(message)
            sweetAlertDialog!!.setCancelable(false)
            sweetAlertDialog!!.show()
        } else if (type == 2) {
            sweetAlertDialog = SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog!!.setTitleText(getString(R.string.title_message_success))
            sweetAlertDialog!!.setContentText(message)
            sweetAlertDialog!!.setCancelable(false)
            sweetAlertDialog!!.setConfirmText(getString(R.string.next_login))

            sweetAlertDialog!!.setConfirmClickListener { dialog ->
                dialog.dismiss()
                val intent = Intent(this@SignUpActivity, ContainerMainActivity::class.java)
                intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@SignUpActivity).toBundle())
                finish()
            }
            sweetAlertDialog!!.show()
        }
    }

    private fun animationStart() {
        ObjectAnimator.ofFloat(binding.v1, View.ROTATION, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        ObjectAnimator.ofFloat(binding.v2, View.ROTATION, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        ObjectAnimator.ofFloat(binding.v3, View.ROTATION, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        ObjectAnimator.ofFloat(binding.v4, View.ROTATION, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (sweetAlertDialog != null) sweetAlertDialog = null
    }

    companion object {
        private val TAG = SignUpActivity::class.java.simpleName
    }
}