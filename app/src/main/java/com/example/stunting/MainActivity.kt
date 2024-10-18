package com.example.stunting

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.databinding.DialogBottomSheetCegahStuntingBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogBottomSheetCegahStunting: DialogBottomSheetCegahStuntingBinding

    companion object {
        const val TEXT_1 = "1. Konsumsi makanan bergizi seimbang yang mencakup protein hewani, sayuran, buah, dan karbohidrat kompleks."
        const val TEXT_2 = "2. Minum suplemen kehamilan sesuai anjuran dokter."
        const val TEXT_3 = "3. Menjaga berat badan ideal selama kehamilan."
        const val TEXT_4 = "4. Biarkan bayi menyusu langsung pada ibu dalam 1 jam pertama setelah dilahirkan."
        const val TEXT_5 = "5. ASI eksklusif selama 6 bulan pertama kehidupan bayi."
        const val TEXT_6 = "6. Lanjutkan pemberian ASI hingga anak berusia 2 tahun atau lebih, dengan pendamping makanan pendamping ASI (MPASI) yang sehat dan bergizi."
        const val TEXT_7 = "7. Mulai memberikan MPASI pada usia 6 bulan."
        const val TEXT_8 = "8. Berikan MPASI yang terbuat dari bahan-bahan alami dan kaya nutrisi."
        const val TEXT_9 = "9. Pastikan tekstur MPASI sesuai dengan usia dan kemampuan mengunyah bayi."
        const val TEXT_10 = "10. Berikan MPASI secara rutin dan bervariasi."
        const val TEXT_11 = "11. Imunisasi lengkap sesuai dengan jadwal yang dianjurkan."
        const val TEXT_12 = "12. Imunisasi membantu melindungi anak dari penyakit infeksi yang dapat mengganggu pertumbuhannya."
        const val TEXT_13 = "13. Cuci tangan dengan sabun dan air mengalir secara teratur."
        const val TEXT_14 = "14. Pastikan ketersediaan air bersih dan sanitasi yang baik."
        const val TEXT_15 = "15. Buang air besar pada tempat yang sehat."
        const val TEXT_16 = "16. Timbang berat badan dan ukur tinggi badan anak secara rutin di posyandu atau fasilitas kesehatan."
        const val TEXT_17 = "17. Periksa kehamilan secara rutin ke dokter atau bidan."
        const val TEXT_18 = "18. Konsultasikan ke dokter atau tenaga kesehatan lainnya jika anak mengalami masalah kesehatan atau gangguan pertumbuhan."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()
        // Binding bottom sheet dialog cegah stunting
        dialogBottomSheetCegahStunting = DialogBottomSheetCegahStuntingBinding.inflate(layoutInflater)

        binding.cvBumil.setOnClickListener {
            val intent = Intent(this, BumilActivity::class.java)
            startActivity(intent)
        }
        binding.cvCalonPengantin.setOnClickListener {
            val intent = Intent(this, CalonPengantinActivity::class.java)
            startActivity(intent)
        }
        binding.cvRemajaPutri.setOnClickListener {
            val intent = Intent(this, RemajaPutriActivity::class.java)
            startActivity(intent)
        }
        binding.cvLayananKeluarga.setOnClickListener {
            val intent = Intent(this, LayananKeluargaActivity::class.java)
            startActivity(intent)
        }
        binding.cvAnak.setOnClickListener {
            val intent = Intent(this, AnakActivity::class.java)
            startActivity(intent)
        }
        binding.cvCegahStunting.setOnClickListener {
            showBottomSheetDialogPencegahanStunting()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about -> {
                // Menu About
                customDialogAbout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun customDialogAbout() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomAboutBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        // Set text
        dialogBinding.tvDescription.text =
            "Jika anda mengalami permasalah pada aplikasi anda silahkan hubungi pembuat aplikasi " +d2
                    "dibawah ini, atau jika ingin ada yang ditanyakan karena proses pembuatan aplikasi" +
                    " ini masih tahap percobaan dan belum sebenuhnya sempurna.\nSilahkan hubungi Kotak dibawah ini :"
        // Link to linkedin
        dialogBinding.ivLinkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/hendri-mardani-1b6ba61a8/"
            linkToWebBrowser(url)
        }
        // Link to WA
        dialogBinding.ivWa.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=6281388372075"
            linkToWebBrowser(url)
        }
        // Link to Email
        dialogBinding.ivEmail.setOnClickListener {
            val url = "https://mail.google.com/mail/u/0/#inbox?compose=DmwnWrRvxmkxPlXNnrdQHFnTpMFwwGQFsRbBzClXRqRrMbKBBgxdXmgPxVTVNKmFGBHJTdgpsnsV"
            linkToWebBrowser(url)
        }
        // Link to Instagral
        dialogBinding.ivIg.setOnClickListener {
            val url = "https://www.instagram.com/hendri.mardani/"
            linkToWebBrowser(url)
        }
        dialogBinding.tvOk.setOnClickListener {
            // Close
            customDialog.dismiss()
        }
        // Display dialog
        customDialog.show()
    }

    private fun linkToWebBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun showBottomSheetDialogPencegahanStunting() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = dialogBottomSheetCegahStunting.root

        dialogBottomSheetCegahStunting.tv1.text = TEXT_1
        dialogBottomSheetCegahStunting.tv2.text = TEXT_2
        dialogBottomSheetCegahStunting.tv3.text = TEXT_3
        dialogBottomSheetCegahStunting.tv4.text = TEXT_4
        dialogBottomSheetCegahStunting.tv5.text = TEXT_5
        dialogBottomSheetCegahStunting.tv6.text = TEXT_6
        dialogBottomSheetCegahStunting.tv7.text = TEXT_7
        dialogBottomSheetCegahStunting.tv8.text = TEXT_8
        dialogBottomSheetCegahStunting.tv9.text = TEXT_9
        dialogBottomSheetCegahStunting.tv10.text = TEXT_10
        dialogBottomSheetCegahStunting.tv11.text = TEXT_11
        dialogBottomSheetCegahStunting.tv12.text = TEXT_12
        dialogBottomSheetCegahStunting.tv13.text = TEXT_13
        dialogBottomSheetCegahStunting.tv14.text = TEXT_14
        dialogBottomSheetCegahStunting.tv15.text = TEXT_15
        dialogBottomSheetCegahStunting.tv16.text = TEXT_16
        dialogBottomSheetCegahStunting.tv17.text = TEXT_17
        dialogBottomSheetCegahStunting.tv18.text = TEXT_18



        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbMain)
        supportActionBar!!.title = "Stunting"
        // Change font style text
        binding.tbMain.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.icon_stunting)
//        // Enable back button if you're in a child activity
//        if (supportActionBar != null) {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        }
//        binding.tbMain.setNavigationOnClickListener {
//            onBackPressed()
//        }
    }
}