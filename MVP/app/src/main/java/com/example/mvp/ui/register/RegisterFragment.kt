package com.example.mvp.ui.register

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mvp.Model
import com.example.mvp.databinding.FragmentRegisterBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var imageCapture: ImageCapture? = null
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        registerViewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()


        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        _binding!!.imageCaptureButton.setOnClickListener { takePhoto() }

//        _binding!!.modelChangeButton.text = if(registerViewModel.getStatus() == 1) "FaceNet" else "MobileFaceNet"
//        _binding!!.modelChangeButton.setOnClickListener{
//            val status = registerViewModel.changeModel()
//            if(status == 1){
//                _binding!!.modelChangeButton.text = "FaceNet"
//
//            } else {
//                binding!!.modelChangeButton.text = "MobileFaceNet"
//            }
//            Model.changeModel(status)
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()), object:ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                }

                @SuppressLint("UnsafeOptInUsageError") override fun onCaptureSuccess(image: ImageProxy) {
                    registerViewModel.clearBitmap()
                    val newImage = image.image
                    val inputImage = InputImage.fromMediaImage(newImage!!,image.imageInfo.rotationDegrees)
                    registerViewModel.setBitmap(ImageConvertUtils.getInstance().getUpRightBitmap(inputImage))
                    val nextFrag = PreviewFragment()
                    requireActivity().supportFragmentManager.beginTransaction().replace((requireView().parent as ViewGroup).id, nextFrag, "").addToBackStack(null).commit()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(_binding!!.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY).build()

            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).toTypedArray()
    }
}