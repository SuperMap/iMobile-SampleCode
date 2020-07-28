package com.sercant.basicsegmentation

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Utility functions
 *
 * @author  sercant
 * @date 2019-06-08
 */
class Utils {
    companion object {
        fun segmentResultToBitmap(
                segmentedImage: IntArray,
                classColors: Array<Int>,
                targetBitmap: Bitmap
        ) {
            targetBitmap.apply {
                val pixels = IntArray(width * height) {
                    classColors[segmentedImage[it]]
                }

                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        }

        fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
            val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            if (bitmap.width != bitmap.height)
                throw Error("Mask expected to be square but got something else")
            // Expects image to be square shape
            val scaledBitmap = if (height > width)
                Bitmap.createScaledBitmap(bitmap, width, width, true)
            else
                Bitmap.createScaledBitmap(bitmap, height, height, true)

            val pX = (width - scaledBitmap.width) / 2
            val pY = (height - scaledBitmap.height) / 2
            val can = Canvas(result)
            // can.drawARGB(0x00, 0xff, 0xff, 0xff)
            can.drawBitmap(scaledBitmap, pX.toFloat(), pY.toFloat(), null)
            // scaledBitmap.recycle()

            return result
        }
    }
}