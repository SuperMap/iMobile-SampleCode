package com.supermap.clip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    //    声明Paint对象
    private Paint mPaint = null;
    private int StrokeWidth = 5;
    private Rect rect = new Rect(0, 0, 0, 0);//手动绘制矩形
    private ReactLinListener reactLinListener;

    public GameView(Context context) {
        super(context);
        //开启线程
        // new Thread(this).start();
    }

    public GameView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setReactCallBack(ReactLinListener listener){
         reactLinListener=listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //构建对象
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        //设置无锯齿
        mPaint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(StrokeWidth);
        mPaint.setAlpha(100);
        mPaint.setColor(Color.RED);
        canvas.drawRect(rect, mPaint);
        reactLinListener.reactCallBack(rect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rect.right += StrokeWidth;
                rect.bottom += StrokeWidth;
                invalidate(rect);
                rect.left = x;
                rect.top = y;
                rect.right = rect.left;
                rect.bottom = rect.top;

            case MotionEvent.ACTION_MOVE:
                Rect old = new Rect(rect.left, rect.top, rect.right + StrokeWidth, rect.bottom + StrokeWidth);
                rect.right = x;
                rect.bottom = y;
                old.union(x, y);
                invalidate(old);
                break;

            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;//处理了触摸信息，消息不再传递
    }
}
