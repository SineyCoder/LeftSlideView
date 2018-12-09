package siney.cn.leftslideview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class LeftSlideLinearManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public LeftSlideLinearManager(Context context) {
        super(context);
    }

    public void setScroll(boolean isScrollEnabled){
        this.isScrollEnabled = isScrollEnabled;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }

}
