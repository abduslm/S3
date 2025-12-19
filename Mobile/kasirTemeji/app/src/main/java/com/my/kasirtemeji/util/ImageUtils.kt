package com.my.kasirtemeji.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import com.my.kasirtemeji.R

object ImageUtils {

    fun loadImageFromBase64(imageView: ImageView, base64String: String?) {
        if (base64String.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.es_teh)
            return
        }

        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.es_teh)
        }
    }
}