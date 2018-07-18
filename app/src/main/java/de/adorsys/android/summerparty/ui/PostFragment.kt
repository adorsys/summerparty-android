package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import de.adorsys.android.summerparty.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

internal class PostFragment : Fragment() {
    interface OnGetPermissionsListener {
        fun onGetPermission()
    }

    private lateinit var listener: PostFragment.OnGetPermissionsListener
    private lateinit var preferences: SharedPreferences
    private lateinit var pictureImageView: ImageView
    private lateinit var postMainContainer: LinearLayout
    private lateinit var informedConsentContainer: LinearLayout
    private lateinit var photoEmptyContainer: LinearLayout
    private lateinit var pictureContainer: LinearLayout
    private lateinit var successContainer: LinearLayout
    private lateinit var uploadImageButton: Button
    private lateinit var descriptionEditText: TextInputEditText

    private var mCurrentPhotoPath: String = ""
    private val IS_INFORMED_CONSENT = "is_informed_consent"
    private var file: File? = null
    // Initialize the handler instance
    private var mHandler = Handler()
    private val REQUEST_CODE_CAMERA: Int = 942


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        val agreeButton = view.findViewById(R.id.agree_button) as Button
        preferences = context!!.getSharedPreferences(MainActivity.KEY_PREFS_FILENAME, Context.MODE_PRIVATE)
        postMainContainer = view.findViewById(R.id.post_container) as LinearLayout
        informedConsentContainer = view.findViewById(R.id.informed_consent_container) as LinearLayout
        photoEmptyContainer = view.findViewById(R.id.photo_empty_container) as LinearLayout
        pictureContainer = view.findViewById(R.id.picture_container) as LinearLayout
        pictureImageView = view.findViewById(R.id.picture_image_view) as ImageView
        successContainer = view.findViewById(R.id.success_upload_container) as LinearLayout


        if (isInformedConsent()) {
            hideInformedContainer()
        }

        agreeButton.setOnClickListener {
            setInformedConsent()
        }

        photoEmptyContainer.setOnClickListener {
            openCamera()
        }

        descriptionEditText = view.findViewById(R.id.description_edit_text) as TextInputEditText
        uploadImageButton = view.findViewById(R.id.upload_image_button) as Button
        uploadImageButton.setOnClickListener({
            val description = descriptionEditText.text.toString()
            if ( file != null) {
                var imageAsString: String? = getEncodedBytesFromBitmap(BitmapFactory.decodeFile(file?.path))

                setSuccessScreen()
                mHandler.postDelayed({
                    updateView()
                }, 10000)
            } else {


            }
        })

        return view
        updateView();
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.listener = context as OnGetPermissionsListener
        } catch (e: ClassCastException) {
            IllegalStateException("Your activity has to implement OnGetPermissionsListener")
        }
    }

    private fun setInformedConsent() {
        (preferences as SharedPreferences).edit().putBoolean(IS_INFORMED_CONSENT, true).apply()
        hideInformedContainer()
    }

    private fun hideInformedContainer() {
        postMainContainer?.visibility = VISIBLE
        informedConsentContainer?.visibility = GONE
    }

    private fun isInformedConsent(): Boolean {
        if (!preferences!!.contains(IS_INFORMED_CONSENT)) {
            return false
        }
        return true
    }

    private fun openCamera() {
        listener.onGetPermission()

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            file = createFile(context!!, ".jpg")
            try{
                file?.let {
                    val currentPhotoUri = FileProvider.getUriForFile(
                            context!!, "de.adorsys.android.summerparty",
                            it)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                    getActivity()!!.startActivityForResult(intent, REQUEST_CODE_CAMERA)
                }
            }
            catch(e: Exception){
                //Log
            }

        }

    }

    private fun createFile(context: Context, suffix: String): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "${timeStamp}_"
            val storageDir = context.filesDir
            val image: File? = try {
                File.createTempFile(
                        imageFileName, /* prefix */
                        suffix, /* suffix */
                        storageDir)   /* directory */
            } catch (e: Exception) {
                null
            }

            image
        } catch (e: IOException) {
            //Log
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            showImageView();
            setThumbnailImage(file?.path)
        }
    }

    private fun setThumbnailImage(filePath: String?) {
        filePath?.let {
            try {

                val imageViewSize = pictureImageView.resources.getDimension(R.dimen.image_size)
                // Get the dimensions of the bitmap
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(it, options)
                val photoW = options.outWidth
                val photoH = options.outHeight

                // Determine how much to scale down the image
                val scaleFactor = Math.min(photoW / imageViewSize, photoH / imageViewSize)

                // Decode the image file into a Bitmap sized to fill the View
                options.inJustDecodeBounds = false
                options.inSampleSize = Math.round(scaleFactor)

                pictureImageView.setImageBitmap(BitmapFactory.decodeFile(it, options))
            } catch (e: Exception) {
                //Log
            }
        }
    }

    private fun hideImageView(){
        photoEmptyContainer?.visibility = VISIBLE
        pictureContainer?.visibility = GONE
    }

    private fun showImageView(){
        photoEmptyContainer?.visibility = GONE
        pictureContainer?.visibility = VISIBLE

        uploadImageButton.background = getResources().getDrawable(R.drawable.button_background)
        uploadImageButton.isEnabled = true;
    }

    private fun getEncodedBytesFromBitmap(bitmap: Bitmap): String? {
        return try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun setSuccessScreen() {
        successContainer.visibility = VISIBLE
        postMainContainer.visibility = GONE
    }

    private fun updateView(){
        postMainContainer.visibility = VISIBLE
        descriptionEditText.setText("")
        successContainer.visibility = GONE
        hideImageView()
    }


}
