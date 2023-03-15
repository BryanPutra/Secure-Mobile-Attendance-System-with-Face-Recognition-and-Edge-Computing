package com.example.mvp.ui.register

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mvp.Model
import com.example.mvp.databinding.FragmentPreviewBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions


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

            var bitmap = _binding!!.previewIv.drawable.toBitmap(_binding!!.previewIv.width,_binding!!.previewIv.height)
            bitmap = Model.toGrayscale(bitmap)
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
}