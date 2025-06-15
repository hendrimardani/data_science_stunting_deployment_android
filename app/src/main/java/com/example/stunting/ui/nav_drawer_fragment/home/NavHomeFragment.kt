package com.example.stunting.ui.nav_drawer_fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.R
import com.example.stunting.adapter.CardStackViewAdapter
import com.example.stunting.adapter.GroupChatListAdapter
import com.example.stunting.adapter.ListCegahStuntingAdapter
import com.example.stunting.databinding.NavFragmentHomeBinding
import com.example.stunting.resouce_data.CegahStuntingData
import com.example.stunting.resouce_data.Services
import com.example.stunting.ui.anak.AnakActivity
import com.example.stunting.ui.bumil.BumilActivity
import com.example.stunting.ui.calon_pengantin.CalonPengantinActivity
import com.example.stunting.ui.chatbot.ChatbotActivity
import com.example.stunting.ui.group_chat_list.GroupChatListFragment.Companion.EXTRA_USER_ID_TO_GROUP_CHAT_LIST_FRAGMET
import com.example.stunting.ui.layanan_keluarga.LayananKeluargaActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom

class NavHomeFragment : Fragment() {
    private var _binding: NavFragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var overlay: View

    private val list = ArrayList<CegahStuntingData>()
    private val servicesList = ArrayList<Services>()
    private var userId: Int? = null

    lateinit var cardStackViewLayoutManager: CardStackLayoutManager
    private val cardStackViewAdapter = CardStackViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NavFragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getInt(EXTRA_USER_ID_TO_NAV_HOME_FRAGMENT)
//        Log.d(TAG, "onNavHomeFragment id user : ${userId}")
        setupCardStackView()

        // bottomSheetCoordinatorLayout
//        setupBottomSheet()
//        setupViews()

        // Get from resources
//        binding.rvCegahStunting.setHasFixedSize(true)
//        list.addAll(getListCegahStuntingData())
//        showRecyclerList()
//        navigation(userId)
    }

    private fun setupCardStackView() {
        val services = mapOf(
            "Layanan Ibu Hamil" to R.drawable.ic_bumil,
            "Layanan Anak" to R.drawable.ic_anak,
            "Layanan Remaja Putri" to R.drawable.ic_remaja_putri,
            "Layanan Keluarga" to R.drawable.ic_keluarga,
            "Layanan Cegah Stunting" to R.drawable.ic_cegah_stunting
        )

        services.forEach { (key, value) ->
            servicesList.add(Services((1.. 1_000_000).random(), key, value))
        }

        cardStackViewLayoutManager = CardStackLayoutManager(requireActivity(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {
//                Log.d("CardStack", "onCardDragging : direction ${direction}, ratio : ${ratio}")
            }

            override fun onCardSwiped(direction: Direction?) {
                val position = cardStackViewLayoutManager.topPosition

                if (position == servicesList.size) {
                    val newList = ArrayList<Services>()

                    services.forEach { (key, value) ->
                        newList.add(Services((1.. 1_000_000).random(), key, value))
                    }

                    cardStackViewAdapter.submitList(newList)
                    binding.cardStackView.smoothScrollToPosition(0)
                }

//                Log.d("CardStack", "onCardSwiped  currentPosition : ${position}")
//                Log.d("CardStack", "onCardSwiped : direction ${direction}")
            }

            override fun onCardRewound() {
//                Log.d("CardStack", "onCardRewound")
            }

            override fun onCardCanceled() {
//                Log.d("CardStack", "onCardCanceled")
            }

            override fun onCardAppeared(view: View?, position: Int) {
//                Log.d("CardStack", "onCardAppeared view : ${view}, position : ${position}")
            }

            override fun onCardDisappeared(view: View?, position: Int) {
//                Log.d("CardStack", "onCardDisappeared view : ${view}, position : ${position}")
            }
        })
        cardStackViewLayoutManager.setMaxDegree(20f)
        cardStackViewLayoutManager.setTranslationInterval(30.0f)
        cardStackViewLayoutManager.setStackFrom(StackFrom.Left)

        binding.cardStackView.apply {
            layoutManager = cardStackViewLayoutManager
            adapter = cardStackViewAdapter
        }
        cardStackViewAdapter.submitList(servicesList)
    }

//    private fun navigation(userId: Int?) {
//        binding.apply {
//            cvBumil.setOnClickListener {
//                val intent = Intent(requireActivity(), BumilActivity::class.java)
//                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
//            }
//            cvCalonPengantin.setOnClickListener {
//                val intent = Intent(requireActivity(), CalonPengantinActivity::class.java)
//                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
//            }
//            cvRemajaPutri.setOnClickListener {
//                val intent = Intent(requireActivity(), RemajaPutriActivity::class.java)
//                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
//            }
//            cvLayananKeluarga.setOnClickListener {
//                val intent = Intent(requireActivity(), LayananKeluargaActivity::class.java)
//                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
//            }
//            cvAnak.setOnClickListener {
//                val intent = Intent(requireActivity(), AnakActivity::class.java)
//                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
//            }
//            fabHome.setOnClickListener { showPopupMenu(userId) }
//        }
//    }

//    private fun showPopupMenu(userId: Int?) {
//        val popupMenu = PopupMenu(requireActivity(), binding.fabHome)
//
//        popupMenu.menuInflater.inflate(R.menu.menu_popup_message, popupMenu.menu)
//        popupMenu.setOnMenuItemClickListener { menuItem ->
//            if (menuItem.title == getString(R.string.menu_title_chatbot)) {
//                val intent = Intent(requireActivity(), ChatbotActivity::class.java)
//                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
//                true
//            } else {
//                val bundle = Bundle(). apply {
//                    putInt(EXTRA_USER_ID_TO_GROUP_CHAT_LIST_FRAGMET, userId!!)
//                }
//                val navController = findNavController()
//                val navOptions = NavOptions.Builder()
//                    .setEnterAnim(R.anim.slide_in)
//                    .setExitAnim(R.anim.fade_out)
//                    .setPopEnterAnim(R.anim.fade_in)
//                    .setPopExitAnim(R.anim.slide_out)
//                    .build()
//                navController.navigate(R.id.action_nav_home_to_nav_group_chat_list, bundle, navOptions)
//                true
//            }
//        }
//
//        // Supaya muncul ikon di popup menu
//        try {
//            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
//            fieldMPopup.isAccessible = true
//            val mPopup = fieldMPopup.get(popupMenu)
//            mPopup.javaClass
//                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
//                .invoke(mPopup, true)
//        } catch (e: Exception){
//            Log.e("Main", "Error showing menu icons.", e)
//        } finally {
//            popupMenu.show()
//        }
//    }

//    private fun showRecyclerList() {
//        binding.rvCegahStunting.layoutManager = LinearLayoutManager(requireActivity())
//        val listCegahStuntingAdapter = ListCegahStuntingAdapter(list)
//        binding.rvCegahStunting.adapter = listCegahStuntingAdapter
//    }
//
//    @SuppressLint("Recycle")
//    private fun getListCegahStuntingData(): ArrayList<CegahStuntingData> {
//        val titles = resources.getStringArray(R.array.data_titles)
//        val images = resources.obtainTypedArray(R.array.data_images)
//        val listCegahStuntingData = ArrayList<CegahStuntingData>()
//
//        for (i in 0..titles.size - 1) {
//            val items = CegahStuntingData(titles[i], images.getResourceId(i, -1))
//            listCegahStuntingData.add(items)
//        }
//        return listCegahStuntingData
//    }

//    override fun onBackPressed() {
//        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        } else if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        } else {
//            // Keluar dari aplikasi jika CoordinatorLayout tidak terlihat
//            super.onBackPressed()
//        }
//    }

//    private fun setupBottomSheet() {
//        val bottomSheet = requireView().findViewById<View>(R.id.bottom_sheet)
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
//        overlay = requireView().findViewById(R.id.overlay)
//
//        bottomSheetBehavior.apply {
//            isHideable = true
//
//            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    when (newState) {
//                        BottomSheetBehavior.STATE_EXPANDED -> {
//                            overlay.visibility = View.VISIBLE
//                            overlay.alpha = 1f
//                        }
//                        BottomSheetBehavior.STATE_COLLAPSED -> {
//                            overlay.visibility = View.GONE
//                        }
//                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
//                            overlay.visibility = View.VISIBLE
//                            overlay.alpha = 1f
//                        }
//                        BottomSheetBehavior.STATE_HIDDEN -> {
//                            overlay.visibility = View.GONE
//                        }
//                    }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                    if (slideOffset > 0) {
//                        overlay.visibility = View.VISIBLE
//                        overlay.alpha = slideOffset
//                    } else {
//                        overlay.visibility = View.GONE
//                    }
//                }
//            })
//        }
//    }

//    private fun setupViews() {
//        binding.cvCegahStunting.setOnClickListener {
//            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//            } else {
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            }
//        }
//
//        // Setup overlay click behavior
//        overlay.setOnClickListener {
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = NavHomeFragment::class.java.simpleName
        val EXTRA_USER_ID_TO_NAV_HOME_FRAGMENT = "extra_user_id_to_nav_home_fragment"
    }
}