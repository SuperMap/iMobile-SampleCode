package com.sercant.basicsegmentation

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.TextureView
import android.view.View

/** A [TextureView] that can be adjusted to a specified aspect ratio.  */
class AutoFitTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TextureView(context, attrs, defStyle) {

    private var mRatioWidth = 0
    private var mRatioHeight = 0

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that is,
     * calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width Relative horizontal size
     * @param height Relative vertical size
     */
    fun setAspectRatio(width: Int, height: Int) {
        if (width < 0 || height < 0) {
            throw IllegalArgumentException("Size cannot be negative.")
        }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }

    override fun getBitmap(width: Int, height: Int): Bitmap {
        var w = getWidth()
        var h = getHeight()

        if (h > w) {
            h = (width.toFloat() / w * h).toInt()
            w = width
        } else {
            w = (height.toFloat() / h * w).toInt()
            h = height
        }

        val bigBitmap = super.getBitmap(w, h)
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        if (bigBitmap != null) {
            val pixels = IntArray(width * height)

            val xPad = (bigBitmap.width - width) / 2
            val yPad = (bigBitmap.height - height) / 2

            bigBitmap.getPixels(pixels, 0, w, xPad, yPad, width, height)
            result.setPixels(pixels, 0, width, 0, 0, width, height)
        }

        return result
    }
}
