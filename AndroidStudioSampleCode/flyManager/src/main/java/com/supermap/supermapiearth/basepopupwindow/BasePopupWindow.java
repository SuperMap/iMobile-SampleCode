
package com.supermap.supermapiearth.basepopupwindow;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.PopupWindow;

import java.lang.reflect.Field;

/**
 * 抽象通用popupwindow的父类
 */
public abstract class BasePopupWindow implements BasePopup, PopupWindow.OnDismissListener, PopupController {
    private static final String TAG = "BasePopupWindow";
    //元素定义
    private PopupWindowProxy mPopupWindow;
    //popup视图
    private View mPopupView;
    private Activity mContext;
    protected View mAnimaView;
    protected View mDismissView;
    //是否自动弹出输入�?(default:false)
    private boolean autoShowInputMethod = false;
    private OnDismissListener mOnDismissListener;
    private OnBeforeShowCallback mOnBeforeShowCallback;
    //anima
    private Animation mShowAnimation;
    private Animator mShowAnimator;
    private Animation mExitAnimation;
    private Animator mExitAnimator;

    private boolean isExitAnimaPlaying = false;
    private boolean needPopupFadeAnima = true;

    //option
    private int popupGravity = Gravity.NO_GRAVITY;
    private int offsetX;
    private int offsetY;
    private int popupViewWidth;
    private int popupViewHeight;
    //锚点view的location
    private int[] mAnchorViewLocation;
    //是否参�?�锚�?
    private boolean relativeToAnchorView;
    //是否自动适配popup的位�?
    private boolean isAutoLocatePopup;
    //showasdropdown
    private boolean showAtDown;
    //点击popup外部是否消失
    private boolean dismissWhenTouchOuside;

    public BasePopupWindow(Activity context) {
        initView(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public BasePopupWindow(Activity context, int w, int h) {
    	initView(context, w, h);
    }

    private void initView(Activity context, int w, int h) {
        mContext = context;
        
        mPopupView = onCreatePopupView();
        //默认占满全屏
        mPopupWindow = new PopupWindowProxy(mPopupView, w, h, this);
        mPopupWindow.setOnDismissListener(this);
        setDismissWhenTouchOuside(true);

        //修复可能出现的android 4.2的measure空指针问�?
        if (mPopupView != null) {
            int contentViewHeight = ViewGroup.LayoutParams.MATCH_PARENT;
            final ViewGroup.LayoutParams layoutParams = mPopupView.getLayoutParams();
            if (layoutParams != null && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                contentViewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentViewHeight);
            mPopupView.setLayoutParams(p);
            mPopupView.measure(w, h);
            popupViewWidth = mPopupView.getMeasuredWidth();
            popupViewHeight = mPopupView.getMeasuredHeight();
            mPopupView.setFocusableInTouchMode(true);
        }

        //默认是渐入动�?
        //setNeedPopupFade(Build.VERSION.SDK_INT <= 22);

        //=============================================================为外层的view添加点击事件，并设置点击消失
        //=============================================================元素获取
        mShowAnimator = initShowAnimator();
        mExitAnimation = initExitAnimation();
        mExitAnimator = initExitAnimator();

        mAnchorViewLocation = new int[2];
    }

    //------------------------------------------抽象-----------------------------------------------

    /**
     * PopupWindow展示出来后，�?要执行动画的View.�?般为蒙层之上的View
     */
   // protected abstract Animation initShowAnimation();

    /**
     * 设置�?个点击后触发dismiss PopupWindow的View，一般为蒙层
     */
  //  public abstract View getClickToDismissView();

    /**
     * 设置展示动画View的属性动�?
     */
    protected Animator initShowAnimator() {
        return null;
    }

    /**
     * 设置�?个拥有输入功能的View，一般为EditTextView
     */
    public EditText getInputView() {
        return null;
    }

    /**
     * 设置PopupWindow�?毁时的�??出动�?
     */
    protected Animation initExitAnimation() {
        return null;
    }

    /**
     * 设置PopupWindow�?毁时的�??出属性动�?
     */
    protected Animator initExitAnimator() {
        return null;
    }

    /**
     * popupwindow是否�?要淡入淡�?
     */
    public void setNeedPopupFade(boolean needPopupFadeAnima) {
        this.needPopupFadeAnima = needPopupFadeAnima;
      //  setPopupAnimaStyle(needPopupFadeAnima ? R.style.PopupAnimaFade : 0);
        setPopupAnimaStyle(needPopupFadeAnima ? 1: 0);
    }

    public boolean getNeedPopupFade() {
        return needPopupFadeAnima;
    }

    /**
     * 设置popup的动画style
     */
    public void setPopupAnimaStyle(int animaStyleRes) {
        if (animaStyleRes > 0) {
            mPopupWindow.setAnimationStyle(animaStyleRes);
        }
    }

    //------------------------------------------showPopup-----------------------------------------------

    /**
     * 调用此方法时，PopupWindow将会显示在DecorView
     */
    public void showPopupWindow() {
        if (checkPerformShow(null)) {
            tryToShowPopup(null);
        }
    }

    /**
     * 调用此方法时，PopupWindow左上角将会与anchorview左上角对�?
     *
     * @param anchorViewResid
     */
    public void showPopupWindow(int anchorViewResid) {
        View v = mContext.findViewById(anchorViewResid);
        showPopupWindow(v);
    }

    /**
     * 调用此方法时，PopupWindow左上角将会与anchorview左上角
     *
     * @param v
     */
    public void showPopupWindow(View v) {
        if (checkPerformShow(v)) {
            setRelativeToAnchorView(true);
            tryToShowPopup(v);
        }
    }

    //------------------------------------------Methods-----------------------------------------------
    private void tryToShowPopup(View v) {
        try {
            int offset[];
            //传�?�了view
            if (v != null) {
                offset = calcuateOffset(v);
                if (showAtDown) {
                    mPopupWindow.showAsDropDown(v, offset[0], offset[1]);
                } else {
                    mPopupWindow.showAtLocation(v, popupGravity, offset[0], offset[1]);
                }
            } else {
                //�?么都没传递，取顶级view的id
                mPopupWindow.showAtLocation(mContext.findViewById(android.R.id.content), popupGravity, offsetX, offsetY);
            }
            if (mShowAnimation != null && mAnimaView != null) {
                mAnimaView.clearAnimation();
                mAnimaView.startAnimation(mShowAnimation);
            }
            if (mShowAnimation == null && mShowAnimator != null && mAnimaView != null) {
                mShowAnimator.start();
            }
            //自动弹出键盘
            if (autoShowInputMethod && getInputView() != null) {
                getInputView().requestFocus();
                InputMethodUtils.showInputMethod(getInputView(), 150);
            }
        } catch (Exception e) {
            Log.e(TAG, "show error");
            e.printStackTrace();
        }
    }
    
    public void showAsDropDown(View view,int x,int y){
    	mPopupWindow.showAsDropDown(view, x, y);
    }
    
    public void showAtLocation(View view,int gravity,int x,int y){
    	mPopupWindow.showAtLocation(view, gravity, x, y);
    	
    }
    
    public void showAsDropDownBottom(View view,int x,int y){
    	mPopupWindow.showAsDropDownBottom(view, x, y);
    }

    /**
     * 暂时还不是很稳定，需要进�?步测试优�?
     *
     * @param anchorView
     * @return
     */
    private int[] calcuateOffset(View anchorView) {
        int[] offset = {0, 0};
        anchorView.getLocationOnScreen(mAnchorViewLocation);
        //当参考了anchorView，那么意味着必定使用showAsDropDown，此时popup初始显示位置在anchorView的底�?
        //因此�?要先将popupview与anchorView的左上角对齐
        if (relativeToAnchorView) {
            offset[0] = offset[0] + offsetX;
            offset[1] = -anchorView.getHeight() + offsetY;
        }

        if (isAutoLocatePopup) {
            final boolean onTop = (getScreenHeight() - mAnchorViewLocation[1] + offset[1] < popupViewHeight);
            if (onTop) {
                offset[1] = offset[1] - popupViewHeight + offsetY;
                showOnTop(mPopupView);
            } else {
                showOnDown(mPopupView);
            }
        }
        return offset;

    }

    /**
     * PopupWindow是否�?要自适应输入法，为输入法弹出让出区域
     *
     * @param needAdjust <br>
     *                   ture for "SOFT_INPUT_ADJUST_RESIZE" mode<br>
     *                   false for "SOFT_INPUT_ADJUST_NOTHING" mode
     */
    public void setAdjustInputMethod(boolean needAdjust) {
        if (needAdjust) {
            mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
    }

    /**
     * 当PopupWindow展示的时候，这个参数决定了是否自动弹出输入法
     * 如果使用这个方法，您必须保证通过 <strong>getInputView()<strong/>得到�?个EditTextView
     */
    public void setAutoShowInputMethod(boolean autoShow) {
        this.autoShowInputMethod = autoShow;
        if (autoShow) {
            setAdjustInputMethod(true);
        } else {
            setAdjustInputMethod(false);
        }
    }

    /**
     * 这个参数决定点击返回键是否可以取消掉PopupWindow
     */
    public void setBackPressEnable(boolean backPressEnable) {
        if (backPressEnable) {
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        } else {
            mPopupWindow.setBackgroundDrawable(null);
        }
    }

    /**
     * 这个方法封装了LayoutInflater.from(context).inflate，方便您设置PopupWindow�?用的xml
     *
     * @param resId reference of layout
     * @return root View of the layout
     */
    public View createPopupById(int resId) {
        if (resId != 0) {
            return LayoutInflater.from(mContext).inflate(resId, null);
        } else {
            return null;
        }
    }

    protected View findViewById(int id) {
        if (mPopupView != null && id != 0) {
            return mPopupView.findViewById(id);
        }
        return null;
    }

    /**
     * 是否允许popupwindow覆盖屏幕（包含状态栏�?
     */
    public void setPopupWindowFullScreen(boolean needFullScreen) {
        fitPopupWindowOverStatusBar(needFullScreen);
    }

    /**
     * 这个方法用于�?化您为View设置OnClickListener事件，多个View将会使用同一个点击事�?
     */
    protected void setViewClickListener(View.OnClickListener listener, View... views) {
        for (View view : views) {
            if (view != null && listener != null) {
                view.setOnClickListener(listener);
            }
        }
    }

    private void fitPopupWindowOverStatusBar(boolean needFullScreen) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
//                mLayoutInScreen.setAccessible(true);
//                mLayoutInScreen.set(mPopupWindow, needFullScreen);
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
    }
    //------------------------------------------Getter/Setter-----------------------------------------------

    /**
     * PopupWindow是否处于展示状�??
     */
    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public OnBeforeShowCallback getOnBeforeShowCallback() {
        return mOnBeforeShowCallback;
    }

    public void setOnBeforeShowCallback(OnBeforeShowCallback mOnBeforeShowCallback) {
        this.mOnBeforeShowCallback = mOnBeforeShowCallback;
    }

    public void setShowAnimation(Animation showAnimation) {
        if (mShowAnimation != null && mAnimaView != null) {
            mAnimaView.clearAnimation();
            mShowAnimation.cancel();
        }
        if (showAnimation != null && showAnimation != mShowAnimation) {
            mShowAnimation = showAnimation;
        }
    }

    public Animation getShowAnimation() {
        return mShowAnimation;
    }

    public void setShowAnimator(Animator showAnimator) {
        if (mShowAnimator != null) mShowAnimator.cancel();
        if (showAnimator != null && showAnimator != mShowAnimator) {
            mShowAnimator = showAnimator;
        }
    }

    public Animator getShowAnimator() {
        return mShowAnimator;
    }

    public void setExitAnimation(Animation exitAnimation) {
        if (mExitAnimation != null && mAnimaView != null) {
            mAnimaView.clearAnimation();
            mExitAnimation.cancel();
        }
        if (exitAnimation != null && exitAnimation != mExitAnimation) {
            mExitAnimation = exitAnimation;
        }
    }

    public Animation getExitAnimation() {
        return mExitAnimation;
    }

    public void setExitAnimator(Animator exitAnimator) {
        if (mExitAnimator != null) mExitAnimator.cancel();
        if (exitAnimator != null && exitAnimator != mExitAnimator) {
            mExitAnimator = exitAnimator;
        }
    }

    public Animator getExitAnimator() {
        return mExitAnimator;
    }

    public Activity getContext() {
        return mContext;
    }

    /**
     * 获取popupwindow的根布局
     *
     * @return
     */
    public View getPopupWindowView() {
        return mPopupView;
    }

    /**
     * 获取popupwindow实例
     *
     * @return
     */
    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public int getOffsetX() {
        return offsetX;
    }

    /**
     * 设定x位置的偏移量(中心点在popup的左上角)
     * <p>
     *
     * @param offsetX
     */
    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    /**
     * 设定y位置的偏移量(中心点在popup的左上角)
     *
     * @param offsetY
     */
    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getPopupGravity() {
        return popupGravity;
    }

    /**
     * 设置参�?�点，一般情况下，参考对象指的不是指定的view，�?�是它的windoToken，可以看作为整个screen
     *
     * @param popupGravity
     */
    public void setPopupGravity(int popupGravity) {
        this.popupGravity = popupGravity;
    }

    public boolean isRelativeToAnchorView() {
        return relativeToAnchorView;
    }

    /**
     * 是否参�?�锚点view，如果是true，则会显示到跟指定view的x,y�?样的位置(如果空间足够的话)
     *
     * @param relativeToAnchorView
     */
    public void setRelativeToAnchorView(boolean relativeToAnchorView) {
        setShowAtDown(true);
        this.relativeToAnchorView = relativeToAnchorView;
    }

    public boolean isAutoLocatePopup() {
        return isAutoLocatePopup;
    }

    public void setAutoLocatePopup(boolean autoLocatePopup) {
        setShowAtDown(true);
        isAutoLocatePopup = autoLocatePopup;
    }

    /**
     * 这个值是在创建view时进行测量的，并不能当作�?个完全准确的�?
     *
     * @return
     */
    public int getPopupViewWidth() {
        return popupViewWidth;
    }

    /**
     * 这个值是在创建view时进行测量的，并不能当作�?个完全准确的�?
     *
     * @return
     */
    public int getPopupViewHeight() {
        return popupViewHeight;
    }

    public boolean isShowAtDown() {
        return showAtDown;
    }

    /**
     * 决定使用showAtLocation还是showAsDropDown
     * decide showAtLocation/showAsDropDown
     *
     * @param showAtDown
     */
    public void setShowAtDown(boolean showAtDown) {
        this.showAtDown = showAtDown;
    }

    /**
     * 点击外部是否消失
     * <p>
     * dismiss popup when touch ouside from popup
     *
     * @param dismissWhenTouchOuside true for dismiss
     */
    public void setDismissWhenTouchOuside(boolean dismissWhenTouchOuside) {
        this.dismissWhenTouchOuside = dismissWhenTouchOuside;
        if (dismissWhenTouchOuside) {
            //指定透明背景，back键相�?
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        } else {
            mPopupWindow.setFocusable(false);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setBackgroundDrawable(null);
        }

    }

    public boolean isDismissWhenTouchOuside() {
        return dismissWhenTouchOuside;
    }

    //------------------------------------------状�?�控�?-----------------------------------------------

    /**
     * 取消�?个PopupWindow，如果有�?出动画，PopupWindow的消失将会在动画结束后执�?
     */
    public void dismiss() {
        try {
            mPopupWindow.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "dismiss error");
        }
    }

    @Override
    public boolean onBeforeDismiss() {
        return checkPerformDismiss();
    }

    @Override
    public boolean callDismissAtOnce() {
        boolean hasAnima = false;
        if (mExitAnimation != null && mAnimaView != null) {
            if (!isExitAnimaPlaying) {
                mExitAnimation.setAnimationListener(mAnimationListener);
                mAnimaView.clearAnimation();
                mAnimaView.startAnimation(mExitAnimation);
                isExitAnimaPlaying = true;
                hasAnima = true;
            }
        } else if (mExitAnimator != null) {
            if (!isExitAnimaPlaying) {
                mExitAnimator.removeListener(mAnimatorListener);
                mExitAnimator.addListener(mAnimatorListener);
                mExitAnimator.start();
                isExitAnimaPlaying = true;
                hasAnima = true;
            }
        }
        //如果有动画，则不立刻执行dismiss
        return !hasAnima;
    }

    /**
     * 直接消掉popup而不�?要动�?
     */
    public void dismissWithOutAnima() {
        if (!checkPerformDismiss()) return;
        try {
            if (mExitAnimation != null && mAnimaView != null) mAnimaView.clearAnimation();
            if (mExitAnimator != null) mExitAnimator.removeAllListeners();
            mPopupWindow.callSuperDismiss();
        } catch (Exception e) {
            Log.e(TAG, "dismiss error");
        }
    }


    private boolean checkPerformDismiss() {
        boolean callDismiss = true;
        if (mOnDismissListener != null) {
            callDismiss = mOnDismissListener.onBeforeDismiss();
        }
        return callDismiss && !isExitAnimaPlaying;
    }

    private boolean checkPerformShow(View v) {
        boolean result = true;
        if (mOnBeforeShowCallback != null) {
            result = mOnBeforeShowCallback.onBeforeShow(mPopupView, v, this.mShowAnimation != null || this.mShowAnimator != null);
        }
        return result;
    }

    //------------------------------------------Anima-----------------------------------------------

    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mPopupWindow.callSuperDismiss();
            isExitAnimaPlaying = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            isExitAnimaPlaying = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mPopupWindow.callSuperDismiss();
            isExitAnimaPlaying = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 生成TranslateAnimation
     *
     * @param durationMillis 动画显示时间
     * @param start          初始位置
     */
    protected Animation getTranslateAnimation(int start, int end, int durationMillis) {
        return SimpleAnimUtil.getTranslateAnimation(start, end, durationMillis);
    }

    /**
     * 生成ScaleAnimation
     * <p>
     * time=300
     */
    protected Animation getScaleAnimation(float fromX,
                                          float toX,
                                          float fromY,
                                          float toY,
                                          int pivotXType,
                                          float pivotXValue,
                                          int pivotYType,
                                          float pivotYValue) {
        return SimpleAnimUtil.getScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    /**
     * 生成自定义ScaleAnimation
     */
    protected Animation getDefaultScaleAnimation() {
        return SimpleAnimUtil.getDefaultScaleAnimation();
    }

    /**
     * 生成默认的AlphaAnimation
     */
    protected Animation getDefaultAlphaAnimation() {
        return SimpleAnimUtil.getDefaultAlphaAnimation();
    }

    /**
     * 从下方滑动上�?
     */
    protected AnimatorSet getDefaultSlideFromBottomAnimationSet() {
        return SimpleAnimUtil.getDefaultSlideFromBottomAnimationSet(mAnimaView);
    }

    /**
     * 获取屏幕高度(px)
     */
    public int getScreenHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    //------------------------------------------callback-----------------------------------------------
    protected void showOnTop(View mPopupView) {

    }

    protected void showOnDown(View mPopupView) {

    }

    @Override
    public void onDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
        isExitAnimaPlaying = false;
    }


    //------------------------------------------Interface-----------------------------------------------
    public interface OnBeforeShowCallback {
        /**
         * <b>return ture for perform show</b>
         *
         * @return
         */
        boolean onBeforeShow(View popupRootView, View anchorView, boolean hasShowAnima);


    }

    public static abstract class OnDismissListener implements PopupWindow.OnDismissListener {
        /**
         * <b>return ture for perform dismiss</b>
         *
         * @return
         */
        public boolean onBeforeDismiss() {
            return true;
        }
    }
    
    public void setFocusable(boolean value){
    	 mPopupWindow.setFocusable(value); 
    }
    
}
