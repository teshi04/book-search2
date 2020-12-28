package jp.tsur.booksearchscan2

import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import jp.tsur.booksearchscan2.databinding.FragmentScanBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment(R.layout.fragment_scan) {

    private lateinit var binding: FragmentScanBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScanBinding.bind(view)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setUpCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    private fun setUpCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        cameraProviderFuture.get().unbindAll()

        val preview = Preview.Builder().build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        try {
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
        } catch (e: Exception) {
            // TODO
        }

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
    }
}
