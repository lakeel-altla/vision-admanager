package com.lakeel.altla.vision.admanager.presentation.view.helper;

import com.lakeel.altla.vision.admanager.R;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public final class SwipeRightItemTouchHelper extends ItemTouchHelper {

    public SwipeRightItemTouchHelper(@NonNull OnItemSwipedListener onItemSwipedListener) {
        super(new SwipeRightCallback(onItemSwipedListener));
    }

    public interface OnItemSwipedListener {

        void onSwiped(int position);
    }

    private static class SwipeRightCallback extends ItemTouchHelper.SimpleCallback {

        private OnItemSwipedListener mOnItemSwipedListener;

        public SwipeRightCallback(@NonNull OnItemSwipedListener onItemSwipedListener) {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT);

            mOnItemSwipedListener = onItemSwipedListener;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = viewHolder.getAdapterPosition();
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mOnItemSwipedListener.onSwiped(position);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View view = viewHolder.itemView;
                Paint paint = new Paint();

                if (0 < dX) {
                    Resources resources = recyclerView.getResources();
                    int backgroundColor = resources.getColor(R.color.background_swipe_to_delete_item);
                    int foregroundColor = resources.getColor(R.color.foreground_swipe_to_delete_item);
                    int iconMarginLeft = resources.getDimensionPixelSize(R.dimen.swape_to_delete_icon_margin_left);
                    Drawable icon = resources.getDrawable(R.drawable.ic_delete_black_24dp);

                    // Draw the background
                    paint.setColor(backgroundColor);
                    c.drawRect(view.getLeft(), view.getTop(), dX, view.getBottom(), paint);
                    icon.setColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP);

                    // Draw the icon.
                    int left = view.getLeft() + iconMarginLeft;
                    int top = view.getTop() +
                              (int) ((view.getBottom() - view.getTop() - icon.getIntrinsicHeight()) * 0.5f);
                    icon.setBounds(left, top, left + icon.getIntrinsicWidth(), top + icon.getIntrinsicHeight());
                    icon.draw(c);
                }
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
