package com.imooc.arcmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.imooc.arcmenu.R;

public class ArcMenu extends ViewGroup implements OnClickListener {
    public static final int X_RIGHT = 1;
    public static final int X_LEFT = -1;
    public static final int Y_UP = -1;
    public static final int Y_DOWN = 1;

    private int duration = 300;
    private int mRadius = 180;

    public int getmRadius() {
        return mRadius;
    }

    public void setmRadius(int mRadius) {
        this.mRadius = (int) (getResources().getDisplayMetrics().density*mRadius);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 菜单的状态
     */
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 菜单的主按钮
     */
    private View mCButton;

    private OnMenuItemClickListener mMenuItemClickListener;
    private int xflag = X_RIGHT;
    private int yflag = Y_DOWN;
    private int mCButton_left;
    private int mCButton_top;

    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 点击子菜单项的回调接口
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置子菜单的方向
     *
     * @param xflag
     * @param yflag
     */
    public void setDirection(int xflag, int yflag) {
        this.xflag = xflag;
        this.yflag = yflag;
    }

    public void setTranslationDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            // 测量child
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutCButton();

            int count = getChildCount();

            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);

                child.setVisibility(View.GONE);

                int cl = (int) (mRadius * Math.sin(Math.PI / (count - 2)
                        * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / (count - 2)
                        * i));

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                //一级菜单图标和二级菜单图标中心对其
                cl = cl* xflag + (mCButton_left + mCButton.getMeasuredWidth() / 2 - child.getMeasuredWidth() / 2) ;
                ct = ct * yflag +( mCButton_top + mCButton.getMeasuredHeight() / 2 - child.getMeasuredHeight() / 2);

                Log.i("TAG", "CL : " + cl + "   CT : " + ct);
                child.layout(cl, ct, cl + cWidth, ct + cHeight);

            }

        }

    }

    /**
     * 定位主菜单按钮
     */
    private void layoutCButton() {
        mCButton = getChildAt(0);
        mCButton.setOnClickListener(this);

        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();

        mCButton_left = getMeasuredWidth() / 2 - width / 2;
        mCButton_top = getMeasuredHeight() / 2 - height / 2;
        mCButton.layout(mCButton_left, mCButton_top, mCButton_left + width, mCButton_top + height);

    }

    @Override
    public void onClick(View v) {

        //旋转主菜单
        //rotateCButton(v, 0f, 360f, 300);
        toggleMenu();
    }

    /**
     * 切换菜单
     */
    public void toggleMenu() {
        // 为menuItem添加平移动画和旋转动画
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);

            // end 0 , 0
            // start
            int cl = (int) (mRadius * Math.sin(Math.PI / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / (count - 2) * i));


            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;

            // to open
            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(-xflag * cl, 0, -yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);

            } else {  // to close
                tranAnim = new TranslateAnimation(0, -xflag * cl, 0, -yflag * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i * 100) / count);
            tranAnim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }
            });
            // 旋转动画
            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);

            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);

            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemClickListener != null)
                        mMenuItemClickListener.onClick(childView, pos);

                    menuItemAnim(pos - 1);
                    changeStatus();

                }
            });
        }
        // 切换菜单状态
        changeStatus();
    }

    /**
     * 添加menuItem的点击动画
     *
     * @param
     */
    private void menuItemAnim(int pos) {
        for (int i = 0; i < getChildCount() - 1; i++) {

            View childView = getChildAt(i + 1);
            if (i == pos) {
                childView.startAnimation(scaleBigAnim(300));
            } else {

                childView.startAnimation(scaleSmallAnim(300));
            }

            childView.setClickable(false);
            childView.setFocusable(false);

        }

    }

    private Animation scaleSmallAnim(int duration) {

        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;

    }

    /**
     * 为当前点击的Item设置变大和透明度降低的动画
     *
     * @param duration
     * @return
     */
    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);

        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;

    }

    /**
     * 切换菜单状态
     */
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
    }

    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }


    private void rotateCButton(View v, float start, float end, int duration) {

        RotateAnimation anim = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

}
