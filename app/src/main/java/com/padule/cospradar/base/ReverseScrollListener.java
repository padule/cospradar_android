package com.padule.cospradar.base;

import android.widget.AbsListView;

public abstract class ReverseScrollListener implements AbsListView.OnScrollListener {
    private int offSet = 0;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;

    public ReverseScrollListener() {
    }

    public ReverseScrollListener(int offSet) {
        this.offSet = offSet;
    }

    public ReverseScrollListener(int offSet, int startPage) {
        this.offSet = offSet;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    public int getItemCountOffset() {
        return offSet;
    }

    @Override
    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount) {
        totalItemCount -= getItemCountOffset();
        totalItemCount = Math.max(0, totalItemCount);
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        if (!loading && firstVisibleItem == 1) {
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //
    }
}
