package com.example.stunting.ui.nav_drawer_patient_fragment.home

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.stunting.R
import com.example.stunting.databinding.NavFragmentHomePatientBinding
import com.example.stunting.ui.anak_patient.AnakPatientActivity
import com.example.stunting.ui.anak_patient.AnakPatientActivity.Companion.EXTRA_CATEGORY_SERVICE_ID_TO_ANAK_PATIENT_ACTIVITY
import com.example.stunting.ui.anak_patient.AnakPatientActivity.Companion.EXTRA_USER_PATIENT_ID_TO_ANAK_PATIENT_ACTIVITY
import com.example.stunting.ui.bumil_patient.BumilPatientActivity
import com.example.stunting.ui.bumil_patient.BumilPatientActivity.Companion.EXTRA_CATEGORY_SERVICE_ID_TO_BUMIL_PATIENT_ACTIVITY
import com.example.stunting.ui.bumil_patient.BumilPatientActivity.Companion.EXTRA_USER_PATIENT_ID_TO_BUMIL_PATIENT_ACTIVITY
import com.example.stunting.ui.chatbot.ChatbotActivity
import com.example.stunting.utils.NetworkLiveData
import com.example.stunting.utils.VectorDrawableTagItems
import com.magicgoop.tagsphere.OnTagTapListener
import com.magicgoop.tagsphere.item.TagItem
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem


class NavHomePatientFragment : Fragment() {
    private var _binding: NavFragmentHomePatientBinding? = null
    private val binding get() = _binding!!

    private lateinit var powerMenu: PowerMenu
    private var userPatientId: Int? = null
    private var categoryServiceId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NavFragmentHomePatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPatientId = arguments?.getInt(EXTRA_USER_PATIENT_ID_TO_NAV_HOME_PATIENT_FRAGMENT)
        if (userPatientId != null) {
            setupTagSphereView()
        }
        setupPowerMenu()
        setupFabButton()
    }

    private fun setupFabButton() {
        binding.fabHome.setOnClickListener { view ->
            powerMenu.showAsAnchorCenter(view)
        }
    }

    private fun handleMenuClick(position: Int, item: PowerMenuItem) {
        when (item.title) {
            "Groupchat" -> {

            }
            "Chatbot" ->  {
                val intent = Intent(requireActivity(), ChatbotActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
            }
        }
    }

    private fun setupPowerMenu() {
        val listMenuItem = listOf(
            PowerMenuItem("Groupchat", iconRes = R.drawable.ic_people_outline_24),
            PowerMenuItem("Chatbot", iconRes = R.drawable.ic_child_care_24)
        )
        powerMenu = PowerMenu.Builder(requireActivity())
            .addItemList(listMenuItem)
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextGravity(Gravity.LEFT)
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setMenuRadius(30f)
            .setSelectedMenuColor(ContextCompat.getColor(requireActivity(), R.color.bluePrimary))
            .setOnMenuItemClickListener { position, item ->
                handleMenuClick(position, item)
                powerMenu.dismiss()
            }
            .build()
    }

    private fun getVectorDrawable(id: Int): Drawable? = ContextCompat.getDrawable(requireContext(), id)

    private fun setupTagSphereView() {
        val tags = mutableListOf<VectorDrawableTagItems>()

        val drawableResMap = mapOf(
            R.drawable.ic_bumil to "Ibu Hamil",
            R.drawable.ic_anak to "Anak",
            R.drawable.ic_remaja_putri to "Remaja Putri",
            R.drawable.ic_calon_pengantin to "Calon Pengantin",
            R.drawable.ic_keluarga to "Keluarga",
            R.drawable.ic_cegah_stunting to "Cegah Stunting"
        )

        (0..0).map {
            drawableResMap.forEach { (resId, label) ->
                getVectorDrawable(resId)?.let { drawable ->
                    tags.add(
                        VectorDrawableTagItems(drawable, resId, label)
                    )
                }
            }
        }

        binding.tagSphereView.addTagList(tags)
        binding.tagSphereView.setRadius(0.2f)
        binding.tagSphereView.setOnTagTapListener(object : OnTagTapListener {
            override fun onTap(tagItem: TagItem) {
                if (tagItem is VectorDrawableTagItems) {
                    val resName = resources.getResourceEntryName(tagItem.resId)
                    when (resName) {
                        "ic_bumil" -> {
                            categoryServiceId = 1
                            val intent = Intent(requireActivity(), BumilPatientActivity::class.java)
                            intent.putExtra(EXTRA_USER_PATIENT_ID_TO_BUMIL_PATIENT_ACTIVITY, userPatientId)
                            intent.putExtra(EXTRA_CATEGORY_SERVICE_ID_TO_BUMIL_PATIENT_ACTIVITY, categoryServiceId)
                            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
                        }
                        "ic_anak" -> {
                            categoryServiceId = 2
                            val intent = Intent(requireActivity(), AnakPatientActivity::class.java)
                            intent.putExtra(EXTRA_USER_PATIENT_ID_TO_ANAK_PATIENT_ACTIVITY, userPatientId)
                            intent.putExtra(EXTRA_CATEGORY_SERVICE_ID_TO_ANAK_PATIENT_ACTIVITY, categoryServiceId)
                            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())

                        }
                        "ic_remaja_putri" -> {
                            categoryServiceId = 3

                        }
                        "ic_calon_pengantin" -> {
                            categoryServiceId = 4

                        }
                        "ic_keluarga" -> {
                            categoryServiceId = 5

                        }
                        "ic_cegah_stunting" -> {

                        }
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = NavHomePatientFragment::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_NAV_HOME_PATIENT_FRAGMENT = "extra_user_patient_id_to_nav_home_patient_fragment"
    }
}