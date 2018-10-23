package de.adorsys.android.summerpartysocial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity(), FeedFragment.OnShowDetailListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val feedFragment = FeedFragment()
        val orderStateFragment = OrderStateFragment()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_start, feedFragment)
                .replace(R.id.container_end, orderStateFragment)
                .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            deleteFiles(cacheDir)
        }
    }

    private fun deleteFiles(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        deleteFiles(f)
                    } else {
                        f.delete()
                    }
                }
            }
        }
        return file.delete()
    }

    override fun onShowDetailedImage(imageReference: String) {
        val fragment = ImageDetailFragment()
        val bundle = Bundle()
        bundle.putString(ImageDetailFragment.EXTRA_IMAGE_REFERENCE, imageReference)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
                .add(R.id.container_start, fragment)
                .addToBackStack(ImageDetailFragment.TAG)
                .commit()
    }
}
