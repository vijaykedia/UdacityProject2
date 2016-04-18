package com.kediavijay.popularmovies2.decorator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by vijaykedia on 15/04/16.
 * This class will serve as vertical list item divider
 * Reference : https://gist.github.com/alexfu/0f464fc3742f134ccd1e
 */
public class VerticalListItemDividerDecorator extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    public VerticalListItemDividerDecorator(@NonNull final Context context) {

        final TypedArray typedArray = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        divider = typedArray.getDrawable(0);
        typedArray.recycle();

    }

    @Override
    public void onDrawOver(@NonNull final Canvas c, @NonNull final RecyclerView parent, @NonNull final RecyclerView.State state) {

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }

        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull final Rect outRect, @NonNull final View view, @NonNull final RecyclerView parent, @NonNull final RecyclerView.State state) {
        outRect.set(0, 0, 0, divider.getIntrinsicHeight());
    }
}
