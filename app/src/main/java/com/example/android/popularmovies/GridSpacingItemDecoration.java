package com.example.android.popularmovies;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Set column spacing to make each column have the same spacing.
 *
 * Reference: @see "https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing"
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    /**
     * Constructor
     *
     * @param spanCount The number of columns
     * @param spacing The spacing between each grid item
     * @param includeEdge Whether to include left and right margins
     */
    GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // item position
        int position = parent.getChildAdapterPosition(view);
        // item column
        int column = position % spanCount;

        if (includeEdge) {
            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = spacing - column * spacing / spanCount;
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount;

            // top edge
            if (position < spanCount) {
                outRect.top = spacing;
            }
            // item bottom
            outRect.bottom = spacing;
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * spacing / spanCount;
            // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                // item top
                outRect.top = spacing;
            }
        }
    }
}
