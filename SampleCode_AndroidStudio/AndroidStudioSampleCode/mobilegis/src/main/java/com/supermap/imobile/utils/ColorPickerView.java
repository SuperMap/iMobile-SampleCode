package com.supermap.imobile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.supermap.imobile.myapplication.R;

public class ColorPickerView extends View {

    private Context mContext;
    private Paint mRightPaint;            //画笔
    private int mHeight;                  //view高
    private int mWidth;                   //view宽
    private Bitmap mLeftBitmap;
    private Bitmap bitmapTemp;

    private Paint mBitmapPaint;//画笔
    private PointF mLeftSelectPoint;//坐标
    private OnColorBackListener onColorBackListener;
    private int mLeftBitmapRadius;
    public String colorStr = "";
    private int initX = 0;
    private int initY = 0;
    private int r;//半径

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setOnColorBackListener(OnColorBackListener listener) {
        onColorBackListener = listener;
    }

    //初始化资源与画笔
    private void init() {
        bitmapTemp = BitmapFactory.decodeResource(getResources(), R.drawable.color_wheel);
        mRightPaint = new Paint();
        mRightPaint.setStyle(Paint.Style.FILL);
        mRightPaint.setStrokeWidth(1);
        mBitmapPaint = new Paint();

        mLeftBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.color_wheel_dot);
        mLeftBitmapRadius = mLeftBitmap.getWidth() / 2;
        //获取圆心
        initX = bitmapTemp.getWidth() / 2;
        initY = bitmapTemp.getHeight() / 2;
        r = bitmapTemp.getHeight() / 2;
        mLeftSelectPoint = new PointF(0, 0);
    }


    //important patient please!!!
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmapTemp, null, new Rect(0, 0, mWidth, mHeight), mBitmapPaint);
        if (mLeftSelectPoint.x != 0 || mLeftSelectPoint.y != 0) {
            canvas.drawBitmap(mLeftBitmap, mLeftSelectPoint.x - mLeftBitmapRadius,
                    mLeftSelectPoint.y - mLeftBitmapRadius, mBitmapPaint);
        }
        //默认显示在中间
        else {
            canvas.drawBitmap(mLeftBitmap, bitmapTemp.getWidth() / 2 - mLeftBitmapRadius, bitmapTemp.getWidth() / 2 - mLeftBitmapRadius,
                    mBitmapPaint
            );
            getRGB((int) (bitmapTemp.getWidth() / 2 - mLeftBitmapRadius), (int) (bitmapTemp.getWidth() / 2 - mLeftBitmapRadius));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = bitmapTemp.getWidth();
        mHeight = bitmapTemp.getHeight();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                proofLeft(x, y);
                invalidate();
                getRGB((int) mLeftSelectPoint.x, (int) mLeftSelectPoint.y);
                break;
            case MotionEvent.ACTION_UP:
                //取色
                getRGB((int) mLeftSelectPoint.x, (int) mLeftSelectPoint.y);
                invalidate();
        }
        return true;
    }


    private String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(
                Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 像素转RGB
     */
    private void getRGB(int x, int y) {
        int pixel = bitmapTemp.getPixel(x, y);
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);
        int a = Color.alpha(pixel);
        colorStr = getColorStr(r, g, b);  //十六进制的颜色字符串。
        if (onColorBackListener != null) {
            onColorBackListener.onColorBack(a, r, g, b);
        }
    }

    /**
     * 当view离开附着的窗口时触发，该方法和 onAttachedToWindow() 是相反
     */
    @Override
    protected void onDetachedFromWindow() {
        if (mLeftBitmap != null && mLeftBitmap.isRecycled() == false) {
            mLeftBitmap.recycle();//图片回收
        }
        if (bitmapTemp != null && bitmapTemp.isRecycled() == false) {
            bitmapTemp.recycle();//图片回收
        }
        super.onDetachedFromWindow();
    }

    // 校正xy
    private void proofLeft(float x, float y) {
        int r = bitmapTemp.getWidth() / 2 - mLeftBitmapRadius / 2;//圆半径
        //北
        PointF N = new PointF(initX, initY - r);//北
        PointF S = new PointF(initX, initY + r);//南
        PointF W = new PointF(initX - r, initY);//西
        PointF E = new PointF(initX + r, initY);//东
        int a = twoSpotGetLine(initX, initY, x, y);//圆心到点

        if (a < r) {//在圆内
            mLeftSelectPoint.x = x;
            mLeftSelectPoint.y = y;
        } else {
            double angle = 0;
            int c = r;
            int b = 0;
            int newx = 0;
            int newy = 0;
            if (x > initX) {//二四象限 NE SE
                if (y > initY) {//四象限 东南ES
                    b = twoSpotGetLine(S.x, S.y, x, y);//南点到点
                    double aoccos = (Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2 * a * c);
                    angle = 90d - (Math.acos(aoccos) * (180 / Math.PI));//角度
                    Log.e("getLeftColor", "角度东南ES a: " + a + ",b: " + b + ",c: " + c + ",angle:" + angle);
                    if (angle % 90 == 0) {
                        newx = initX;
                        newy = initY + r;
                    }
                } else if (y < initY) {//二象限 北东EN
                    b = twoSpotGetLine(E.x, E.y, x, y);//北点到点
                    double aoccos = (Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2 * a * c);
                    angle = 360d - (Math.acos(aoccos) * (180 / Math.PI));//角度
                    Log.e("getLeftColor", "角度北东EN a: " + a + ",b: " + b + ",c: " + c + ",angle:" + angle);
                    if (angle % 90 == 0) {
                        newx = initX + r;
                        newy = initY;
                    }
                }
            } else {//一三象限
                if (y > initY) {//一象限 西北WN
                    b = twoSpotGetLine(W.x, W.y, x, y);//西点到点
                    double aoccos = (Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2 * a * c);
                    angle = 180d - (Math.acos(aoccos) * (180 / Math.PI));//角度
                    Log.e("getLeftColor", "角度西北WN a: " + a + ",b: " + b + ",c: " + c + ",angle:" + angle);
                    if (angle % 90 == 0) {
                        newx = initX - r;
                        newy = initY;
                    }
                } else if (y < initY) {//三象限 南西SW
                    b = twoSpotGetLine(N.x, N.y, x, y);//东点到点
                    double aoccos = (Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2 * a * c);
                    angle = 270d - (Math.acos(aoccos) * (180 / Math.PI));//角度
                    Log.e("getLeftColor", "角度南西SW a: " + a + ",b: " + b + ",c: " + c + ",angle:" + angle);
                    if (angle % 90 == 0) {
                        newx = initX;
                        newy = initY - r;
                    }
                }
            }
            if (angle % 90 != 0) {
                newx = (int) (initX + r * Math.cos(angle * Math.PI / 180));
                newy = (int) (initY + r * Math.sin(angle * Math.PI / 180));
            }
            Log.e("getLeftColor", "新坐标 x: " + newx + ",y: " + newy);
            mLeftSelectPoint.x = newx;
            mLeftSelectPoint.y = newy;
        }

//		图片区域
//		if (x < 0) {
//			mLeftSelectPoint.x = 0;
//		} else if (x > (LEFT_WIDTH)) {
//			mLeftSelectPoint.x = LEFT_WIDTH;
//		} else {
//			mLeftSelectPoint.x = x;
//		}
//
//		Log.e("proofLeft()", "proofLeft: " + x + "," + y);
//		if (x < 0) {
//			mLeftSelectPoint.x = 0;
//		} else if (x > (LEFT_WIDTH)) {
//			mLeftSelectPoint.x = LEFT_WIDTH;
//		} else {
//			mLeftSelectPoint.x = x;
//		}
//		if (y < 0) {
//			mLeftSelectPoint.y = 0;
//		} else if (y > (mHeight - 0)) {
//			mLeftSelectPoint.y = mHeight - 0;
//		} else {
//			mLeftSelectPoint.y = y;
//		}
    }

    /**
     * 两点取直线
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private int twoSpotGetLine(float x1, float y1, float x2, float y2) {
        double line2 = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2);
        return (int) Math.abs(Math.sqrt(line2));
    }

    /**
     * 获取当前16位进制颜色
     *
     * @return
     */
    public String getColorStr() {
        return colorStr;
    }

    protected void setColorStr(String colorStr) {
        this.colorStr = colorStr;
    }

    /**
     * 颜色选择器监听
     */
    public interface OnColorBackListener {
        public void onColorBack(int a, int r, int g, int b);
    }

    /**
     * 根据指定rgb，获取16位进制颜色
     *
     * @param r
     * @param b
     * @param g
     * @return
     */
    public String getColorStr(int r, int b, int g) {
        colorStr = "#" + toBrowserHexValue(r) + toBrowserHexValue(g)
                + toBrowserHexValue(b);    //十六进制的颜色字符串。
        return colorStr;
    }
}


