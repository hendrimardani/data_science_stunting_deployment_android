package com.example.stunting.ui.group_chat_list

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyDeskripsiGroupEditText.Companion.MAX_CHARACTER_DESKRIPSI_GROUP
import com.example.stunting.MyNamaGroupEditText.Companion.MAX_CHARACTER_NAMA_GROUP
import com.example.stunting.R
import com.example.stunting.adapter.GroupChatListAdapter
import com.example.stunting.databinding.DialogCustomTambahGroupBinding
import com.example.stunting.databinding.FragmentGroupChatListBinding
import com.example.stunting.ui.MainViewModel
import com.example.stunting.ui.ViewModelFactory

class GroupChatListFragment : Fragment() {
    private var _binding: FragmentGroupChatListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var userId: Int? = null
    private val groupChatListAdapter = GroupChatListAdapter()

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

        userId = arguments?.getInt(EXTRA_USER_ID_TO_GROUP_CHAT_LIST_FRAGMET)
//        Log.d(TAG, "onGroupChatListFragment id user : ${userId}")

        getUserGroups()
//        getUserGroupRelationByUserId(userId!!)

        binding.rvGroup.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = groupChatListAdapter
        }

//        getUserGroupRelationByUserIdRole(userId!!, ROLE_ADMIN)

//        binding.fabAdd.setOnClickListener { showDialogCustomTambahGroupBinding(userId) }
    }

//    private fun getUserGroupRelationByUserIdRole(userId: Int, role: String) {
//        viewModel.getUserGroupRelationByUserIdRole(userId, role).observe(requireActivity()) { result ->
//            val groupIsCreated = result.size
//            if (groupIsCreated >= 1) binding.fabAdd.visibility = View.GONE
//            else binding.fabAdd.visibility = View.VISIBLE
//        }
//    }

    private fun textWatcherDialogCustomTambahGroup(view: DialogCustomTambahGroupBinding) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val namaGroup = view.tietNamaGroup.text.toString()
                val deskripsiGroup = view.tietDeskripsiGroup.text.toString()

                val isNamaGroupValid = namaGroup.length >= MAX_CHARACTER_NAMA_GROUP
                val isDeskripsiGroupValid = deskripsiGroup.length >= MAX_CHARACTER_DESKRIPSI_GROUP

                view.btnAdd.isEnabled = namaGroup.isNotEmpty() && deskripsiGroup.isNotEmpty() &&
                    !isNamaGroupValid && !isDeskripsiGroupValid

                if (view.btnAdd.isEnabled) {
                    view.btnAdd.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(requireActivity(), R.color.blueSecond)
                    )
                } else {
                    view.btnAdd.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(requireActivity(), R.color.buttonDisabledColor)
                    )
                    view.btnAdd.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireActivity(), R.color.white)
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        view.tietNamaGroup.addTextChangedListener(textWatcher)
        view.tietDeskripsiGroup.addTextChangedListener(textWatcher)
    }


    private fun getUserGroups() {
        val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

//        viewModel.getUserGroupsFromApi()
//        viewModel.getUserGroupsResult.observe(requireActivity()) { result ->
//            if (result != null) {
//                when (result) {
//                    is ResultState.Loading -> progressBar.show()
//                    is ResultState.Error -> {
//                        progressBar.dismiss()
////                        Log.d(TAG, "onGroupChatListFragment getUserGroups Error  : ${result.error}")
//                    }
//                    is ResultState.Success -> {
//                        progressBar.dismiss()
////                        Log.d(TAG, "onGroupChatListFragment getUserGroups : ${result.data}")
//                    }
//                    is ResultState.Unauthorized -> {
//                        viewModel.logout()
//                        val intent = Intent(requireActivity(), MainActivity::class.java)
//                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, LOGIN_FRAGMENT)
//                        startActivity(intent)
//                    }
//                }
//            }
//        }
    }

//    private fun getUserGroupRelationByUserId(userId: Int?) {
//        viewModel.getUserGroupRelationByUserId(userId!!).observe(requireActivity()) { userGroupRelation ->
//            groupChatListAdapter.submitList(userGroupRelation)
//        }
//    }

//    private fun showDialogCustomTambahGroupBinding(userId: Int?) {
//        val view = DialogCustomTambahGroupBinding.inflate(layoutInflater)
//        val viewDialog = Dialog(requireActivity())
//
//        viewDialog.setContentView(view.root)
//        viewDialog.setCanceledOnTouchOutside(false)
//        viewDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
//        progressBar.setTitleText(getString(R.string.title_loading))
//        progressBar.setContentText(getString(R.string.description_loading))
//            .progressHelper.barColor = Color.parseColor("#73D1FA")
//        progressBar.setCancelable(false)
//
//        textWatcherDialogCustomTambahGroup(view)
//
//        view.btnAdd.setOnClickListener {
//            val userIdList = listOf(userId!!)
//            val namaGroup = view.tietNamaGroup.text.toString()
//            val deskripsi = view.tietDeskripsiGroup.text.toString()
//
//            viewModel.addUserGroup(
//                userIdList, namaGroup, deskripsi, null, null)
//                .observe(requireActivity()) { result ->
//                    if (result != null) {
//                        when (result) {
//                            is ResultState.Loading -> progressBar.show()
//                            is ResultState.Error -> {
//                                progressBar.dismiss()
//    //                            Log.d(TAG, "onGroupChatListFragment error : ${result.error}")
//                            }
//                            is ResultState.Success -> {
//                                progressBar.dismiss()
//                                viewDialog.dismiss()
//
//                                val message = result.data?.message.toString()
//    //                            Log.d(TAG, "onGroupChatListFragment success adding the group : ${result.data}}")
//                                Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
//
//                                // Simpan ke database lagi
//                                getUserGroupRelationByUserId(userId)
//                                getUserGroups()
//
//                                view.tietNamaGroup.text?.clear()
//                                view.tietDeskripsiGroup.text?.clear()
//                            }
//                            is ResultState.Unauthorized -> {
//                                viewModel.logout()
//                                val intent = Intent(requireActivity(), MainActivity::class.java)
//                                intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, LOGIN_FRAGMENT)
//                                startActivity(intent)
//                            }
//                        }
//                    }
//                }
//        }
//            view.btnCancel.setOnClickListener { viewDialog.dismiss() }
//            viewDialog.show()
//    }

    override fun onResume() {
        super.onResume()
        getUserGroups()
    }

    companion object {
        private val TAG = GroupChatListFragment::class.java.simpleName
        private const val ROLE_ADMIN = "admin"
        private const val LOGIN_FRAGMENT = "LoginFragment"
        const val EXTRA_USER_ID_TO_GROUP_CHAT_LIST_FRAGMET = "extra_user_id_to_group_chat_list_fragment"
    }
}