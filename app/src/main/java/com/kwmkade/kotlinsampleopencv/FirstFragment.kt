package com.kwmkade.kotlinsampleopencv

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kwmkade.kotlinsampleopencv.databinding.FragmentFirstBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        OpenCVLoader.initDebug()  // ← OpenCVライブラリ読込
        initCamera()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initCamera() {

        // リスナ設定
        binding.cameraView.setCvCameraViewListener(object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) { }

            override fun onCameraViewStopped() { }

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                // このメソッド内で画像処理. 今回はポジネガ反転.
                val mat = requireNotNull(inputFrame).rgba()
                Core.bitwise_not(mat, mat)
                return mat
            }
        })

        // プレビューを有効にする
        binding.cameraView.setCameraPermissionGranted()
        binding.cameraView.enableView()
    }
}