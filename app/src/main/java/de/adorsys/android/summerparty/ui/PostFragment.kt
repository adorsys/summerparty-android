package de.adorsys.android.summerparty.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.ui.MainActivity.Companion.REQUEST_CODE_CAMERA_PERMISSION
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import de.adorsys.android.shared.FirebaseProvider
import de.adorsys.android.shared.Post
import de.adorsys.android.shared.views.ImageUtils

internal class PostFragment : Fragment() {
    interface OnGetPermissionsListener {
        fun onRequestPermission()
    }

    interface OnShowProgressListener {
        fun showProgress(show: Boolean = true, progress: Int? = null)
    }

    private var preferences: SharedPreferences? = null
    private lateinit var getPermissionsListener: PostFragment.OnGetPermissionsListener
    private lateinit var showProgressListener: OnShowProgressListener
    private lateinit var pictureImageView: ImageView
    private lateinit var postMainContainer: LinearLayout
    private lateinit var informedConsentContainer: ConstraintLayout
    private lateinit var photoEmptyContainer: LinearLayout
    private lateinit var pictureContainer: LinearLayout
    private lateinit var successContainer: LinearLayout
    private lateinit var uploadImageButton: Button
    private lateinit var descriptionEditText: TextInputEditText

    private var file: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        val agreeButton = view.findViewById<Button>(R.id.agree_button)
        preferences = activity?.getSharedPreferences(MainActivity.KEY_PREFS_FILENAME, Context.MODE_PRIVATE)
        postMainContainer = view.findViewById(R.id.post_container)
        informedConsentContainer = view.findViewById(R.id.informed_consent_container)
        photoEmptyContainer = view.findViewById(R.id.photo_empty_container)
        pictureContainer = view.findViewById(R.id.picture_container)
        pictureImageView = view.findViewById(R.id.picture_image_view)
        successContainer = view.findViewById(R.id.success_upload_container)


        val toolbar = activity!!.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitle(R.string.postTitle)

        if (isInformedConsent()) {
            hideInformedContainer()
        }

        agreeButton.setOnClickListener {
            setInformedConsent()
        }

        photoEmptyContainer.setOnClickListener {
            openCamera()
        }

        descriptionEditText = view.findViewById(R.id.description_edit_text)
        uploadImageButton = view.findViewById(R.id.upload_image_button)
        pictureImageView.setOnClickListener()
        {
            openCamera()
        }
        uploadImageButton.setOnClickListener { _ ->
            if (file != null) {
                FirebaseProvider.uploadImage(
                        file!!,
                        { showProgressListener.showProgress(true, it) },
                        { createPost(it) },
                        { Log.e("TAG_IMAGE_UPLOAD", it.message) })
            } else {
                Log.e(javaClass.name, "file is null")
            }
        }

        return view
    }

    private fun createPost(storageReference: String?) {
        Log.d("TAG_IMAGE_UPLOAD", storageReference)

        val name = preferences?.getString(MainActivity.KEY_USER_NAME, null)
        val text = descriptionEditText.text.toString()


        showProgressListener.showProgress(true)

        val post = Post(UUID.randomUUID().toString(), name = name, imageReference = storageReference, text = text)
        FirebaseProvider.createPost(
                post,
                {
                    showSuccessScreen()
                    descriptionEditText.postDelayed({
                        updateView()
                    }, 4000)
                },
                { Log.e("TAG_IMAGE_UPLOAD", it.message) })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.getPermissionsListener = context as OnGetPermissionsListener
            this.showProgressListener = context as OnShowProgressListener
        } catch (e: ClassCastException) {
            IllegalStateException("Your activity has to implement OnGetPermissionsListener")
        }
    }

    internal fun openCamera() {
        if (PermissionManager.permissionPending(context!!, Manifest.permission.CAMERA)) {
            getPermissionsListener.onRequestPermission()
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context!!.packageManager) != null) {
                file = createFile(context!!, ".jpg")
                try {
                    file?.let {
                        // Compress bitmap
                        setScaledImage(it.absolutePath, "1920".toFloat())
                        val currentPhotoUri = FileProvider.getUriForFile(
                                context!!, "de.adorsys.android.summerparty",
                                it)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                        activity!!.startActivityForResult(intent, REQUEST_CODE_CAMERA_CAPTURE)
                    }
                } catch (e: Exception) {
                    Log.e(javaClass.name, e.message)
                }
            }
        }
    }

    private fun setInformedConsent() {
        preferences!!.edit().putBoolean(KEY_IS_INFORMED_CONSENT, true).apply()
        hideInformedContainer()
    }

    private fun hideInformedContainer() {
        postMainContainer.visibility = VISIBLE
        informedConsentContainer.visibility = GONE
    }

    private fun isInformedConsent(): Boolean {
        if (!preferences!!.contains(KEY_IS_INFORMED_CONSENT)) {
            return false
        }
        return true
    }

    private fun createFile(context: Context, suffix: String): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "${timeStamp}_"
            val storageDir = context.filesDir
            val image: File? = try {
                File.createTempFile(
                        imageFileName, /* prefix */
                        suffix,        /* suffix */
                        storageDir)    /* directory */
            } catch (e: Exception) {
                Log.e(javaClass.name, e.message)
                null
            }

            image
        } catch (e: IOException) {
            Log.e(javaClass.name, e.message)
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAMERA_CAPTURE
                && resultCode == Activity.RESULT_OK) {
            showImageView()
            setScaledImage(file?.path, pictureImageView.resources.getDimension(R.dimen.image_size))
        }

        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION
                && resultCode == Activity.RESULT_OK) {
            openCamera()
        }
    }

    private fun setScaledImage(filePath: String?, imageViewSize: Float) {
        filePath?.let {
            pictureImageView.setImageBitmap(ImageUtils.getScaledImage(imageViewSize, it))
        }
    }

    private fun hideImageView() {
        photoEmptyContainer.visibility = VISIBLE
        pictureContainer.visibility = GONE
    }

    private fun showImageView() {
        photoEmptyContainer.visibility = GONE
        pictureContainer.visibility = VISIBLE

        uploadImageButton.background = resources.getDrawable(R.drawable.button_background, activity?.theme)
        uploadImageButton.isEnabled = true
    }

    private fun showSuccessScreen() {
        showProgressListener.showProgress(false)
        successContainer.visibility = VISIBLE
        postMainContainer.visibility = GONE

        uploadImageButton.background = resources.getDrawable(R.drawable.button_background_disabled, activity?.theme)
        uploadImageButton.isEnabled = false
    }

    private fun updateView() {
        postMainContainer.visibility = VISIBLE
        descriptionEditText.setText("")
        successContainer.visibility = GONE
        fragmentManager?.popBackStack()
    }

    companion object {
        private const val KEY_IS_INFORMED_CONSENT = "is_informed_consent"
        const val REQUEST_CODE_CAMERA_CAPTURE: Int = 944
    }
}
