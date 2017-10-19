package com.funny.bjokes.listeners;

import android.widget.AbsListView;

import com.funny.bjokes.Constants;

/**
 * Created by CJH on 2016.01.14.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener{

    private int currentPage;
    private boolean loading;
    private int previousTotalItemCount;
    private int startingPageIndex;
    private int visibleThreshold;

    public EndlessScrollListener() {
        this.visibleThreshold = 5;
        this.currentPage = 0;
        this.previousTotalItemCount = 0;
        this.loading = true;
        this.startingPageIndex = 0;
    }

    public EndlessScrollListener(final int visibleThreshold) {
        this.currentPage = 0;
        this.previousTotalItemCount = 0;
        this.loading = true;
        this.startingPageIndex = 0;
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(final int visibleThreshold, final int n) {
        this.previousTotalItemCount = 0;
        this.loading = true;
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = n;
        this.currentPage = n;
    }

    public abstract void onLoadMore(final int page, final int count);

    @Override
    public void onScroll(final AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        Log.w("OnScroll", "Loading:" + loading);
//        Log.w("OnScroll", "First Visible:" + firstVisibleItem + ">>>" + "  VisibleItemCount:" + visibleItemCount + ">>>  totalItemCount:" + totalItemCount);
//        Log.w("OnScroll", "PreviousTotalItemCount:" + previousTotalItemCount + ">>>  StartingPageIndex:" + startingPageIndex + ">>>  currentPage:" + currentPage);

        if (totalItemCount < previousTotalItemCount) {
//            Log.i("OnScroll", "Step 1");
            currentPage = startingPageIndex;
            previousTotalItemCount = totalItemCount;
            if (previousTotalItemCount == 0) {
                loading = true;
            }
        }

        if (loading && totalItemCount > previousTotalItemCount) {
//            Log.i("OnScroll", "Step 2");
            loading = false;
            if (totalItemCount> Constants.PAGE_SIZE && previousTotalItemCount!=0)
                currentPage++;
            previousTotalItemCount = totalItemCount;
//            Log.i("OnScroll", "Step 2>>>" + currentPage);
        }

        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
//            Log.i("OnScroll", "Step 3");
            onLoadMore(currentPage+1, totalItemCount);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }
}
