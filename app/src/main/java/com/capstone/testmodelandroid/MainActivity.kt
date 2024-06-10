package com.capstone.testmodelandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.testmodelandroid.databinding.ActivityMainBinding
import com.capstone.testmodelandroid.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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

        binding.button.setOnClickListener {
            processInputToModel()
        }
    }

    private fun processInputToModel() {
        val input1 = binding.textInputLayout.editText?.text.toString()
        val input2 = binding.textInputLayout2.editText?.text.toString()
        val input3 = binding.textInputLayout3.editText?.text.toString()

        val combinedInputUser = "$input1,$input2,$input3"

        try {
            val model = Model.newInstance(this)

            val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.STRING)
            val byteBuffer = ByteBuffer.wrap(combinedInputUser.toByteArray(StandardCharsets.UTF_8))
            inputBuffer.loadBuffer(byteBuffer)

            val outputModel = model.process(inputBuffer)
            val outputBuffer = outputModel.outputFeature0AsTensorBuffer

            val outputArrayModel = outputBuffer.floatArray
            val recipes = outputArrayModel.map { it.toString() }.toTypedArray()

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(RECIPES, recipes)
            startActivity(intent)
            model.close()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val RECIPES = "recipes"
    }
}