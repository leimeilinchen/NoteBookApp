package com.necer.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.necer.entity.NDate;
import com.necer.listener.OnCalendarChangedListener;
import com.necer.listener.OnCalendarStateChangedListener;
import com.necer.listener.OnClickDisableDateListener;
import com.necer.listener.OnMonthAnimatorListener;
import com.necer.listener.OnMonthSelectListener;
import com.necer.listener.OnWeekSelectListener;
import com.necer.painter.CalendarPainter;
import com.necer.painter.InnerPainter;
import com.necer.utils.Attrs;
import com.necer.utils.AttrsUtil;
import com.necer.view.ChildLayout;


public abstract class NCalendar extends FrameLayout implements NestedScrollingParent, OnCalendarStateChangedListener, OnMonthAnimatorListener, OnMonthSelectListener, OnWeekSelectListener {


    protected WeekCalendar weekCalendar;
    protected MonthCalendar monthCalendar;

    protected int weekHeight;//周日历的高度
    protected int monthHeight;//月日历的高度,是日历整个的高

    protected int childLayoutLayoutTop;//onLayout中，定位的高度，

    protected int STATE;//默认月
    private int lastSate;//防止状态监听重复回调
    private OnCalendarChangedListener onCalendarChangedListener;

    protected ChildLayout childLayout;//NCalendar内部包含的直接子view，直接子view并不一定是NestScrillChild

    protected Rect monthRect;//月日历大小的矩形
    protected Rect weekRect;//周日历大小的矩形 ，用于判断点击事件是否在日历的范围内

    private boolean isWeekHold;//是否需要周状态定住

    private CalendarPainter calendarPainter;
    private boolean isInflateFinish;//是否加载完成，


    public NCalendar(@NonNull Context context) {
        this(context, null);
    }

    public NCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NCalendar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setMotionEventSplittingEnabled(false);
        Attrs attrss = AttrsUtil.getAttrs(context, attrs);

        int duration = attrss.duration;
        monthHeight = attrss.monthCalendarHeight;
        STATE = attrss.defaultCalendar;
        weekHeight = monthHeight / 5;
        isWeekHold = attrss.isWeekHold;

        calendarPainter = new InnerPainter(attrss);
        weekCalendar = new WeekCalendar(context, attrss, calendarPainter);
        monthCalendar = new MonthCalendar(context, attrss, calendarPainter, duration, this);
        childLayout = new ChildLayout(getContext(), attrs, monthHeight, duration, this);

        monthCalendar.setOnMonthSelectListener(this);
        weekCalendar.setOnWeekSelectListener(this);

        childLayout.setBackgroundColor(attrss.bgChildColor);

        setCalenadrState(STATE);
        childLayoutLayoutTop = STATE == Attrs.WEEK ? weekHeight : monthHeight;

        post(new Runnable() {
            @Override
            public void run() {

                monthRect = new Rect(0, 0, monthCalendar.getWidth(), monthCalendar.getHeight());
                weekRect = new Rect(0, 0, weekCalendar.getWidth(), weekCalendar.getHeight());

                monthCalendar.setY(STATE == Attrs.MONTH ? 0 : getMonthYOnWeekState());
                childLayout.setY(STATE == Attrs.MONTH ? monthHeight : weekHeight);

                isInflateFinish = true;

            }
        });
    }

    /**
     * 根据ChildLayout的自动滑动结束的状态来设置月周日历的状态
     * 依据ChildLayout的状态来设置日历的状态
     *
     * @param isMonthState
     */
    @Override
    public void onCalendarStateChanged(boolean isMonthState) {
        if (isMonthState) {
            setCalenadrState(Attrs.MONTH);
        } else {
            setCalenadrState(Attrs.WEEK);
        }
    }

    /**
     * xml文件加载结束，添加月，周日历和child到NCalendar中
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 1) {
            throw new RuntimeException("NCalendar中的只能有一个直接子view");
        }
        childLayout.addView(getChildAt(0), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(monthCalendar, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, monthHeight));
        addView(weekCalendar, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, weekHeight));
        addView(childLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams childLayoutLayoutParams = childLayout.getLayoutParams();
        childLayoutLayoutParams.height = getMeasuredHeight() - weekHeight;

        //需要再调一次父类的方法？真机不调用首次高度不对，为何？
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b); //调用父类的该方法会造成 快速滑动月日历同时快速上滑recyclerview造成月日历的残影

        int measuredWidth = getMeasuredWidth();
        weekCalendar.layout(0, 0, measuredWidth, weekHeight);
        monthCalendar.layout(0, 0, measuredWidth, monthHeight);
        childLayout.layout(0, childLayoutLayoutTop, measuredWidth, childLayout.getMeasuredHeight() + childLayoutLayoutTop);
    }


    /**
     * 根据条件设置日历的月周状态，并回调状态变化
     *
     * @param state
     */
    private void setCalenadrState(int state) {

        if (state == Attrs.WEEK) {
            STATE = Attrs.WEEK;
            weekCalendar.setVisibility(VISIBLE);
            monthCalendar.setVisibility(INVISIBLE);
        } else {
            STATE = Attrs.MONTH;
            weekCalendar.setVisibility(INVISIBLE);
            monthCalendar.setVisibility(VISIBLE);
        }

        if (onCalendarChangedListener != null && lastSate != state) {
            onCalendarChangedListener.onCalendarStateChanged(STATE == Attrs.MONTH);
        }

        lastSate = state;
    }


    /**
     * 自动滑动到适当的位置
     */
    private void autoScroll() {

        float childLayoutY = childLayout.getY();

        if (STATE == Attrs.MONTH && monthHeight - childLayoutY < weekHeight) {
            onAutoToMonthState();
        } else if (STATE == Attrs.MONTH && monthHeight - childLayoutY >= weekHeight) {
            onAutoToWeekState();
        } else if (STATE == Attrs.WEEK && childLayoutY < weekHeight * 2) {
            onAutoToWeekState();
        } else if (STATE == Attrs.WEEK && childLayoutY >= weekHeight * 2) {
            onAutoToMonthState();
        }
    }


    @Override
    public void onMonthSelect(NDate date, boolean isClick) {
        if (STATE == Attrs.MONTH) {
            //月日历变化,改变周的选中
            weekCalendar.jumpDate(date.localDate, true);
            if (onCalendarChangedListener != null) {
                onCalendarChangedListener.onCalendarDateChanged(date, isClick);
            }
        }
    }

    @Override
    public void onWeekSelect(NDate date, boolean isClick) {
        if (STATE == Attrs.WEEK) {
            //周日历变化，改变月的选中
            monthCalendar.jumpDate(date.localDate, true);
            post(new Runnable() {
                @Override
                public void run() {
                    //此时需要根据月日历的选中日期调整Y值
                    // post是因为在前面得到当前view是再post中完成，如果不这样直接获取位置信息，会出现老的数据，不能获取正确的数据
                    monthCalendar.setY(getMonthYOnWeekState());
                }
            });
            if (onCalendarChangedListener != null) {
                onCalendarChangedListener.onCalendarDateChanged(date, isClick);
            }
        }
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        //super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //跟随手势滑动
        gestureMove(dy, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //只有都在都在周状态下，才允许子View Fling滑动
        return !(childLayout.isWeekState() && monthCalendar.isWeekState());
    }

    @Override
    public void onStopNestedScroll(View target) {
        //该方法手指抬起的时候回调，此时根据此刻的位置，自动滑动到相应的状态，
        //如果已经在对应的位置上，则不执行动画，
        if (monthCalendar.isMonthState() && childLayout.isMonthState() && STATE == Attrs.WEEK) {
            setCalenadrState(Attrs.MONTH);
        } else if (monthCalendar.isWeekState() && childLayout.isWeekState() && STATE == Attrs.MONTH) {
            setCalenadrState(Attrs.WEEK);
        } else if (!childLayout.isMonthState() && !childLayout.isWeekState()) {
            //不是周状态也不是月状态时，自动滑动
            autoScroll();
        }
    }

    /**
     * 手势滑动的逻辑，做了简单处理，2种状态，都以ChildLayout滑动的状态判断
     * 1、向上滑动未到周状态
     * 2、向下滑动未到月状态
     *
     * @param dy
     * @param consumed
     */
    protected void gestureMove(int dy, int[] consumed) {


        float monthCalendarY = monthCalendar.getY();
        float childLayoutY = childLayout.getY();

        if (dy > 0 && !childLayout.isWeekState()) {
            monthCalendar.setY(-getGestureMonthUpOffset(dy) + monthCalendarY);
            childLayout.setY(-getGestureChildUpOffset(dy) + childLayoutY);
            if (consumed != null) consumed[1] = dy;
        } else if (dy < 0 && isWeekHold && childLayout.isWeekState()) {
            //不操作，

        } else if (dy < 0 && !childLayout.isMonthState() && !childLayout.canScrollVertically(-1)) {
            monthCalendar.setY(getGestureMonthDownOffset(dy) + monthCalendarY);
            childLayout.setY(getGestureChildDownOffset(dy) + childLayoutY);
            if (consumed != null) consumed[1] = dy;
        }

        onSetWeekVisible(dy);
    }


    /**
     * 月日历执行自动滑动动画的回调
     * 用来控制周日历的显示还是隐藏
     *
     * @param offset
     */
    @Override
    public void onMonthAnimatorChanged(int offset) {
        onSetWeekVisible(offset);
    }

    private int dowmY;
    private int downX;
    private int lastY;//上次的y
    private int verticalY = 50;//竖直方向上滑动的临界值，大于这个值认为是竖直滑动
    private boolean isFirstScroll = true; //第一次手势滑动，因为第一次滑动的偏移量大于verticalY，会出现猛的一划，这里只对第一次滑动做处理

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (isInflateFinish) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dowmY = (int) ev.getY();
                    downX = (int) ev.getX();
                    lastY = dowmY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int y = (int) ev.getY();
                    int absY = Math.abs(dowmY - y);
                    boolean inCalendar = isInCalendar(downX, dowmY);
                    if (absY > verticalY && inCalendar) {
                        //onInterceptTouchEvent返回true，触摸事件交给当前的onTouchEvent处理
                        return true;
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                int y = (int) event.getY();
                int dy = lastY - y;

                if (isFirstScroll) {
                    // 防止第一次的偏移量过大
                    if (dy > verticalY) {
                        dy = dy - verticalY;
                    } else if (dy < -verticalY) {
                        dy = dy + verticalY;
                    }
                    isFirstScroll = false;
                }

                // 跟随手势滑动
                gestureMove(dy, null);
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isFirstScroll = true;
                autoScroll();
                break;
        }
        return true;
    }


    /**
     * 点击事件是否在日历的范围内
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInCalendar(int x, int y) {
        if (STATE == Attrs.MONTH) {
            return monthRect.contains(x, y);
        } else {
            return weekRect.contains(x, y);
        }
    }


    /**
     * 滑动过界处理 ，如果大于最大距离就返回最大距离
     *
     * @param offset    当前滑动的距离
     * @param maxOffset 当前滑动的最大距离
     * @return
     */
    protected float getOffset(float offset, float maxOffset) {
        if (offset > maxOffset) {
            return maxOffset;
        }
        return offset;
    }

    /**
     * 自动回到月的状态 包括月日历和chilayout
     */
    protected abstract void onAutoToMonthState();

    /**
     * 自动回到周的状态 包括月日历和chilayout
     */
    protected abstract void onAutoToWeekState();

    /**
     * 设置weekCalendar的显示隐藏，该方法会在手势滑动和自动滑动的的时候一直回调
     */
    protected abstract void onSetWeekVisible(int dy);

    /**
     * 周状态下 月日历的getY 是个负值
     * 用于在 周状态下日期改变设置正确的y值
     *
     * @return
     */
    protected abstract float getMonthYOnWeekState();


    /**
     * 月日历根据手势向上移动的距离
     *
     * @param dy 当前滑动的距离 dy>0向上滑动，dy<0向下滑动
     * @return 根据不同日历的交互，计算不同的滑动值
     */
    protected abstract float getGestureMonthUpOffset(int dy);

    /**
     * Child根据手势向上移动的距离
     *
     * @param dy 当前滑动的距离 dy>0向上滑动，dy<0向下滑动
     * @return 根据不同日历的交互，计算不同的滑动值
     */
    protected abstract float getGestureChildUpOffset(int dy);

    /**
     * 月日历根据手势向下移动的距离
     *
     * @param dy 当前滑动的距离 dy>0向上滑动，dy<0向下滑动
     * @return 根据不同日历的交互，计算不同的滑动值
     */
    protected abstract float getGestureMonthDownOffset(int dy);

    /**
     * Child根据手势向下移动的距离
     *
     * @param dy 当前滑动的距离 dy>0向上滑动，dy<0向下滑动
     * @return 根据不同日历的交互，计算不同的滑动值
     */
    protected abstract float getGestureChildDownOffset(int dy);


    /**
     * 跳转日期
     *
     * @param formatDate
     */
    public void jumpDate(String formatDate) {
        if (STATE == Attrs.MONTH) {
            monthCalendar.jumpDate(formatDate);
        } else {
            weekCalendar.jumpDate(formatDate);
        }
    }

    /**
     * 日历初始化的日期
     *
     * @param formatDate
     */
    public void setInitializeDate(String formatDate) {
        monthCalendar.setInitializeDate(formatDate);
        weekCalendar.setInitializeDate(formatDate);
    }


    /**
     * 回到今天
     */
    public void toToday() {
        if (STATE == Attrs.MONTH) {
            monthCalendar.toToday();
        } else {
            weekCalendar.toToday();
        }
    }

    /**
     * 自动滑动到周视图
     */
    public void toWeek() {
        if (STATE == Attrs.MONTH) {
            onAutoToWeekState();
        }
    }

    /**
     * 自动滑动到月视图
     */
    public void toMonth() {
        if (STATE == Attrs.WEEK) {
            onAutoToMonthState();
        }
    }

    /**
     * 获取绘制CalendarPainter 强转，设置其他属性
     *
     * @param
     */
    public CalendarPainter getCalendarPainter() {
        return calendarPainter;
    }

    /**
     * 获取当前日历的状态
     * Attrs.MONTH==月视图    Attrs.WEEK==周视图
     *
     * @return
     */
    public int getState() {
        return STATE;
    }


    /**
     * 下一页
     */
    public void toNextPager() {
        if (STATE == Attrs.MONTH) {
            monthCalendar.toNextPager();
        } else {
            weekCalendar.toNextPager();
        }
    }

    /**
     * 上一页
     */
    public void toLastPager() {
        if (STATE == Attrs.MONTH) {
            monthCalendar.toLastPager();
        } else {
            weekCalendar.toLastPager();
        }
    }

    /**
     * 设置日期区间
     *
     * @param startFormatDate
     * @param endFormatDate
     */
    public void setDateInterval(String startFormatDate, String endFormatDate) {
        monthCalendar.setDateInterval(startFormatDate, endFormatDate);
        weekCalendar.setDateInterval(startFormatDate, endFormatDate);
    }

    /**
     * 日期、状态回调
     *
     * @param onCalendarChangedListener
     */
    public void setOnCalendarChangedListener(OnCalendarChangedListener onCalendarChangedListener) {
        this.onCalendarChangedListener = onCalendarChangedListener;
    }

    /**
     * 点击不可用的日期回调
     *
     * @param onClickDisableDateListener
     */
    public void setOnClickDisableDateListener(OnClickDisableDateListener onClickDisableDateListener) {
        monthCalendar.setOnClickDisableDateListener(onClickDisableDateListener);
        weekCalendar.setOnClickDisableDateListener(onClickDisableDateListener);
    }

    //设置绘制类
    public void setCalendarPainter(CalendarPainter calendarPainter) {
        this.calendarPainter = calendarPainter;
        monthCalendar.setCalendarPainter(calendarPainter);
        weekCalendar.setCalendarPainter(calendarPainter);
    }

    //刷新页面
    public void notifyAllView() {
        monthCalendar.notifyAllView();
        weekCalendar.notifyAllView();
    }


    //id重复
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);
    }

}
