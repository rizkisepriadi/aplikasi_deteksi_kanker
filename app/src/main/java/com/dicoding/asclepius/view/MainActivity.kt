package com.dicoding.asclepius.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.ViewModel.MainViewModel
import com.dicoding.asclepius.ViewModel.ViewModelFactory
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAXIMAL_SIZE = 1000000

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this) as ViewModelProvider.Factory
    }
    private var currentImageUri: Uri? = null

    private var requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast("Permission request granted")
        } else {
            showToast("Permission request denied")
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (!allPermissionGranted()) {
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
        binding.buttonNews.setOnClickListener { openNews() }
        binding.buttonHistory.setOnClickListener { openHistory() }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun openNews() {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    private fun openHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)?.reduceFileImage() // Konversi Uri ke File
            Log.d("Image Classification File", "Show Image: ${imageFile?.path}")

            imageClassifierHelper = ImageClassifierHelper(context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResult(result: List<Classifications>) {
                        mainViewModel.insertHistory(uri, result)
                        result?.let { it ->
                            if (it.isNotEmpty()) {
                                Log.d("Analysis Image", "Show Analysis : $result")
                                val category = it[0].categories[0]
                                val displayResult =
                                    "${category.label} " + NumberFormat.getPercentInstance()
                                        .format(category.score).trim()
                                moveToResult(uri, displayResult)
                            } else {
                                showToast("No classifications found")
                            }
                        }
                    }
                })

            if (imageFile != null) {
                imageClassifierHelper.classifyStaticImage(imageFile)
            } else {
                Log.e("AnalyzeImage", "Image file is null, cannot analyze.")
                showToast("Image file is null, cannot analyze.")
            }
        } ?: Log.e("AnalyzeImage", "Current Image URI is null")

        mainViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showToast(errorMessage)
            }
        }

    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("JPEG_$timeStamp", ".jpg", filesDir)
    }

    private fun uriToFile(imageUri: Uri, context: Context): File? {
        val myFile = createCustomTempFile(context)
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(myFile)
            val buffer = ByteArray(1024)
            var length: Int
            inputStream?.use { input ->
                while (input.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
            outputStream.close()
            myFile
        } catch (e: Exception) {
            Log.e("uriToFile", "Error converting URI to file: ${e.message}")
            null
        }
    }

    private fun moveToResult(uri: Uri, result: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
        intent.putExtra(ResultActivity.EXTRA_RESULT, result)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun File.reduceFileImage(): File {
        val bitmap = BitmapFactory.decodeFile(this.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))

        return this
    }
}
