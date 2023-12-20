package com.alvintio.pedulipangan.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.ActivityExpirationCheckerBinding
import com.alvintio.pedulipangan.databinding.ActivityExpirationCheckerFruitBinding
import com.alvintio.pedulipangan.ml.FoodPrediction
import com.alvintio.pedulipangan.ml.MyCnnModel2
import com.alvintio.pedulipangan.util.ViewUtils
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class ExpirationCheckerFruitActivity : AppCompatActivity() {
    companion object {
        private val REQUIRED_CAMERA_PERMISS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISS = 101
    }

    private lateinit var binding: ActivityExpirationCheckerFruitBinding

    private var getFile: File? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = it.data?.data
            selectedImageUri?.let { uri ->
                getFile = File(getPathFromUri(uri))
                binding.ivProductImage.setImageURI(uri)
            }
        }
    }

    private fun getPathFromUri(uri: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(filePathColumn[0])
                return it.getString(columnIndex)
            }
        }
        return ""
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            getFile?.let { image ->
                val bitmap = BitmapFactory.decodeFile(image.path)
                rotateImage(bitmap, image.path).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(image)
                )
                binding.ivProductImage.setImageBitmap(rotateImage(bitmap, image.path))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpirationCheckerFruitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnCamera.setOnClickListener {
                if (!checkImagePermission()) {
                    ActivityCompat.requestPermissions(
                        this@ExpirationCheckerFruitActivity,
                        REQUIRED_CAMERA_PERMISS,
                        REQUEST_CODE_PERMISS
                    )
                } else {
                    startCamera()
                }
            }

            btnGallery.setOnClickListener {
                openGallery()
            }

            binding.startDetection.setOnClickListener {
                startDetection()
            }
        }

        ViewUtils.setupFullScreen(this)
    }

    private fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        val orientation = ExifInterface(path).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val customTempFile = File.createTempFile(
            SimpleDateFormat(
                "dd-MMM-yyyy",
                Locale.US
            ).format(System.currentTimeMillis()), ".jpg", storageDir
        )
        getFile = customTempFile
        val photoURI: Uri = FileProvider.getUriForFile(
            this@ExpirationCheckerFruitActivity,
            "com.alvintio.pedulipangan.fileprovider",
            customTempFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val options = arrayOf("File Manager", "Galeri")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Sumber Gambar")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openFileManager()
                1 -> openImageGallery()
            }
        }
        builder.show()
    }

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"

        if (intent.resolveActivity(packageManager) != null) {
            galleryLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Tidak ada aplikasi file manager yang tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImageGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        if (intent.resolveActivity(packageManager) != null) {
            galleryLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Tidak ada aplikasi galeri yang tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkImagePermission() = REQUIRED_CAMERA_PERMISS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startDetection() {
        if (getFile != null) {
            val model = MyCnnModel2.newInstance(this)
            val foodsDisease = getFoodDisease()

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 150, 150, 3), DataType.FLOAT32)

            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val resize = Bitmap.createScaledBitmap(bitmap, 150, 150, true)

            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resize)

            inputFeature0.loadBuffer(tensorImage.buffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var maxIndex = 0
            var maxValue = outputFeature0.getFloatValue(0)
            for (i in 0 until 2) {
                val value = outputFeature0.getFloatValue(i)
                if (value > maxValue) {
                    maxValue = value
                    maxIndex = i
                }
            }

            val foodDisease = foodsDisease[maxIndex]

            binding.foodDisease.text = foodDisease

        } else {
            Toast.makeText(this, getString(R.string.input_img), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFoodDisease(): List<String> {
        val inputString = this.assets.open("labelfruit.txt").bufferedReader().use {
            it.readText()
        }

        return inputString.split("\n")
    }
}

