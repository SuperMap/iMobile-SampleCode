package com.supermap.osgbmodelcolor;

import com.example.osgbmodelcolor.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ColorPickerView extends ImageView {
	Context context;
	private Bitmap iconBitMap;
	float iconRadius;// 
	float iconCenterX;
	float iconCenterY;
	PointF iconPoint;//

	public ColorPickerView(Context context) {
		this(context, null);
	}

	public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init();
	}

	Paint mBitmapPaint;
	Bitmap imageBitmap;
	float viewRadius;// 
	float radius;// 

	private void init() {
		iconBitMap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.pickup);// 
		iconRadius = iconBitMap.getWidth() / 2;// 

		mBitmapPaint = new Paint();
		iconPoint = new PointF();

		imageBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		radius = imageBitmap.getHeight() / 2;// 

		
		iconPoint.x = radius;
		iconPoint.y = radius;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	Canvas canvas;

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		this.canvas = canvas;

		viewRadius = this.getWidth() / 2;

		canvas.drawBitmap(iconBitMap, iconPoint.x - iconRadius, iconPoint.y
				- iconRadius, mBitmapPaint);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int pixel;
		int r;
		int g;
		int b;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			proofLeft(x, y);
			pixel = getImagePixel(iconPoint.x, iconPoint.y);
			r = Color.red(pixel);
			g = Color.green(pixel);
			b = Color.blue(pixel);
			if (mChangedListener != null) {
				mChangedListener.onMoveColor(r, g, b);
			}
			if (isMove) {
				isMove = !isMove;
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			pixel = getImagePixel(iconPoint.x, iconPoint.y);
			r = Color.red(pixel);
			g = Color.green(pixel);
			b = Color.blue(pixel);
			if (mChangedListener != null) {
				mChangedListener.onColorChanged(r, g, b);
			}
			break;

		default:
			break;
		}
		return true;
	}

	public int getImagePixel(float x, float y) {

		Bitmap bitmap = imageBitmap;
		
		int intX = (int) x;
		int intY = (int) y;
		if (intX < 0)
			intX = 0;
		if (intY < 0)
			intY = 0;
		if (intX >= bitmap.getWidth()) {
			intX = bitmap.getWidth() - 1;
		}
		if (intY >= bitmap.getHeight()) {
			intY = bitmap.getHeight() - 1;
		}
		int pixel = bitmap.getPixel(intX, intY);
		return pixel;

	}

	/**
	 * R = sqrt(x * x + y * y)<br>
	 * point.x = x * r / R + r <br>
	 * point.y = y * r / R + r
	 */
	private void proofLeft(float x, float y) {

		float h = x - viewRadius; 
		float w = y - viewRadius;
		float h2 = h * h;
		float w2 = w * w;
		float distance = (float) Math.sqrt((h2 + w2)); 
		if (distance > radius) { 
			float maxX = x - viewRadius;
			float maxY = y - viewRadius;
			x = ((radius * maxX) / distance) + viewRadius; 
			y = ((radius * maxY) / distance) + viewRadius;
		}
		iconPoint.x = x;
		iconPoint.y = y;

		isMove = true;
	}

	boolean isMove;

	public void setOnColorChangedListenner(OnColorChangedListener l) {
		this.mChangedListener = l;
	}

	private OnColorChangedListener mChangedListener;

	public interface OnColorChangedListener {
		void onColorChanged(int r, int g, int b);

		void onMoveColor(int r, int g, int b);
	}
}
