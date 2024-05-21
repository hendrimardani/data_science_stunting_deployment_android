package com.example.stunting

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.Database.BabyApp
import com.example.stunting.Database.BabyDao
import com.example.stunting.Database.BabyEntity
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.ml.ModelStunting
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.IOException
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Call database
        val babyDao = (application as BabyApp).db.babyDao()

        binding.btnSubmit.setOnClickListener {
            val umur = binding.etUmur.text.toString()
            val jk = binding.etJk.text.toString()
            val tinggi = binding.etTinggi.text.toString()

            if (umur.isNotEmpty() && jk.isNotEmpty() && tinggi.isNotEmpty() ) {
                // Get data
                val umurPred = umur.toFloat()
                val jkPred = jk.toFloat()
                val tinggiPred = tinggi.toFloat()
                prediction(babyDao, umurPred, jkPred, tinggiPred)
            } else {
                toastInfo("Gagal ☹️",
                    "Tidak boleh ada data yang kosong !", MotionToastStyle.ERROR)
            }
        }
        // Get all items
        getAll(babyDao)
    }

    private fun getAll(babyDao: BabyDao) {
        lifecycleScope.launch {
            babyDao.fetchAllbabies().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }
    private fun setupListOfDataIntoRecyclerView(babyList: ArrayList<BabyEntity>) {
        if (babyList.isNotEmpty()) {
            val mainAdapter = MainAdapter(babyList)

            // Count item list
            val countItem = babyList.size

            binding.rvItemList.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
            binding.rvItemList.adapter = mainAdapter
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun prediction(babyDao: BabyDao, umur: Float, jk: Float, tinggi: Float) {
        // Normalisasi data with formula => (Xi - Xmin) / (Xmax - Xmin)
        val umurNormalized = (umur - 0f) / (60f - 0f)
        val tinggiNormalized = (tinggi - 40.01f) / (128f - 40.0104370037594f)

        try { // this is apropriated by input and output value, 3 * 4 It depends on memory allocated
            val byteBuffer = ByteBuffer.allocateDirect(3 * 4)
                byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.putFloat(umurNormalized)
            byteBuffer.putFloat(jk)
            byteBuffer.putFloat(tinggiNormalized)

            val model = ModelStunting.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            lifecycleScope.launch {
                babyDao.insert(BabyEntity(umur=umur.toString(), jenisKelamin=jk.toString(), tinggi=tinggi.toString()))
                toastInfo("Data tersimpan !", "Data OK !!", MotionToastStyle.SUCCESS)

                // Clear the text when data saved !!! (success)
                binding.etUmur.text!!.clear()
                binding.etJk.text!!.clear()
                binding.etTinggi.text!!.clear()
            }

            // If the result value is less than 0.5 then Normal, otherwise is Stunting
            if ( Math.round(outputFeature0[0]) < 0.5 ) {
                // Toast

            } else {
                toastInfo("Kondisi bayi anda terkena stunting ☹️",
                    "Perbaiki lagi asupan gizi anaknya !!!", MotionToastStyle.ERROR)
            }
            // Releases model resources if no longer used.
            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    private fun toastInfo(title: String, description: String, info: MotionToastStyle) {
        MotionToast.createToast(this,
            title,
            description,
            info,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold))
    }
}