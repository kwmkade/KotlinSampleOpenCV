package com.kwmkade.kotlinsampleopencv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kwmkade.kotlinsampleopencv.databinding.FragmentFirstBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mArgs: FirstFragmentArgs by navArgs()

    private var mDetector: CascadeClassifier? = null
    private lateinit var mLoader: CascadeClassifierLoader
    private val mRectColor = Scalar(255.0, 0.0, 0.0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        mLoader = CascadeClassifierLoader(requireContext(), resources)
        lifecycleScope.launch {
            mLoader.Load(
                if (mArgs.classifierId > 0) mArgs.classifierId else R.raw.haarcascade_frontalface_alt2
            ).collect { cascadeClassifier ->
                mDetector = cascadeClassifier
            }
        }

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
        binding.cameraView.setCvCameraViewListener(object :
            CameraBridgeViewBase.CvCameraViewListener2 {

            private var mRgba: Mat? = null
            private var mGrey: Mat? = null

            override fun onCameraViewStarted(width: Int, height: Int) {}

            override fun onCameraViewStopped() {
                mRgba?.release()
                mGrey?.release()
            }

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                // このメソッド内で画像処理. 今回はポジネガ反転.
                mRgba = requireNotNull(inputFrame).rgba()
                mGrey = requireNotNull(inputFrame).gray()

                if (mDetector is CascadeClassifier) {
                    var detections = MatOfRect()
                    mDetector!!.detectMultiScale(mRgba, detections)
                    for (react in detections.toArray()) {
                        val x1 = react.x.toDouble()
                        val y1 = react.y.toDouble()
                        val x2 = (react.x + react.width).toDouble()
                        val y2 = (react.y + react.height).toDouble()
                        Imgproc.rectangle(
                            mRgba, Point(x1, y1), Point(x2, y2), mRectColor, 5
                        )
                    }
                }
                return requireNotNull(mRgba)
            }
        })

        // プレビューを有効にする
        binding.cameraView.setCameraPermissionGranted()
        binding.cameraView.enableView()
    }
}