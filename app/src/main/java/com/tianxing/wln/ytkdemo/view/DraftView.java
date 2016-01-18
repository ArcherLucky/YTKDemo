package com.tianxing.wln.ytkdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tianxing.wln.ytkdemo.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 草稿纸自定义View
 * 此View有两个功能，单个手指滑动画图，两个手指滑动移动View。
 */
public class DraftView extends View {

    /**
     * 当用户单手抬起的时候，则在屏幕上添加了一个Path，则上一步按钮可用。
     */
    public interface OnPaintListener {
        void onPaint();
    }

    private Context mContext; // 上下文
    private List<Path> mPaintSparseArray = new ArrayList<>(); // 存放Path的集合，用于上一步下一步操作
    private Paint mBrush = new Paint(); // 描绘所需要的画笔
    private Path mPath = new Path(); // 根据用户手势记录下Path
    private int mTarget; // 当前是第几步操作
    private boolean isUserChange; // 用户是否点击了上一步或者下一步
    private boolean isTwoFinger = false; // 是否为多点触控

    /*
    当第二个手指按下的时候的坐标，然后当两根手指移动的时候，此为两根手指移动过程中坐标。
     */
    float lastX;
    float lastY;

    /*
    第一个手指按下屏幕的坐标，如果是单个手指触摸，即为单个手指的坐标
     */
    float clickX;
    float clickY;

    /*
    测试的时候，发现移动后，画画痕迹会有偏差， 这个是记录第一根手指和第二根手指之间的距离，
    改变画笔坐标的时候，是滑动距离加上这个
     */
    int x;
    int y;

    /*
    滑动距离
     */
    private int maxX;
    private int maxY;

    int mWidth;
    int mHeight;


    float tempClickX;
    float tempClickY;

    public DraftView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mBrush.setAntiAlias(true);
        mBrush.setColor(Color.BLACK);
        mBrush.setStyle(Paint.Style.STROKE);
        mBrush.setStrokeJoin(Paint.Join.ROUND);
        mBrush.setStrokeWidth(4f);
        mPaintSparseArray.add(new Path());

    }

    public DraftView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public DraftView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec) - ScreenUtils.getScreenWidth(mContext);
        mHeight = MeasureSpec.getSize(heightMeasureSpec) - (ScreenUtils.getScreenHeight(mContext) - 75);
    }

    /**
     * 上一步操作
     *
     * @return 是否为第一步
     */
    public boolean lastPath() {
        isUserChange = true;
        mTarget--;
        invalidate();
        return mTarget > 0;
    }

    /**
     * 下一步
     *
     * @return 是否为最后一步
     */
    public boolean nextPath() {
        isUserChange = true;
        mTarget++;
        if (mTarget >= mPaintSparseArray.size()) {
            return false;
        }
        invalidate();
        return mTarget < mPaintSparseArray.size() - 1;
    }

    /**
     * 清除所有痕迹
     */
    public void clearPath() {
        isUserChange = false;
        mPath.reset();
        mPaintSparseArray.clear();
        mPaintSparseArray.add(new Path());
        mTarget = 0;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 如果没有多点触控，则 x, y 为0，如果有的话，则画笔的坐标需要加上 x, y
        float pointX = event.getX() + x; // 获取用户点击的X坐标
        float pointY = event.getY() + y; // 获取用户点击的Y坐标


        // 如果是大于两根手指，return，不处理
        if (event.getPointerCount() > 2) {
            return true;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 第一个手指按下
            case MotionEvent.ACTION_DOWN:

                isTwoFinger = false; // 第一次按下（或者说第一根手指按下），则先认定为不是多点触控
                mPath.moveTo(pointX, pointY);
                clickX = event.getX();
                clickY = event.getY();
                tempClickX = pointX;
                tempClickY = pointY;
                break;
            // 第二或者更多手指按下，则认定为多点操作，不画Path，移动画布
            case MotionEvent.ACTION_POINTER_DOWN:
                // 获取第二根手指按下的坐标
                lastX = event.getX();
                lastY = event.getY();
                // 获取和第一根手指之间的距离
                x = (int) (lastX - clickX);
                y = (int) (lastY - clickY);
                // 是多点触控
                isTwoFinger = true;
                break;
            // 用户手指在屏幕上移动
            case MotionEvent.ACTION_MOVE:
                // 如果是单点触控
                if (!isTwoFinger) {
                    touchMove(event);
                    invalidate();
                } else {
                    // 滑动
                    int moveX = (int) (lastX - event.getX());
                    int moveY = (int) (lastY - event.getY());
                    // 手指左滑动
                    if (moveX > 0 && maxX > mWidth) {
//                        Log.e("tag", "手指左滑动  maxX--" + maxX);
                        moveX = 0;
                    }

                    //手指右滑动
                    if (moveX < 0 && maxX <= 0) {
                        moveX = 0;
                    }

                    // 手指往上滑动
                    if (moveY > 0 && maxY >= mHeight) {
                        moveY = 0;
                    }

                    // 手指往下滑动
                    if (moveY < 0 && maxY <= 0) {
                        moveY = 0;
                    }

                    maxX += moveX;
                    maxY += moveY;

                    if(event.getPointerCount() > 1) {
                        scrollBy(moveX, moveY);
                    }

                    lastX = (int) event.getX();
                    lastY = (int) event.getY();


                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // 加上滑动距离
                x += getScrollX();
                y += getScrollY();
                break;
            // 用户抬起操作
            case MotionEvent.ACTION_UP:

                // 如果是单点触控
                if (!isTwoFinger) {

                    if (mTarget == 0) {
                        mPaintSparseArray.clear();
                        mPaintSparseArray.add(new Path());
                    }
                    Path p = new Path();
                    p.addPath(mPath); // 把这个path添加进去
                    mTarget += 1; // List下标+1
                    mPaintSparseArray.add(p);
                    if (mContext instanceof OnPaintListener) {
                        ((OnPaintListener) mContext).onPaint();
                    }
                }
                break;

            default:
                return true;
        }
        return true;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 如果是用户点击了上一步或者下一步
        if (isUserChange) {
            // 获取上一步或者下一步的Path
            Path path = mPaintSparseArray.get(mTarget);
            // 如果有这个Path，则画出来，mPath重新set成屏幕当前的Path
            if (null != path) {
                canvas.drawPath(path, mBrush);
                mPath.reset();
                mPath.set(mPaintSparseArray.get(mTarget));
            }
            isUserChange = false; // 初始化用户操作标识
        } else {
            canvas.drawPath(mPath, mBrush); // 认定用户为在屏幕上手动画Path
        }



    }

    //手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event) {
        final float eventX = event.getX() + x;
        final float eventY = event.getY() + y;

        final float previousX = tempClickX;
        final float previousY = tempClickY;

        final float dx = Math.abs(eventX - previousX);
        final float dy = Math.abs(eventY - previousY);

        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            //设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (eventX + previousX) / 2;
            float cY = (eventY + previousY) / 2;

            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);

            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            tempClickX = eventX;
            tempClickY = eventY;
        }
        invalidate();
    }

}
