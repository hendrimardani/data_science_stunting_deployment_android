package com.example.stunting.ui.group_chat_list

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.R
import com.example.stunting.ResultState
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

        getExtraDataFromNavHomeFragment()

    }

    private fun getExtraDataFromNavHomeFragment() {
        val userId = arguments?.getInt(EXTRA_USER_ID_TO_GROUP_CHAT_LIST_FRAGMET)
        Log.d(TAG, "onGroupChatListFragment id user : ${userId}")
    }

    private fun showCustomInputFragmentialog(userId: Int?) {
        val view = DialogCustomInputFragmentBinding.inflate(layoutInflater)
        val viewDialog = Dialog(requireActivity())

        viewDialog.setContentView(view.root)
        viewDialog.setCanceledOnTouchOutside(true)
        viewDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        view.btnAddGroup.setOnClickListener {
            val namaGroup = view.tietNamaGroup.text.toString()
            val deskripsi = view.tietDeskripsiGroup.text.toString()

            viewModel.addUserGroup(userId!!, namaGroup, deskripsi).observe(requireActivity()) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> progressBar.show()
                        is ResultState.Error -> {
                            progressBar.dismiss()
//                            showSweetAlertDialog(result.error, 1)
                            Log.d(TAG, "onGroupChatListFragment success adding group : ${result.error}")

                        }
                        is ResultState.Success -> {
                            progressBar.dismiss()
//                            val message = result.data.status
                            Log.d(TAG, "onGroupChatListFragment success adding group : berhiasl")
//                            showSweetAlertDialog(result.data.message.toString(), 2, user, dataLogin)
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