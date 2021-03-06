package com.example.supportbuddy_androidapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.example.supportbuddy_androidapp.data.AttachmentRepo
import com.example.supportbuddy_androidapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_camera.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private  var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private var isFrontCamera: Boolean = false
    private var picturePath: String = ""

    private lateinit var attachRepo : AttachmentRepo

    /**
     * Method that is ran when the activity runs.
     * @param savedInstanceState Bundle received by the method that runs the activity. Containing information.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_camera)

        supportActionBar?.hide()

        attachRepo = AttachmentRepo.get()

        //Camera permissions
        if (allPermissionsGranted()){
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS )
        }
        btnTakePhoto.setOnClickListener { onClickPhoto() }
        btnSavePhoto.setOnClickListener{ onClickSave() }
        btnExitPhoto.setOnClickListener{ onClickExit () }
        switchFlip.setOnCheckedChangeListener { compoundButton, b ->
            isFrontCamera = b
            startCamera()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /**
     * Adds image attachment to answer upon pressing Save after taking an image.
     */
    private fun onClickSave() {
        val attachmentObject = attachRepo.addAttachment(picturePath)

        if(attachmentObject.id > 0) {
            val data = Intent().apply { putExtra("attachmentId", attachmentObject.id) }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
        else {
            Toast.makeText(this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Captures image from the camera.
     * Unlocks the "Save" button, and runs the "setPicture" method.
     */
    private fun onClickPhoto() {
        btnSavePhoto.isEnabled = true

        val imageCapture = imageCapture?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues(). apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "failed to capture Photo: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    picturePath = "${output.savedUri}"

                    if(output.savedUri != null)
                        picturePath = getRealPathFromURI(output.savedUri!!)

                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    setPicture()
                }
            }
        )
    }

    /**
     * Method that takes the captured image, and puts it onto the screen.
     */
    private fun setPicture() {
        if (!picturePath.contentEquals("")) {
            val takenPictureTest = findViewById<ImageView>(R.id.takenPicture)
            viewFinder.visibility = View.INVISIBLE
            Log.d(TAG, "$picturePath: ER PATH2!!")
            val uri: Uri = Uri.parse(picturePath)
            println(uri)
            takenPictureTest.setImageURI(uri)
            takenPictureTest.visibility= View.VISIBLE
            Log.d(TAG, "$picturePath: ER PATH1!!")
        }
    }

    /**
     * Method that starts the camera. This method is called in onCreate.
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            if (isFrontCamera) {
                useFrontOrBackCamera(CameraSelector.DEFAULT_FRONT_CAMERA, cameraProvider, preview)

            } else {
                useFrontOrBackCamera(CameraSelector.DEFAULT_BACK_CAMERA, cameraProvider, preview)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    /**
     * Method that determines whether the user is using the back or front camera.
     * Changed by tapping the Switch on the camera screen.
     */
    private fun useFrontOrBackCamera(defaultBackCamera: CameraSelector, cameraProvider: ProcessCameraProvider, preview: Preview) {

        val cameraSelector = defaultBackCamera

        try {
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )

        } catch (exc: Exception) {
            Log.e(TAG, "Use case bindind failed", exc)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext,it ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Method that overrides the onDestroy normal functionality by also shutting down the cameraExecutor.
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "PhotoActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            }else {
                Toast.makeText(this,
                    "Permissions not granted by the user,",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    /**
     * Method that handles what happens when clicking on the exit button in the camera activity.
     */
    private fun onClickExit() {
        if (!picturePath.contentEquals("")) {
            picturePath = ""
            takenPicture.visibility = View.INVISIBLE
            viewFinder.visibility = View.VISIBLE
            btnSavePhoto.isEnabled = true
        } else {
            finish()
        }
    }

    /**
     * Method that gets the path of the newly taken photo.
     *
     * @param contentUri URI from the photo taken.
     *
     * @return result - The real image path when stored on the phone.
     */
    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result: String = cursor.getString(column_index)
        cursor.close()
        return result
    }
}