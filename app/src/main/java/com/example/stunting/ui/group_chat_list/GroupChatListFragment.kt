package com.example.stunting.ui.group_chat_list

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.adapter.GroupChatListAdapter
import com.example.stunting.databinding.DialogCustomInputFragmentBinding
import com.example.stunting.databinding.FragmentGroupChatListBinding
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory

class GroupChatListFragment : Fragment() {
    private var _binding: FragmentGroupChatListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    val groupChatListAdapter = GroupChatListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupChatListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getInt(EXTRA_USER_ID_TO_GROUP_CHAT_LIST_FRAGMET)
//        Log.d(TAG, "onGroupChatListFragment id user : ${userId}")
        getUserGroupByUserId(userId)
        getUserGroup()

        binding.rvGroupChatList.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, true)
            setHasFixedSize(true)
            adapter = groupChatListAdapter
        }

        binding.fabGroupChatList.setOnClickListener { showCustomInputFragmentialog(userId) }
    }

    private fun textWatcherDialogCustomInput(view: DialogCustomInputFragmentBinding) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val namaGroup = view.tietNamaGroup.text.toString()
                val deskripsiGroup = view.tietDeskripsiGroup.text.toString()

                view.btnAddGroup.isEnabled = namaGroup.isNotEmpty() && deskripsiGroup.isNotEmpty()

                if (view.btnAddGroup.isEnabled == true) {
                    view.btnAddGroup.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.blueSecond))
                } else {
                    view.btnAddGroup.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.buttonDisabledColor))
                    view.btnAddGroup.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        view.tietNamaGroup.addTextChangedListener(textWatcher)
        view.tietDeskripsiGroup.addTextChangedListener(textWatcher)
    }


    private fun getUserGroup() {
        val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getUserGroup().observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> progressBar.dismiss()
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onGroupChatListFragment getUserGroup : ${result.data}")
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

    private fun getUserGroupByUserId(userId: Int?) {
        val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getUserGroupByUserId(userId!!).observe(requireActivity()) { result ->
//            Log.d(TAG, "onGroupChatListFragment getUserGroupByUserId : ${result}")
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> { progressBar.show() }
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onGroupChatListFragment error : ${result.error}")
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
                        val dataUserGroupByUserId = result.data?.dataUserGroupByUserId
//                        Log.d(TAG, "onGroupChatListFragment success adding the group : ${data}}")
                        groupChatListAdapter.submitList(dataUserGroupByUserId)
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

    private fun showCustomInputFragmentialog(userId: Int?) {
        val view = DialogCustomInputFragmentBinding.inflate(layoutInflater)
        val viewDialog = Dialog(requireActivity())

        viewDialog.setContentView(view.root)
        viewDialog.setCanceledOnTouchOutside(false)
        viewDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        textWatcherDialogCustomInput(view)

        view.btnAddGroup.setOnClickListener {
            val namaGroup = view.tietNamaGroup.text.toString()
            val deskripsi = view.tietDeskripsiGroup.text.toString()

            viewModel.addUserGroup(userId!!, namaGroup, deskripsi).observe(requireActivity()) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> progressBar.show()
                        is ResultState.Error -> {
                            progressBar.dismiss()
//                            Log.d(TAG, "onGroupChatListFragment error : ${result.error}")
                        }
                        is ResultState.Success -> {
                            progressBar.dismiss()
                            viewDialog.dismiss()

                            val message = result.data?.message.toString()
//                            Log.d(TAG, "onGroupChatListFragment success adding the group : ${result.data}}")
                            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()

                            getUserGroupByUserId(userId)

                            view.tietNamaGroup.text?.clear()
                            view.tietDeskripsiGroup.text?.clear()
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
        view.btnCancel.setOnClickListener { viewDialog.dismiss() }
        viewDialog.show()
    }

    companion object {
        private val TAG = GroupChatListFragment::class.java.simpleName
        const val EXTRA_USER_ID_TO_GROUP_CHAT_LIST_FRAGMET = "extra_user_id_to_group_chat_list_fragment"
    }
}