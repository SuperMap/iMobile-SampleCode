package com.supermap.android.speech;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的View:水波纹扩散圆
 */
public class DiffuseView extends View {

    /** 扩散圆圈颜色 */
    private int mColor = getResources().getColor(R.color.colorAccent);
    /** 圆圈中心颜色 */
    private int mCoreColor = getResources().getColor(R.color.colorPrimary);
    /** 圆圈中心图片 */
    private Bitmap mBitmap;
    /** 扩散时圆圈中心图片 */
    private Bitmap mDiffuseBitmap;
    /** 中心圆半径 */
    private float mCoreRadius = 80;
    /** 扩散圆宽度 */
    private int mDiffuseWidth = 2;
    /** 最大宽度 */
    private Integer mMaxWidth = 150;
    /** 扩散速度 */
    private int mDiffuseSpeed = 3;
    /** 是否正在扩散中 */
    private boolean mIsDiffuse = false;
    // 透明度集合
    private List<Integer> mAlphas = new ArrayList<>();
    // 扩散圆半径集合
    private List<Integer> mWidths = new ArrayList<>();
    private Paint mPaint;

    public DiffuseView(Context context) {
        this(context, null);
    }

    public DiffuseView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DiffuseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiffuseView, defStyleAttr, 0);
        mColor = a.getColor(R.styleable.DiffuseView_diffuse_color, mColor);
        mCoreColor = a.getColor(R.styleable.DiffuseView_diffuse_coreColor, mCoreColor);
        mCoreRadius = a.getFloat(R.styleable.DiffuseView_diffuse_coreRadius, mCoreRadius);
        mDiffuseWidth = a.getInt(R.styleable.DiffuseView_diffuse_width, mDiffuseWidth);
        mMaxWidth = a.getInt(R.styleable.DiffuseView_diffuse_maxWidth, mMaxWidth);
        mDiffuseSpeed = a.getInt(R.styleable.DiffuseView_diffuse_speed, mDiffuseSpeed);
        int imageId = a.getResourceId(R.styleable.DiffuseView_diffuse_coreImage, -1);
        if(imageId != -1) mBitmap = BitmapFactory.decodeResource(getResources(), imageId);
        int diffusecoreImage = a.getResourceId(R.styleable.DiffuseView_diffuse_diffusecoreImage, -1);
        if (diffusecoreImage != -1) mDiffuseBitmap = BitmapFactory.decodeResource(getResources(), diffusecoreImage);
        a.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mAlphas.add(255);
        mWidths.add(0);
    }

    @Override
    public void invalidate() {
        if(hasWindowFocus()){
            super.invalidate();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(hasWindowFocus){
            if (!isDiffuse()) {
                mWidths.clear();
                mAlphas.clear();
                mAlphas.add(255);
                mWidths.add(0);
            }
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isDiffuse()) {
            // 绘制扩散圆
            mPaint.setColor(mColor);
            for (int i = 0; i < mAlphas.size(); i ++) {
                // 设置透明度
                Integer alpha = mAlphas.get(i);
                mPaint.setAlpha(alpha);
                // 绘制扩散圆
                Integer width = mWidths.get(i);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCoreRadius + width, mPaint);

                //Log.d("TAG", "mMaxWidth =" + mMaxWidth);
                if (mMaxWidth >= 255) {
                    if(alpha > 0 && width < mMaxWidth){
                        mAlphas.set(i, alpha - mDiffuseSpeed > 0 ? alpha - mDiffuseSpeed : 1);
                        mWidths.set(i, width + mDiffuseSpeed);
                    }
                } else {
                    //透明度为255,当mMaxWidth小于255时，透明度不能递减到1
                    if(alpha > 0 && width < mMaxWidth){
                        int value = 1;
                        if(255 / mMaxWidth == 0){
                            value = 255 / mMaxWidth;
                        } else{
                            value = 255 / mMaxWidth + 1;
                        }
                        int d = mDiffuseSpeed * value;
                        mAlphas.set(i, alpha - d  > 0 ? alpha - d  : 1);
                        mWidths.set(i, width + mDiffuseSpeed);
                    }
                }
            }
            Integer integer = mAlphas.get(0);
            //Log.d("TAG", "最外层透明度：" + integer);

            // 判断当扩散圆扩散到指定宽度时添加新扩散圆
            if (mWidths.get(mWidths.size() - 1) >= mMaxWidth / mDiffuseWidth) {
                mAlphas.add(255);
                mWidths.add(0);
            }

            // 超过10个扩散圆，删除最外层
            if(mWidths.size() >= 5){
                mWidths.remove(0);
                mAlphas.remove(0);
            }

            if(mIsDiffuse){
                invalidate();
            }

        }

        // 绘制中心圆及图片
        mPaint.setAlpha(255);
        mPaint.setColor(mCoreColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCoreRadius, mPaint);

        if (mIsDiffuse) {
            if(mDiffuseBitmap != null){
                Bitmap bitmap = zoomImg(mDiffuseBitmap, 200, 200);
                canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2
                        , getHeight() / 2 - bitmap.getHeight() / 2, mPaint);
            }
        } else {
            if(mBitmap != null){
                Bitmap bitmap = zoomImg(mBitmap, 200, 200);
                canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2
                        , getHeight() / 2 - bitmap.getHeight() / 2, mPaint);
            }
        }

    }

    private Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 停止扩散
     */
    public void stop() {
        mIsDiffuse = false;
        mWidths.clear();
        mAlphas.clear();
        mAlphas.add(255);
        mWidths.add(0);
        invalidate();
    }

    /**
     * 开始扩散
     */
    public void start() {
        mIsDiffuse = true;
        invalidate();
    }

    /**
     * 是否扩散中
     */
    public boolean isDiffuse(){
        return mIsDiffuse;
    }

    /**
     * 设置扩散圆颜色
     */
    public void setColor(int colorId){
        mColor = colorId;
    }

    /**
     * 设置中心圆颜色
     */
    public void setCoreColor(int colorId){
        mCoreColor = colorId;
    }

    /**
     * 设置中心圆图片
     */
    public void setCoreImage(int imageId){
        mBitmap = BitmapFactory.decodeResource(getResources(), imageId);
    }

    /**
     * 设置扩散时中心圆图片
     */
    public void setDiffuseCoreImage(int imageId) {
        mDiffuseBitmap = BitmapFactory.decodeResource(getResources(), imageId);
    }

    /**
     * 设置中心圆半径
     */
    public void setCoreRadius(int radius){
        mCoreRadius = radius;
    }

    /**
     * 设置扩散圆宽度(值越小宽度越大)
     */
    public void setDiffuseWidth(int width){
        mDiffuseWidth = width;
    }

    /**
     * 设置最大宽度
     */
    public void setMaxWidth(int maxWidth){
        mMaxWidth = maxWidth;
    }

    /**
     * 设置扩散速度，值越大速度越快
     */
    public void setDiffuseSpeed(int speed){
        mDiffuseSpeed = speed;
    }
}
