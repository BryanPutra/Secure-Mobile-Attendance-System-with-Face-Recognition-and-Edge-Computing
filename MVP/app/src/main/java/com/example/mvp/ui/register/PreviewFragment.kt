package com.example.mvp.ui.register

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mvp.BitmapUtils
import com.example.mvp.Model
import com.example.mvp.databinding.FragmentPreviewBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream


class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private lateinit var previewViewModel: RegisterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        val root = _binding!!.root
        previewViewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
        _binding!!.previewIv.setImageBitmap(previewViewModel.imgBitmap)
        _binding!!.backButton.setOnClickListener(){
            previewViewModel.clearBitmap()
            val nextFrag = RegisterFragment()
            requireActivity().supportFragmentManager.beginTransaction().replace((requireView().parent as ViewGroup).id, nextFrag, "").addToBackStack(null).commit()
        }

        _binding!!.saveButton.setOnClickListener(){
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build()
            val faceDetector = FaceDetection.getClient(options)
            var bitmap = _binding!!.previewIv.drawable.toBitmap(600,800)
            bitmap = BitmapUtils.toGrayscale(bitmap)
            saveImage(bitmap,requireContext(),"Test")
            val inputImage = InputImage.fromBitmap(bitmap,0)

            faceDetector.process(inputImage).addOnSuccessListener { faces ->
                if(faces.size == 0){
                    Toast.makeText(activity,"No face detected",Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for(i in faces){
                    val bounds = i.boundingBox
                    try {
                        var croppedBitmap = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.width(), bounds.height())
                        croppedBitmap = Bitmap.createScaledBitmap(croppedBitmap,Model.modelInput,Model.modelInput,true)
                        Toast.makeText(activity,"Face successfully saved",Toast.LENGTH_SHORT).show()
                        val model = Model(requireContext())
                        model.registerFace(croppedBitmap)
                    } catch (ex: IllegalArgumentException){
                        Toast.makeText(activity,"Face too close to camera",Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    return@addOnSuccessListener
                }
            }
        }
        return root

    }

    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}