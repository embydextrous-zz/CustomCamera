package com.embydextrous.customcamera

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.fragment_preview.*
import java.io.File

class PreviewFragment : Fragment() {

    companion object {
        const val ARG_IMAGE_URI = "image_uri"
        const val ARG_ASPECT = "aspect"

        @JvmStatic
        fun newInstance(uri : String, aspect : Double) : PreviewFragment {
            val fragment = PreviewFragment()
            val bundle = Bundle()
            bundle.putString(ARG_IMAGE_URI, uri)
            bundle.putDouble(ARG_ASPECT, aspect)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var imageUri = ""
    private var aspect = 16.0/9.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = arguments.getString(ARG_IMAGE_URI)
        aspect = arguments.getDouble(ARG_ASPECT)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_preview, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView.layoutParams.height = (getScreenWidth(context) * aspect).toInt()
        imageView.requestLayout()
        val request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(File(imageUri)))
                .build()
        val controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request).setOldController(imageView.controller).build() as PipelineDraweeController
        imageView.controller = controller
        cameraButton.setOnClickListener { (activity as CameraActivity).showCamera() }
    }
}
