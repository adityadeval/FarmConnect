package ms.cs.farmconnect.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import ms.cs.farmconnect.R
import java.io.IOException

class GlideLoader(val context: Context) {

    // Below function takes two simple arguments :
    // Location of the image in our device's storage and imageView in which we want to display it.
    fun loadUserPicture(imageURI: Uri, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(imageURI) // URI of the image
                .centerCrop() // Scale and center the image.
                .placeholder(R.drawable.ic_user_placeholder) // A default place holder if our image at the given URI fails to load.
                .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}