package com.liyaan.glide.request.target

import android.graphics.drawable.Drawable
import android.widget.ImageView

class DrawableImageViewTarget(
    view: ImageView, override val currentDrawable: Drawable? = view.drawable
) : ImageViewTarget<Drawable>(view) {
    override fun setResource(resource: Drawable?) {
        view.setImageDrawable(resource)
    }
}