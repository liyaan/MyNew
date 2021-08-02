package com.liyaan.glide.load.resource.bitmap

import android.os.Build
import com.liyaan.glide.load.Option

abstract class DownsampleStrategy {


    companion object{
        val CENTER_OUTSIDE: DownsampleStrategy = CenterOutside()
        val FIT_CENTER: DownsampleStrategy = FitCenter()
        val DEFAULT: DownsampleStrategy = CENTER_OUTSIDE
        val OPTION = Option.memory(
            "com.bumptech.glide.load.resource.bitmap.Downsampler.DownsampleStrategy",
            DEFAULT
        )
        val IS_BITMAP_FACTORY_SCALING_SUPPORTED =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }
    abstract fun getSampleSizeRounding(
        sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
    ): SampleSizeRounding

    abstract fun getScaleFactor(
        sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
    ): Float

    enum class SampleSizeRounding {
        MEMORY,
        QUALITY
    }

    private class CenterOutside  internal constructor() : DownsampleStrategy() {
        override fun getScaleFactor(
            sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
        ): Float {
            val widthPercentage = requestedWidth / sourceWidth.toFloat()
            val heightPercentage = requestedHeight / sourceHeight.toFloat()
            return Math.max(widthPercentage, heightPercentage)
        }

        override fun getSampleSizeRounding(
            sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
        ): SampleSizeRounding {
            return SampleSizeRounding.QUALITY
        }
    }

    private class CenterInside  internal constructor() : DownsampleStrategy() {
        override fun getScaleFactor(
            sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
        ): Float {
            return Math.min(
                1f,
                DownsampleStrategy.FIT_CENTER.getScaleFactor(
                    sourceWidth,
                    sourceHeight,
                    requestedWidth,
                    requestedHeight
                )
            )
        }

        override fun getSampleSizeRounding(
            sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
        ): SampleSizeRounding {
            return if (getScaleFactor(
                    sourceWidth,
                    sourceHeight,
                    requestedWidth,
                    requestedHeight
                ) == 1f
            ) SampleSizeRounding.QUALITY else DownsampleStrategy.FIT_CENTER.getSampleSizeRounding(
                sourceWidth, sourceHeight, requestedWidth, requestedHeight
            )
        }
    }

    private class FitCenter  internal constructor() : DownsampleStrategy() {
        override fun getScaleFactor(
            sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
        ): Float {
            return if (DownsampleStrategy.IS_BITMAP_FACTORY_SCALING_SUPPORTED) {
                val widthPercentage = requestedWidth / sourceWidth.toFloat()
                val heightPercentage = requestedHeight / sourceHeight.toFloat()
                Math.min(widthPercentage, heightPercentage)
            } else {
                // Similar to AT_LEAST, but only require one dimension or the other to be >= requested
                // rather than both.
                val maxIntegerFactor =
                    Math.max(sourceHeight / requestedHeight, sourceWidth / requestedWidth)
                if (maxIntegerFactor == 0) 1f else 1f / Integer.highestOneBit(
                    maxIntegerFactor
                )
            }
        }

        override fun getSampleSizeRounding(
            sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int
        ): SampleSizeRounding {
            return if (DownsampleStrategy.IS_BITMAP_FACTORY_SCALING_SUPPORTED) {
                SampleSizeRounding.QUALITY
            } else {
                SampleSizeRounding.MEMORY
            }
        }
    }

}