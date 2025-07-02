package com.example.stunting.ui.nav_drawer_patient_fragment.daftar_anak

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.children_patient.ChildrenPatientEntity
import com.example.stunting.databinding.NavFragmentDaftarAnakPatientBinding
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.detail_anak_patient.DetailAnakPatientActivity
import com.example.stunting.ui.detail_anak_patient.DetailAnakPatientActivity.Companion.EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY
import com.example.stunting.ui.detail_anak_patient.DetailAnakPatientActivity.Companion.EXTRA_USER_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY
import com.example.stunting.ui.tambah_anak_patient.TambahAnakPatientActivity
import com.example.stunting.ui.tambah_anak_patient.TambahAnakPatientActivity.Companion.EXTRA_USER_PATIENT_ID_TO_TAMBAH_ANAK_PATIENT_ACTIVITY


class NavDaftarAnakPatientFragment : Fragment() {
    private var _binding: NavFragmentDaftarAnakPatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NavDaftarAnakPatientViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var userPatientId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NavFragmentDaftarAnakPatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPatientId = arguments?.getInt(EXTRA_USER_PATIENT_ID_TO_NAV_DAFTAR_ANAK_PATIENT_FRAGMENT)

        binding.swipeRefreshLayout.setOnRefreshListener {
            getChildrenPatientByUserPatientIdFromApi()
        }
        getChildrenPatientByUserPatientIdFromApi()
        getChildrenPatientByUserPatientIdFromLocal()
        tambahAnak()
    }

    private fun tambahAnak() {
        binding.clTambah.setOnClickListener {
            val intent = Intent(requireActivity(), TambahAnakPatientActivity::class.java)
            intent.putExtra(EXTRA_USER_PATIENT_ID_TO_TAMBAH_ANAK_PATIENT_ACTIVITY, userPatientId)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
        }
    }

    private fun getChildrenPatientByUserPatientIdFromLocal() {
        viewModel.getChildrenPatientByUserPatientIdFromLocal(userPatientId!!)
            .observe(viewLifecycleOwner) { childrenPatientList ->
                if (childrenPatientList.size == 4) binding.clTambah.visibility = View.INVISIBLE
                else binding.clTambah.visibility = View.INVISIBLE

                setupTableLayout(childrenPatientList)
            }
    }

    private fun getChildrenPatientByUserPatientIdFromApi() {
        viewModel.getChildrenPatientByUserPatientIdFromApi(userPatientId!!)
        viewModel.getChildrenPatientByUserPatientIdFromApiResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
                    is ResultState.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
//                        Log.d(TAG, "getChildrenPatientByUserPatientIdFromApiResult Error : ${result.error}")
                        Toast.makeText(requireContext(), getString(R.string.text_no_connected), Toast.LENGTH_SHORT).show()
                    }
                    is ResultState.Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false
//                        Log.d(TAG, "getChildrenPatientByUserPatientIdFromApiResult Success : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(requireContext(), ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupTableLayout(childrenPatientList: List<ChildrenPatientEntity>) {
        binding.tableLayout.removeViews(1, binding.tableLayout.childCount - 1)
        childrenPatientList.forEach { item ->
            val tableRow = TableRow(requireActivity())

            val namaTextView = TextView(requireActivity()).apply {
                text = item.namaAnak
                setPadding(8, 8, 8, 8)
            }
            val nikTextView = TextView(requireActivity()).apply {
                text = item.nikAnak
                setPadding(8, 8, 8, 8)
            }
            val jenisKelaminTextView = TextView(requireActivity()).apply {
                text = item.jenisKelaminAnak
                setPadding(8, 8, 8, 8)
            }
            val tglLahirTextView = TextView(requireActivity()).apply {
                text = item.tglLahirAnak
                setPadding(8, 8, 8, 8)
            }
            val umurTextView = TextView(requireActivity()).apply {
                text = item.umurAnak
                setPadding(8, 8, 64, 8)
            }
            val layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                // dalam pixel
                bottomMargin = 16
            }
            val editDataTextView = TextView(requireContext()).apply {
                text = "Edit"
                setTextColor(ContextCompat.getColor(context, R.color.orange))
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(requireContext(), R.drawable.stroke_orange_background)
                this.layoutParams = layoutParams
                setOnClickListener {
                    val intent = Intent(requireActivity(), DetailAnakPatientActivity::class.java)
                    intent.putExtra(EXTRA_USER_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY, userPatientId)
                    intent.putExtra(EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY, item.id)
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
                }
            }
            tableRow.addView(namaTextView)
            tableRow.addView(nikTextView)
            tableRow.addView(jenisKelaminTextView)
            tableRow.addView(tglLahirTextView)
            tableRow.addView(umurTextView)
            tableRow.addView(editDataTextView)
            binding.tableLayout.addView(tableRow)
        }
    }

    companion object {
        private val TAG = NavDaftarAnakPatientFragment::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_NAV_DAFTAR_ANAK_PATIENT_FRAGMENT = "extra_user_patient_id_to_nav_anak_patient_fragment"
    }
}