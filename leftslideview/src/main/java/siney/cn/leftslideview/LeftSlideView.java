package siney.cn.leftslideview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class LeftSlideView extends RecyclerView {

    private static final int NO_SLIDE = 0, OPENING = 1, EXPAND_SLIDE = 2, CLOSING = 3, SLIDING = 4, VERTICAL_SLIDE = 5;

    private int HIDEN_NUM = 0;

    private volatile boolean loading;//判断是否在，

    private Scroller mScroll;

    private Context context;

    private int state = NO_SLIDE;

    private int maxWidth;

    private float last_x, last_y;

    private int cur_axis_x;

    private View view;

    private LinearLayout layout;//需要移动的view

    private LeftSlideLinearManager manager;

    private int layoutId;

    private int[] itemsId;

//    private ContactsListAdapter.ViewHolder viewHolder;//当前点击处的ciewHolder

    public LeftSlideView(@NonNull Context context) {
        super(context);
    }

    public LeftSlideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroll = new Scroller(context);
        this.context = context;
    }

    public LeftSlideView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public void setLayout(int layoutId){
        this.layoutId = layoutId;
    }

    /**
     * 传入参数从左到右依次为要显示的格子的id
     * @param itemId view的id
     */
    public void setItems(int... itemId){
        itemsId = itemId;
        HIDEN_NUM = itemsId.length;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        return true;//消费完事件
        float x = e.getX();
        float y = e.getY();
        manager = (LeftSlideLinearManager) getLayoutManager();//起始都设置为不可上下滑动
        if(state != VERTICAL_SLIDE && manager.canScrollVertically())
            manager.setScroll(false);
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
//                ContactsListAdapter adapter = (ContactsListAdapter) getAdapter();
                view = findChildViewUnder(x, y);
                if(view != null){
                    LinearLayout tmp_layout = view.findViewById(layoutId);
                    last_x = x;
                    last_y = y;
                    maxWidth = 0;
                    for (int anItemsId : itemsId) {
                        maxWidth += tmp_layout.findViewById(anItemsId).getWidth();
                    }
                    if(layout == null || layout.getScrollX() == 0){//如果布局为空，或者已经处于原位，那么指向新layout，并且重置所有属性
                        layout = tmp_layout;
                        loading = false;
                        cur_axis_x = 0;
                    }else if(layout != null && layout != tmp_layout && state == EXPAND_SLIDE){//这里是要关闭原先已经打开的隐藏部分
                        state = CLOSING;
                        loading = true;
                        mScroll.startScroll(layout.getScrollX(), 0, -cur_axis_x, 0, 500);
                        invalidate();
                    }else if(layout != null && layout == tmp_layout){//如果之前布局和现在点击的布局一样，那么把loading置为false
                        loading = false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", x +" "+y +" "+state+" "+loading);
                if(!loading){//这里 很重要，可以防止滑动过程中干扰当前滑动元素.【不能使用mScroll的isFinished，因为只要动画一结束，就又能滑动了】
                    int gap_x = (int) Math.abs(last_x - x);
                    int gap_y = (int) Math.abs(last_y - y);
                    if(gap_y > gap_x){
                        if(state == NO_SLIDE)//如果处于关闭状态，且在上下滑动，那么把状态设置为VERTICAL
                            state = VERTICAL_SLIDE;
                        if(state == VERTICAL_SLIDE){
                            manager.setScroll(true);
                        }
                    }else if(gap_x > gap_y && view != null){//如果是左右滑动，并且布局存在，那么表示选中子布局。
                        if(state != VERTICAL_SLIDE)//只要目前不是上下滑动，那么就可以进行左右滑动，改变状态为SLIDING
                            state = SLIDING;
                        if(state == SLIDING){
                            if(last_x - x > 0){
                                //代表向左滑动
                                cur_axis_x += gap_x;
                                cur_axis_x = cur_axis_x >= maxWidth ? maxWidth : cur_axis_x;
                            }else if(x - last_x > 0){
                                //代表向右滑动
                                cur_axis_x -= gap_x;
                                cur_axis_x = cur_axis_x <= 0 ? 0 :cur_axis_x;
                            }
                            layout.scrollTo(cur_axis_x, 0);
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(state == SLIDING){
                    Log.d("TAG", "没进去吗");
                    int thresholdWidth = maxWidth / (HIDEN_NUM + 1);
                    loading = true;
                    if(cur_axis_x > thresholdWidth){
                        state = OPENING;
                        int offset = maxWidth - cur_axis_x;
                        mScroll.startScroll(layout.getScrollX(), 0, offset, 0, 500);
                        cur_axis_x = maxWidth;
                    }else{
                        state = CLOSING;
                        mScroll.startScroll(layout.getScrollX(), 0, -cur_axis_x, 0, 500);
                    }
                    invalidate();
                }else if(state == VERTICAL_SLIDE){
                    state = NO_SLIDE;
                }
                break;
        }
        last_x = x;
        last_y = y;
        return super.onTouchEvent(e);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroll.computeScrollOffset()){
            //动画没完成
//            Log.d("TAG", "正在动画" + " "+mScroll.getFinalX() + " "+mScroll.getFinalY());
            layout.scrollTo(mScroll.getCurrX(), mScroll.getCurrY());
            invalidate();
        }else{
            if(state == OPENING){
                Log.d("TAG", "OPENNING");
                layout.scrollTo(maxWidth, 0);
                state = EXPAND_SLIDE;
            }else if(state == CLOSING){
                Log.d("TAG", "CLOSING");
                layout.scrollTo(0, 0);
                state = NO_SLIDE;
            }
        }
    }



}
