package com.alanrf.cadastraproduto.swipehelper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.alanrf.cadastraproduto.R


abstract class SwipeToEditCallback(context: Context) : SwipeToCallback(context, 0, ItemTouchHelper.RIGHT) {

    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit_white_24)
    private val intrinsicWidth: Int = editIcon?.intrinsicWidth!!
    private val intrinsicHeight: Int = editIcon?.intrinsicHeight!!
    private val background = ColorDrawable()
    private val backgroundColor = Color.GREEN
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
            recyclerView?.let { super.onChildDraw(c, it, viewHolder, dX, dY, actionState, isCurrentlyActive) }
            return
        }

        // Draw the green edit background
        background.color = backgroundColor
        background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
        background.draw(c)

        // Calculate position of edit icon
        val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val editIconMargin = (itemHeight - intrinsicHeight) / 2
        val editIconLeft = itemView.left + editIconMargin
        val editIconRight = itemView.left + editIconMargin + intrinsicWidth
        val editIconBottom = editIconTop + intrinsicHeight

        editIcon?.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
        editIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}