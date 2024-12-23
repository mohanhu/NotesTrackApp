package com.example.notestrack.richlib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.notestrack.R
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

object LoadBottomGlide {
    fun BottomNavigationView.loadGlideMenu(context: Context,menuId: Int) {

        itemIconTintList = null

        val menuItem = menu.findItem(menuId)
        Glide.with(context)
            .asBitmap().override(800, 600).diskCacheStrategy(DiskCacheStrategy.ALL)
            .optionalCircleCrop()
            .load("https://themeisle.com/blog/wp-content/uploads/2024/06/Online-Image-Optimizer-Test-Image-JPG-Version.jpeg")
            .placeholder(R.drawable.ic_home_selector)
            .error(R.drawable.ic_home_selector)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val drawable = BitmapDrawable(context.resources, resource)
                    println("BottomNavigationView.loadGlideMenu done >>$drawable")
                    menuItem.icon = drawable
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle if needed
                    println("BottomNavigationView.loadGlideMenu clear")
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    println("BottomNavigationView.loadGlideMenu fail")
                }
            })

    }

    @SuppressLint("RestrictedApi")
    fun BottomNavigationView.resizeMenuIcon(){
        val menuView = getChildAt(0) as BottomNavigationMenuView
        val iconView = menuView.getChildAt(1)
            .findViewById<View>(com.google.android.material.R.id.navigation_bar_item_icon_view)
        val layoutParams = iconView.layoutParams
        val displayMetrics = resources.displayMetrics
        val newSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, displayMetrics).toInt()
        layoutParams.height = newSize
        layoutParams.width = newSize

        val fl = FrameLayout.LayoutParams(newSize, newSize, Gravity.CENTER)
        iconView.layoutParams = fl
    }

}