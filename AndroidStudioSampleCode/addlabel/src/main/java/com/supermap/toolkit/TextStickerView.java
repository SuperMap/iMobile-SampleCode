package com.supermap.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.supermap.addlable.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本贴图处理控件
 */
public class TextStickerView extends View {

    private static final String TAG = "++++++++++++++++++";
    private static final int STICKER_BTN_HALF_SIZE = 30;

    private static final float TEXT_SIZE_DEFAULT = 80;
    private static final int PADDING = 32;

    private TextPaint textPaint = new TextPaint();
    private Paint mHelpPaint = new Paint();

    private Rect mTextRect = new Rect();// warp text rect record
    private RectF mHelpBoxRect = new RectF();
    private Rect mDeleteRect = new Rect();//删除按钮位置
    private Rect mRotateRect = new Rect();//旋转按钮位置

    private RectF mDeleteDstRect = new RectF();
    private RectF mRotateDstRect = new RectF();

    private Bitmap mDeleteBitmap;
    private Bitmap mRotateBitmap;

    private int mCurrentMode = IDLE_MODE;

    //控件的几种模式
    private static final int IDLE_MODE = 2;//正常
    private static final int MOVE_MODE = 3;//移动模式
    private static final int ROTATE_MODE = 4;//旋转模式
    private static final int DELETE_MODE = 5;//删除模式

    protected int layout_x = 0;
    protected int layout_y = 0;

    private float last_x = 0;
    private float last_y = 0;

    protected float mRotateAngle = 0;
    protected float mScale = 1;
    private boolean isInitLayout = true;

    private boolean isShowHelpBox = true;

    private boolean mAutoNewLine = false;//是否需要自动换行
    private List<String> mTextContents = new ArrayList<String>(2);//存放所写的文字内容
    private String mText;

    private Point mPoint = new Point(0, 0);

    private Canvas mcanvas;

    public TextStickerView(Context context) {
        super(context);
        initView(context);
    }


    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sticker_delete);
        mRotateBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sticker_rotate);

        //设置按钮大小
        mDeleteRect.set(0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight());
        mRotateRect.set(0, 0, mRotateBitmap.getWidth(), mRotateBitmap.getHeight());

        mDeleteDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
        mRotateDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(TEXT_SIZE_DEFAULT);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        mHelpPaint.setColor(Color.BLACK);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setAntiAlias(true);
        mHelpPaint.setStrokeWidth(4);

    }


    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public String getText() {
        String text = this.mText;
        return text;
    }

    public void setTextColor(int newColor) {
        textPaint.setColor(newColor);
        invalidate();
    }

    public int getTextColor() {
        int color = textPaint.getColor();
        return color;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInitLayout) {
            isInitLayout = false;
            resetView();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mcanvas = canvas;
        if (TextUtils.isEmpty(mText))
            return;

        parseText();
        drawContent(mcanvas);
    }

    protected void parseText() {
        if (TextUtils.isEmpty(mText))
            return;

        mTextContents.clear();

        String[] splits = mText.split("\n");
        for (String text : splits) {
            mTextContents.add(text);
        }//end for each
    }

    private void drawContent(Canvas canvas) {
        drawText(canvas);

        //draw x and rotate button
        int offsetValue = ((int) mDeleteDstRect.width()) >> 1;
        mDeleteDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top - offsetValue);
        mRotateDstRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.bottom - offsetValue);

        RectUtil.rotateRect(mDeleteDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);
        RectUtil.rotateRect(mRotateDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);

        if (!isShowHelpBox) {
            return;
        }

        canvas.save();
        canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);
        canvas.restore();

        canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
        canvas.drawBitmap(mRotateBitmap, mRotateRect, mRotateDstRect, null);

    }

    private void drawText(Canvas canvas) {
        drawText(canvas, layout_x, layout_y, mScale, mRotateAngle);
    }

    public void drawText(Canvas canvas, int _x, int _y, float scale, float rotate) {
        if (ListUtil.isEmpty(mTextContents))
            return;

        int x = _x;
        int y = _y;
        int text_height = 0;

        mTextRect.set(0, 0, 0, 0);//clear
        Rect tempRect = new Rect();
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int charMinHeight = Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom);//字体高度
        text_height = charMinHeight;
        //System.out.println("top = "+fontMetrics.top +"   bottom = "+fontMetrics.bottom);
        for (int i = 0; i < mTextContents.size(); i++) {
            String text = mTextContents.get(i);
            textPaint.getTextBounds(text, 0, text.length(), tempRect);
            if (tempRect.height() <= 0) {//处理此行文字为空的情况
                tempRect.set(0, 0, 0, text_height);
            }

            RectUtil.rectAddV(mTextRect, tempRect, 0, charMinHeight);
        }

        mTextRect.offset(x, y);

        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
        RectUtil.scaleRect(mHelpBoxRect, scale);

        canvas.save();
        canvas.scale(scale, scale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.rotate(rotate, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        int draw_text_y = y + (text_height >> 1) + PADDING;
        for (int i = 0; i < mTextContents.size(); i++) {
            canvas.drawText(mTextContents.get(i), x, draw_text_y, textPaint);
            draw_text_y += text_height;
        }//end for i
        canvas.restore();
    }

    public void resetView() {
        layout_x = getMeasuredWidth() / 2;
        layout_y = getMeasuredHeight() / 2;
        mRotateAngle = 0;
        mScale = 1;
        mTextContents.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {// 删除模式
                    isShowHelpBox = true;
                    mCurrentMode = DELETE_MODE;
                } else if (mRotateDstRect.contains(x, y)) {// 旋转按钮
                    isShowHelpBox = true;
                    mCurrentMode = ROTATE_MODE;
                    last_x = mRotateDstRect.centerX();
                    last_y = mRotateDstRect.centerY();
                    ret = true;
                } else if (detectInHelpBox(x, y)) {// 移动模式
                    isShowHelpBox = true;
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    ret = true;
                } else {
                    isShowHelpBox = false;
                    invalidate();
                }// end if

                if (mCurrentMode == DELETE_MODE) {// 删除选定贴图
                    mCurrentMode = IDLE_MODE;// 返回空闲状态
                    clearTextContent();
                    invalidate();
                }// end if
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (mCurrentMode == MOVE_MODE) {// 移动贴图
                    mCurrentMode = MOVE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    layout_x += dx;
                    layout_y += dy;

                    invalidate();

                    last_x = x;
                    last_y = y;
                } else if (mCurrentMode == ROTATE_MODE) {// 旋转 缩放文字操作
                    mCurrentMode = ROTATE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    updateRotateAndScale(dx, dy);

                    invalidate();
                    last_x = x;
                    last_y = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                mCurrentMode = IDLE_MODE;
                break;
        }// end switch

        return ret;
    }

    /**
     * 考虑旋转情况下 点击点是否在内容矩形内
     *
     * @param x
     * @param y
     * @return
     */
    private boolean detectInHelpBox(float x, float y) {
        //mRotateAngle
        mPoint.set((int) x, (int) y);
        //旋转点击点
        RectUtil.rotatePoint(mPoint, mHelpBoxRect.centerX(), mHelpBoxRect.centerY(), -mRotateAngle);
        return mHelpBoxRect.contains(mPoint.x, mPoint.y);
    }

    public void clearTextContent() {
//        if (mEditText != null) {
//            mEditText.setText(null);
//        }
        setText(null);

    }


    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float dx, final float dy) {
        float c_x = mHelpBoxRect.centerX();
        float c_y = mHelpBoxRect.centerY();

        float x = mRotateDstRect.centerX();
        float y = mRotateDstRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        mScale *= scale;
        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        mRotateAngle += angle;
    }

    public float getRotateAngle() {
        return mRotateAngle;
    }

    public com.supermap.data.Point getpoint() {
        com.supermap.data.Point point = new com.supermap.data.Point();
        Paint.FontMetricsInt metricsInt=textPaint.getFontMetricsInt();
        int height=Math.abs(metricsInt.top) + Math.abs(metricsInt.bottom);
        point.setX((int) (mTextRect.left+height/2));
        point.setY((int) (mTextRect.top+height/2));
        return point;
    }

    public float getScale() {
        return mScale;
    }

    public double getTextHeight() {
        double TextHeight;
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//        TextHeight = Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);//字体高度
        if(last_y <= 0 ){
            last_y = Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);//字体高度
        }
        TextHeight = last_y;
        Log.i("++++++++++", String.valueOf(TextHeight));
        return TextHeight;
    }

    public double getTextWeight() {
        double Textweight;
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        Textweight = Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom);//字体高度
        return Textweight;

    }

}

class RectUtil {
    /**
     * 缩放指定矩形
     *
     * @param rect
     * @param scale
     */
    public static void scaleRect(RectF rect, float scale) {
        float w = rect.width();
        float h = rect.height();

        float newW = scale * w;
        float newH = scale * h;

        float dx = (newW - w) / 2;
        float dy = (newH - h) / 2;

        rect.left -= dx;
        rect.top -= dy;
        rect.right += dx;
        rect.bottom += dy;
    }

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    public static void rotateRect(RectF rect, float center_x, float center_y,
                                  float roatetAngle) {
        float x = rect.centerX();
        float y = rect.centerY();
        float sinA = (float) Math.sin(Math.toRadians(roatetAngle));
        float cosA = (float) Math.cos(Math.toRadians(roatetAngle));
        float newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA;
        float newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA;

        float dx = newX - x;
        float dy = newY - y;

        rect.offset(dx, dy);
    }

    /**
     * 旋转Point点
     *
     * @param p
     * @param center_x
     * @param center_y
     * @param roatetAngle
     */
    public static void rotatePoint(Point p, float center_x, float center_y,
                                   float roatetAngle) {
        float sinA = (float) Math.sin(Math.toRadians(roatetAngle));
        float cosA = (float) Math.cos(Math.toRadians(roatetAngle));
        // calc new point
        float newX = center_x + (p.x - center_x) * cosA - (p.y - center_y) * sinA;
        float newY = center_y + (p.y - center_y) * cosA + (p.x - center_x) * sinA;
        p.set((int) newX, (int) newY);
    }


    /**
     * 矩形在Y轴方向上的加法操作
     *
     * @param srcRect
     * @param addRect
     * @param padding
     */
    public static void rectAddV(final RectF srcRect, final RectF addRect, int padding) {
        if (srcRect == null || addRect == null)
            return;

        float left = srcRect.left;
        float top = srcRect.top;
        float right = srcRect.right;
        float bottom = srcRect.bottom;

        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width();
        }

        bottom += padding + addRect.height();

        srcRect.set(left, top, right, bottom);
    }

    /**
     * 矩形在Y轴方向上的加法操作
     *
     * @param srcRect
     * @param addRect
     * @param padding
     */
    public static void rectAddV(final Rect srcRect, final Rect addRect, int padding, int charMinHeight) {
        if (srcRect == null || addRect == null)
            return;

        int left = srcRect.left;
        int top = srcRect.top;
        int right = srcRect.right;
        int bottom = srcRect.bottom;

        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width();
        }

        bottom += padding + Math.max(addRect.height(), charMinHeight);

        srcRect.set(left, top, right, bottom);
    }
}

class ListUtil {
    public static boolean isEmpty(List list) {
        if (list == null)
            return true;

        return list.size() == 0;
    }

}

