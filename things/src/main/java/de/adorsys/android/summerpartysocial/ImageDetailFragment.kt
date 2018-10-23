package de.adorsys.android.summerpartysocial

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import de.adorsys.android.shared.FirebaseProvider
import de.adorsys.android.shared.views.ImageUtils
import kotlinx.android.synthetic.main.fragment_image_detail.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class ImageDetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageReference = arguments?.getString(EXTRA_IMAGE_REFERENCE)

        detail_image_view.alpha = 0F

        close_image_container.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        FirebaseProvider.downloadImage(
                imageReference,
                { file ->
                    launch {
                        val windowManager = activity?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                        val display = windowManager?.defaultDisplay
                        val size = Point()
                        display?.getSize(size)

                        val bitmap = ImageUtils.getScaledImage(size.y.toFloat(), file.path)
                        launch(UI) {
                            detail_image_view?.setImageBitmap(bitmap)
                            detail_image_view?.animate()?.alpha(1F)?.start()
                        }
                    }
                },
                {
                    Log.e("TAG_THINGS", "Could not correctly decode bitmap for $imageReference")
                })

    }

    companion object {
        const val EXTRA_IMAGE_REFERENCE = "extra_image_reference"
        const val TAG = "detail_image_fragment"
    }
}
