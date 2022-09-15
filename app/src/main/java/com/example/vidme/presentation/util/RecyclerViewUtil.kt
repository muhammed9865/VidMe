package com.example.vidme.presentation.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vidme.R


abstract class RecyclerViewUtil {
    companion object {
        fun RecyclerView.setSwipeToDelete(
            swipeDirection: Int,
            onDelete: (viewHolder: RecyclerView.ViewHolder, position: Int) -> Unit,
        ) {
            val swipeToDeleteCallback = SwipeToDeleteCallback(context, swipeDirection, onDelete)
            val itemTouch = ItemTouchHelper(swipeToDeleteCallback)
            itemTouch.attachToRecyclerView(this)
        }

        const val LEFT = ItemTouchHelper.LEFT
        const val RIGHT = ItemTouchHelper.RIGHT
    }
}


private class SwipeToDeleteCallback(
    private val context: Context,
    private val direction: Int,
    private val onDelete: (viewHolder: RecyclerView.ViewHolder, position: Int) -> Unit,
) : ItemTouchHelper.Callback() {

    private val deleteDrawable by lazy { ContextCompat.getDrawable(context, R.drawable.ic_delete) }
    private val clearPaint: Paint by lazy {
        Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    private val backgroundPaint by lazy {
        Paint().apply {
            color = context.getColor(R.color.deleting_color)
        }
    }

    private val cornersRadius = 25f
    private val intrinsicHeight by lazy { deleteDrawable?.intrinsicHeight }
    private val intrinsicWidth by lazy { deleteDrawable?.intrinsicWidth }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        return makeMovementFlags(0, direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val isCancelled = dX == 0f && !isCurrentlyActive

        if (isCancelled) {
            clearCanvas(c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat())

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }


        c.drawRoundRect(
            itemView.left + dX + 10,
            (itemView.top + 5).toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat() - 5,
            cornersRadius,
            cornersRadius,
            backgroundPaint
        )

        val deleteIconTop: Int = itemView.top + (itemHeight - intrinsicHeight!!) / 2
        val deleteIconMargin: Int = (itemHeight - intrinsicHeight!!) / 2
        val deleteIconLeft: Int = itemView.right - deleteIconMargin - intrinsicWidth!!
        val deleteIconRight: Int = itemView.right - deleteIconMargin
        val deleteIconBottom: Int = deleteIconTop + intrinsicHeight!!


        deleteDrawable!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable!!.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.6f
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onDelete(viewHolder, viewHolder.adapterPosition)
    }

}