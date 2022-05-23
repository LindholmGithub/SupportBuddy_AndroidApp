package com.example.supportbuddy_androidapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.ListView
import com.bumptech.glide.Glide
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL


class UIUtils {
    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    fun setListViewHeightBasedOnItems(listView: ListView): Boolean {
        val listAdapter: ListAdapter = listView.getAdapter()
        return if (listAdapter != null) {
            val numberOfItems: Int = listAdapter.getCount()

            // Get total height of all items.
            var totalItemsHeight = 0
            for (itemPos in 0 until numberOfItems) {
                val item: View = listAdapter.getView(itemPos, null, listView)
                item.measure(0, 0)
                totalItemsHeight += item.getMeasuredHeight()
            }

            // Get total height of all item dividers.
            val totalDividersHeight: Int = listView.getDividerHeight() *
                    (numberOfItems - 1)

            // Set list height.
            val params: ViewGroup.LayoutParams = listView.getLayoutParams()
            params.height = totalItemsHeight + totalDividersHeight + 150
            listView.setLayoutParams(params)
            listView.requestLayout()
            true
        } else {
            false
        }
    }
}