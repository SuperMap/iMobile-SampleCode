
package com.supermap.imobile.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.GLES10;
import android.opengl.GLU;
import android.opengl.Matrix;

import com.supermap.ar.ArSensorListener;
import com.supermap.data.Point2D;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class SimpleRenderer
		extends ARRenderer
		implements ArSensorListener {

	private int markerID = -1;

	private double scaleBase = 0.0;   // 缩放基准

	private float ratio = 0.0f;

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		GLES10.glViewport(0, 0, w, h);
		ratio = (float) w / h;

		UGMapMatrixPerspective(finalProjectionMatrix, 0,45f, ratio, 0.1f, 10000f);  //酒仙桥2数据
		System.out.println("SurfaceRatio:"+ratio);
	}


	public Point2D pnt2DMatching = null;

	private boolean initMARactivityFlag = false;

	private double RADIANS = 180/ Math.PI;

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	long timeOldPaint = 0;
	long timeNewPaint = 0;


	public boolean getARSceneControlFlag() {
		return mARSceneControlFlag;
	}

	public void setARSceneControlFlag(boolean mARSceneControlFlag) {
		this.mARSceneControlFlag = mARSceneControlFlag;
	}

	private boolean mARSceneControlFlag = false;


	//----------------------------------
	private static final float[] sInclination = new float[16];
	private float[] mAccelerometerValues = new float[3];
	private float[] mMagneticValues = new float[3];
	private float[] mRotationMatrix = new float[16];
	private float[] mRemappedRotationMatrix = new float[16];

	private float[] mBackupOfTransMatrix = new float[16];
	private float[] mBackupOfRemapRotationMatrix = new float[16];
	private float[] mResultMatrix = new float[16];
	private float[] mFinalMatrix = new float[16];

	private float [] finalTransformMatrix  = new float[16];
	private float [] finalProjectionMatrix = new float[16];

	//----------------------------------
	private boolean invertMatrix(float mInv[], int mInvOffset, float src[], int mOffset)
	{
		// Invert a 4 x 4 matrix using Cramer's Rule
		// transpose matrix
		float src0  = src[mOffset +  0];
		float src4  = src[mOffset +  1];
		float src8  = src[mOffset +  2];
		float src12 = src[mOffset +  3];

		float src1  = src[mOffset +  4];
		float src5  = src[mOffset +  5];
		float src9  = src[mOffset +  6];
		float src13 = src[mOffset +  7];

		float src2  = src[mOffset +  8];
		float src6  = src[mOffset +  9];
		float src10 = src[mOffset + 10];
		float src14 = src[mOffset + 11];

		float src3  = src[mOffset + 12];
		float src7  = src[mOffset + 13];
		float src11 = src[mOffset + 14];
		float src15 = src[mOffset + 15];

		// calculate pairs for first 8 elements (cofactors)
		float atmp0  = src10 * src15;
		float atmp1  = src11 * src14;
		float atmp2  = src9  * src15;
		float atmp3  = src11 * src13;
		float atmp4  = src9  * src14;
		float atmp5  = src10 * src13;
		float atmp6  = src8  * src15;
		float atmp7  = src11 * src12;
		float atmp8  = src8  * src14;
		float atmp9  = src10 * src12;
		float atmp10 = src8  * src13;
		float atmp11 = src9  * src12;

		// calculate first 8 elements (cofactors)
		float dst0  = (atmp0 * src5 + atmp3 * src6 + atmp4  * src7)
				- (atmp1 * src5 + atmp2 * src6 + atmp5  * src7);
		float dst1  = (atmp1 * src4 + atmp6 * src6 + atmp9  * src7)
				- (atmp0 * src4 + atmp7 * src6 + atmp8  * src7);
		float dst2  = (atmp2 * src4 + atmp7 * src5 + atmp10 * src7)
				- (atmp3 * src4 + atmp6 * src5 + atmp11 * src7);
		float dst3  = (atmp5 * src4 + atmp8 * src5 + atmp11 * src6)
				- (atmp4 * src4 + atmp9 * src5 + atmp10 * src6);
		float dst4  = (atmp1 * src1 + atmp2 * src2 + atmp5  * src3)
				- (atmp0 * src1 + atmp3 * src2 + atmp4  * src3);
		float dst5  = (atmp0 * src0 + atmp7 * src2 + atmp8  * src3)
				- (atmp1 * src0 + atmp6 * src2 + atmp9  * src3);
		float dst6  = (atmp3 * src0 + atmp6 * src1 + atmp11 * src3)
				- (atmp2 * src0 + atmp7 * src1 + atmp10 * src3);
		float dst7  = (atmp4 * src0 + atmp9 * src1 + atmp10 * src2)
				- (atmp5 * src0 + atmp8 * src1 + atmp11 * src2);

		// calculate pairs for second 8 elements (cofactors)
		float btmp0  = src2 * src7;
		float btmp1  = src3 * src6;
		float btmp2  = src1 * src7;
		float btmp3  = src3 * src5;
		float btmp4  = src1 * src6;
		float btmp5  = src2 * src5;
		float btmp6  = src0 * src7;
		float btmp7  = src3 * src4;
		float btmp8  = src0 * src6;
		float btmp9  = src2 * src4;
		float btmp10 = src0 * src5;
		float btmp11 = src1 * src4;

		// calculate second 8 elements (cofactors)
		float dst8  = (btmp0  * src13 + btmp3  * src14 + btmp4  * src15)
				- (btmp1  * src13 + btmp2  * src14 + btmp5  * src15);
		float dst9  = (btmp1  * src12 + btmp6  * src14 + btmp9  * src15)
				- (btmp0  * src12 + btmp7  * src14 + btmp8  * src15);
		float dst10 = (btmp2  * src12 + btmp7  * src13 + btmp10 * src15)
				- (btmp3  * src12 + btmp6  * src13 + btmp11 * src15);
		float dst11 = (btmp5  * src12 + btmp8  * src13 + btmp11 * src14)
				- (btmp4  * src12 + btmp9  * src13 + btmp10 * src14);
		float dst12 = (btmp2  * src10 + btmp5  * src11 + btmp1  * src9 )
				- (btmp4  * src11 + btmp0  * src9  + btmp3  * src10);
		float dst13 = (btmp8  * src11 + btmp0  * src8  + btmp7  * src10)
				- (btmp6  * src10 + btmp9  * src11 + btmp1  * src8 );
		float dst14 = (btmp6  * src9  + btmp11 * src11 + btmp3  * src8 )
				- (btmp10 * src11 + btmp2  * src8  + btmp7  * src9 );
		float dst15 = (btmp10 * src10 + btmp4  * src8  + btmp9  * src9 )
				- (btmp8  * src9  + btmp11 * src10 + btmp5  * src8 );

		// calculate determinant
		float det =
				src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3;

		if (det == 0.0f) {
			return false;
		}

		// calculate matrix inverse
		float invdet = 1.0f / det;
		mInv[     mInvOffset] = dst0  * invdet;
		mInv[ 1 + mInvOffset] = dst1  * invdet;
		mInv[ 2 + mInvOffset] = dst2  * invdet;
		mInv[ 3 + mInvOffset] = dst3  * invdet;

		mInv[ 4 + mInvOffset] = dst4  * invdet;
		mInv[ 5 + mInvOffset] = dst5  * invdet;
		mInv[ 6 + mInvOffset] = dst6  * invdet;
		mInv[ 7 + mInvOffset] = dst7  * invdet;

		mInv[ 8 + mInvOffset] = dst8  * invdet;
		mInv[ 9 + mInvOffset] = dst9  * invdet;
		mInv[10 + mInvOffset] = dst10 * invdet;
		mInv[11 + mInvOffset] = dst11 * invdet;

		mInv[12 + mInvOffset] = dst12 * invdet;
		mInv[13 + mInvOffset] = dst13 * invdet;
		mInv[14 + mInvOffset] = dst14 * invdet;
		mInv[15 + mInvOffset] = dst15 * invdet;

		return true;
	}

	@Override
	public void onSensorChanged(float[] filteredValues, SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				mAccelerometerValues = filteredValues;
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				mMagneticValues = filteredValues;
				break;
			default:
				break;
		}


	}

	public float [] getProjectionMatrix(){
		if (ARToolKit.getInstance().isRunning()) {
			if(mARSceneControlFlag == false) //如果不是注册模式
			{
				return ARToolKit.getInstance().getProjectionMatrix();

			}else{

				return finalProjectionMatrix;
			}
		}
		return null;
	}


	public float [] getTransformMatrix(){
		if (ARToolKit.getInstance().isRunning()) {
			return finalTransformMatrix;
		}
		return null;
	}

	void UGMapMatrixPerspective(float m[], int offset, float fovy, float aspect, float zNear, float zFar)
	{
		float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0));
		float rangeReciprocal = 1.0f / (zNear - zFar);

		m[offset + 0] = f / aspect;
		m[offset + 1] = 0.0f;
		m[offset + 2] = 0.0f;
		m[offset + 3] = 0.0f;

		m[offset + 4] = 0.0f;
		m[offset + 5] = f;
		m[offset + 6] = 0.0f;
		m[offset + 7] = 0.0f;

		m[offset + 8] = 0.0f;
		m[offset + 9] = 0.0f;
		m[offset + 10] = (zFar + zNear) * rangeReciprocal;
		m[offset + 11] = -1.0f;

		m[offset + 12] = 0.0f;
		m[offset + 13] = 0.0f;
		m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
		m[offset + 15] = 0.0f;
	}




	public void onDrawFrame(GL10 gl) {


		System.out.println("onDrawFramw");

		if (ARToolKit.getInstance().isRunning()) {

			{//添加AR场景效果。
				SensorManager.getInclination(sInclination);
				SensorManager.getRotationMatrix(mRotationMatrix, sInclination, mAccelerometerValues, mMagneticValues);
				SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_Y,
						SensorManager.AXIS_MINUS_X, mRemappedRotationMatrix);
			}

			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			//gl.glFrustumf(-ratio,ratio,-ratio,ratio,5f,10000f);
//			gl.glEnable(GL10.GL_CULL_FACE);    //面部剔除
//			gl.glShadeModel(GL10.GL_SMOOTH);   //阴影模式
			gl.glEnable(GL10.GL_DEPTH_TEST);   //深度测试
//			gl.glFrontFace(GL10.GL_CW);        //正面朝向


			if(mARSceneControlFlag == false){
                gl.glMatrixMode(GL10.GL_PROJECTION); //specify which matrix is the current matrix
                gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0); //将相机视角中的世界投影到2维屏幕上

            }else{
				gl.glMatrixMode(GL10.GL_PROJECTION);
//				gl.glLoadIdentity();
				// gl.glFrustumf(-ratio, ratio, -1, 1, 1f, 100);
				GLU.gluPerspective(gl, 45f, ratio, 1f, 10000f);  //酒仙桥2数据
				{ //找到识别成功后的场景之后控制不动。

					float  [] invertMatrixOfLastRemaped = new float[16];

					invertMatrix(invertMatrixOfLastRemaped,0,mBackupOfRemapRotationMatrix,0);

					float [] tempMatrix = new float[16];

					Matrix.multiplyMM(tempMatrix,0,mRemappedRotationMatrix,0,invertMatrixOfLastRemaped,0);

					Matrix.multiplyMM(mResultMatrix,0,tempMatrix,0,mBackupOfTransMatrix,0);


					//3.在渲染器中更新地图矩阵。

					//传出结果矩阵
					System.arraycopy(mResultMatrix,0,finalTransformMatrix,0,16);

//					mARActivity.mapControl.getMap().setProjectMatrix(ARToolKit.getInstance().getProjectionMatrix());
//					mARActivity.mapControl.getMap().setTransformMatrix(mResultMatrix);
//

//					mARActivity.mapControl.getMap().setARMapType(5);

//					timeNewPaint = System.currentTimeMillis();
//					if ( Math.abs(timeOldPaint - timeNewPaint) > 200 ){
//						long timeMapBeforePaint = System.currentTimeMillis();
//
//					//	mARActivity.mapControl.getMap().refresh();
//
//						long timeMapAfterPaint = System.currentTimeMillis();
//						System.out.println("come in ARMap222222_Map: " + (timeMapAfterPaint - timeMapBeforePaint) );
//						timeOldPaint = timeNewPaint;
//					}



				}
			}


			if (ARToolKit.getInstance().queryMarkerVisible(markerID) && mARSceneControlFlag == false) {
					float[] fTrans = ARToolKit.getInstance().queryMarkerTransformation(markerID);
					if (fTrans != null){
						gl.glLoadMatrixf(fTrans, 0);

//						if(mARActivity != null) {

							//在这里初始化设置底层ARMap的类型，queryMarkerVisible之外会报空指针异常

//							mARActivity.mapControl.getMap().setARMapType(5);

//							timeNewPaint = System.currentTimeMillis();

						//	mARActivity.mapControl.getMap().setProjectMatrix(ARToolKit.getInstance().getProjectionMatrix());

							System.arraycopy(fTrans,0,finalTransformMatrix,0,16);

							if(mARSceneControlFlag == false){ //默认情况一直识别
								gl.glMatrixMode(GL10.GL_MODELVIEW);
								//先保存一份trans的备份
								System.arraycopy(fTrans,0,mBackupOfTransMatrix,0,16);
								System.arraycopy(mRemappedRotationMatrix,0, mBackupOfRemapRotationMatrix,0,16);

								//--------------------------这里控制缩放比例尺.---------------------------

								//传出结果矩阵
							//	System.arraycopy(fTrans,0,finalTransformMatrix,0,16);
//								mARActivity.mapControl.getMap().setTransformMatrix(fTrans);
							}



//							if ( Math.abs(timeOldPaint - timeNewPaint) > 200 ){
//								long timeMapBeforePaint = System.currentTimeMillis();
//								mARActivity.mapControl.getMap().refresh();
//								long timeMapAfterPaint = System.currentTimeMillis();
//								System.out.println("come in ARMap222222_Map: " + (timeMapAfterPaint - timeMapBeforePaint) );
//
//								timeOldPaint = timeNewPaint;
//							}



//						}
					}
			}

		}

	}


	@Override
	public boolean configureARScene() {
		markerID = ARToolKit.getInstance().addMarker("single;Data/patt.hiro;80");
//		markerID = ARToolKit.getInstance().addMarker("single;"+sdcard+"/SampleData/Data/patt.supermap;80");
		if (markerID < 0) return false;
		return true;
	}





	ARActivity mARActivity = null;
	public void setARActivity(ARActivity arActivity){
		mARActivity = arActivity;
	}
	public ARActivity getARActivity(){
		return mARActivity;
	}

	ARSimple mARSimple = null;
	public  void setARSimple(ARSimple arSimple){
		mARSimple = arSimple;
	}
	public ARSimple getARSimple(){
		return mARSimple;
	}







}