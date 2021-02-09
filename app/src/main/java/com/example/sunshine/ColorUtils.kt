package com.example.sunshine

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat

 class ColorUtils(context: Context, viewHolderCount: Int) {
    /**
     * This method returns the appropriate shade of green to form the gradient
     * seen in the list, based off of the order in which the
     * [com.example.android.recyclerview.GreenAdapter.NumberViewHolder]
     * instance was created.
     *
     * This method is used to show how ViewHolders are recycled in a RecyclerView.
     * At first, the colors will form a nice, consistent gradient. As the
     * RecyclerView is scrolled, the
     * [com.example.android.recyclerview.GreenAdapter.NumberViewHolder]'s will be
     * recycled and the list will no longer appear as a consistent gradient.
     *
     * @param context     Context for getting colors
     * @param instanceNum Order in which the calling ViewHolder was created
     *
     * @return A shade of green based off of when the calling ViewHolder
     * was created.
     */
    fun getViewHolderBackgroundColorFromInstance(
        context: Context?,
        instanceNum: Int
    ): Int? {
        return when (instanceNum) {
            0 -> context?.let { ContextCompat.getColor(it, R.color.material50Green) }
            1 -> context?.let { ContextCompat.getColor(it, R.color.material100Green) }
            2 -> context?.let { ContextCompat.getColor(it, R.color.material150Green) }
            3 -> context?.let { ContextCompat.getColor(it, R.color.material200Green) }
            4 -> context?.let { ContextCompat.getColor(it, R.color.material250Green) }
            5 -> context?.let { ContextCompat.getColor(it, R.color.material300Green) }
            6 -> context?.let { ContextCompat.getColor(it, R.color.material350Green) }
            7 -> context?.let { ContextCompat.getColor(it, R.color.material400Green) }
            8 -> context?.let { ContextCompat.getColor(it, R.color.material450Green) }
            9 -> context?.let { ContextCompat.getColor(it, R.color.material500Green) }
            10 -> context?.let { ContextCompat.getColor(it, R.color.material550Green) }
            11 -> context?.let { ContextCompat.getColor(it, R.color.material600Green) }
            12 -> context?.let { ContextCompat.getColor(it, R.color.material650Green) }
            13 -> context?.let { ContextCompat.getColor(it, R.color.material700Green) }
            14 -> context?.let { ContextCompat.getColor(it, R.color.material750Green) }
            15 -> context?.let { ContextCompat.getColor(it, R.color.material800Green) }
            16 -> context?.let { ContextCompat.getColor(it, R.color.material850Green) }
            17 -> context?.let { ContextCompat.getColor(it, R.color.material900Green) }
            else -> Color.WHITE
        }
    }
}
