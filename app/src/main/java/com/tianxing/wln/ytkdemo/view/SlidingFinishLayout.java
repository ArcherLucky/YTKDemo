package com.tianxing.wln.ytkdemo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.tianxing.wln.ytkdemo.util.ScreenUtils;


/**
 * 自定义可以滑动的RelativeLayout, 当我们要使用
 * 此功能的时候，需要将该Activity的顶层布局设置为SildingFinishLayout，
 * 然后需要调用setTouchView()方法来设置需要滑动的View
 *
 * @author liuzhe
 */
public class SlidingFinishLayout extends RelativeLayout implements
        OnTouchListener {

    int total = 0; // 总共更改了多少

    int miniHeight;

    int maxHeight;

    int screenHeight;

    private ViewGroup myViewGroup;

    /*
     * 处理滑动逻辑的View
     */
    private View touchView;

    /*
     * 内容View
     */
    private ViewPager contentView;
    /*
     * 滑动的最小距离
     */
    private int mTouchSlop;

    /*
     * 按下点的Y坐标
     */
    private int downY;

    /*
     * 临时存储Y坐标
     */
    private int tempY;
    /*
     * 滑动类
     */
    private Scroller mScroller;

    private ObservableScrollView titleScrollView;

    public SlidingFinishLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingFinishLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            myViewGroup = this;

        }
    }

    /**
     * 设置Touch的View
     *
     * @param contentView
     */
    public void setContentView(ViewPager contentView) {
        this.contentView = contentView;
    }

    /**
     * 设置Touch的View
     *
     * @param touchView
     */
    public void setTouchView(View touchView) {
        this.touchView = touchView;
        touchView.setOnTouchListener(this);
        screenHeight = ScreenUtils.getScreenHeight(getContext());
        maxHeight = screenHeight - 120;
        miniHeight = 200;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 当手指触摸ListView时，让父控件交出onTouch权限,不能滚动
            case MotionEvent.ACTION_DOWN:
                setParentScrollAble(false);
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 当手指松开时，让父控件重新获取onTouch权限
                setParentScrollAble(true);
                break;

        }
        return super.onInterceptTouchEvent(ev);

    }

    // 设置父控件是否可以获取到触摸处理权限
    private void setParentScrollAble(boolean flag) {
        getParent().requestDisallowInterceptTouchEvent(!flag);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                downY = (int) event.getRawY();
                tempY = downY;
                break;
            case MotionEvent.ACTION_MOVE:

                if (Math.abs(event.getRawY() - downY) >= mTouchSlop) {

                    int len = (int) (tempY - event.getRawY());
                    int length = myViewGroup.getTop() - len;

                    if (length < 240) {
                        length = 240;
                    } else if (length > 1200) {
                        length = 1200;
                    }

                    myViewGroup.layout(myViewGroup.getLeft(), length,
                            myViewGroup.getRight(), myViewGroup.getBottom());
                    total = myViewGroup.getHeight();

                    contentView.layout(contentView.getLeft(), contentView.getTop(),
                            contentView.getRight(), contentView.getBottom()
                                    + total); // ViewPager 更改大小

                    View view = contentView.getChildAt(contentView.getCurrentItem());

                    view.layout(
						 contentView.getWidth()
								* contentView.getCurrentItem(), 0,
                            view.getRight(),
						total - touchView.getHeight());// ScrollView更改大小，要减去上面的TouchView的大小

//				scrollView = (ObservableScrollView) list.get(contentView
//						.getCurrentItem());
//
//				scrollView.layout(
//						0 + contentView.getWidth()
//								* contentView.getCurrentItem(), 0,
//						scrollView.getRight(),
//						groupHeight - touchView.getHeight());// ScrollView更改大小，要减去上面的TouchView的大小

                    titleScrollView.layout(0, 0, titleScrollView.getRight(),
                            screenHeight - (total + 70));// 这个70是状态栏高度，根据手机不同而不同

                    tempY = (int) event.getRawY();
                }

                break;
            case MotionEvent.ACTION_UP:

			int count = contentView.getChildCount();

			for (int i = 0; i < count; i++) {

				if (i != contentView.getCurrentItem()) {

                    View type = contentView.getChildAt(i);

					type.layout(0 + contentView.getWidth() * i, 0,
							type.getRight(), total - touchView.getHeight());// ScrollView
					// 更改大小，要减去上面的TouchView的大小
				}

			}

                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (mScroller.computeScrollOffset()) {
            this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }


    public void setTitleView(ObservableScrollView titleScrollView) {
        // TODO Auto-generated method stub
        this.titleScrollView = titleScrollView;
    }

}
