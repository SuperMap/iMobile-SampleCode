package com.supermap.ar.video.samples.arvideo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Sansteve on 2020/07/07.
 */

public class GLVideoRenderer implements GLSurfaceView.Renderer
        , SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnVideoSizeChangedListener {


    private static final String TAG = "GLVideoRenderer";
    private Context context;
    private int aPositionLocation;
    private int programId;
    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            1f, -1f, 0f,
            -1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
    };

    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    private int uGrayEffectLocation;
    private int uAlphaFactorLocation;


    private final float[] textureVertexData = {
            1f, 0f,
            0f, 0f,
            1f, 1f,
            0f, 1f
    };
    private FloatBuffer textureVertexBuffer;
    private int uTextureSamplerLocation;
    private int aTextureCoordLocation;
    private int textureId;

    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer2;

    private float[] mSTMatrix = new float[16];
    private int uSTMMatrixHandle;

    private boolean updateSurface;
    private int screenWidth, screenHeight;

    public GLVideoRenderer(Context context, String videoPath) {
        this.context = context;
        synchronized (this) {
            updateSurface = false;
        }
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);


        initMediaPlayer(videoPath);
    }

    private static final String VERTEX_SHADER_NAME = "vertex_shader.glsl";
    private static final String FRAGMENT_SHADER_NAME = "fragment_shader.glsl";


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vetext_sharder);
//        String fragmentShader = ShaderUtils.readRawTextFile(context, R.raw.fragment_sharder);
//        programId = ShaderUtils.createProgram(vertexShader, fragmentShader);
        int vertexShader = -1;
        int passthroughShader = -1;
        try {
            vertexShader = ShaderUtil.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
            passthroughShader = ShaderUtil.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        programId = GLES20.glCreateProgram();
        GLES20.glAttachShader(programId, vertexShader);
        GLES20.glAttachShader(programId, passthroughShader);
        GLES20.glLinkProgram(programId);
        GLES20.glUseProgram(programId);

        ShaderUtil.checkGLError(TAG, "programId");


        aPositionLocation = GLES20.glGetAttribLocation(programId, "aPosition");
        uMatrixLocation = GLES20.glGetUniformLocation(programId, "uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        uTextureSamplerLocation = GLES20.glGetUniformLocation(programId, "sTexture");
        aTextureCoordLocation = GLES20.glGetAttribLocation(programId, "aTexCoord");


//        uGrayEffectLocation = GLES20.glGetUniformLocation(programId, "uGrayEffects");

        uAlphaFactorLocation = GLES20.glGetUniformLocation(programId, "uAlphaFactor");


        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        ShaderUtil.checkGLError(TAG, "glBindTexture mTextureID");
   /*GLES11Ext.GL_TEXTURE_EXTERNAL_OES Its usefulness?
      As mentioned before, the output format of video decoding is YUV (YUV420p, should be), so the function of this extended texture is to realize the automatic conversion from YUV format to RGB,
      We don't need to write the code of YUV to RGB anymore*/
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId);

        surfaceTexture.setOnFrameAvailableListener(this);//Monitor whether a new frame of data arrives

        Surface surface = new Surface(surfaceTexture);


        mediaPlayer.setSurface(surface);


    }

    public void setPlaySpeed(float speed){
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
    }

    private float fragAlpha = 1.0f;

    public void setupAlpha(float alpha) {
        fragAlpha = alpha;
    }


    private void initMediaPlayer(String videoPath) {

        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(videoPath);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//            String path = "http://192.168.1.254:8192";
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.setDataSource(TextureViewMediaActivity.videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnVideoSizeChangedListener(this);

    }

    private boolean isFirstInit = false;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: " + width + " " + height);
        screenWidth = width;
        screenHeight = height;

        if (!isFirstInit) {
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            isFirstInit = true;
        }


    }


    public void setGrayScale(float factor) {


    }


    public void setOffset(float offset){

        mSTMatrix[13] -= offset;
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this) {
            if (updateSurface) {

                surfaceTexture.updateTexImage();//Get new data
                surfaceTexture.getTransformMatrix(mSTMatrix);//The definition of mSTMatrix is exactly the same as that of projectionMatrix.

                updateSurface = false;
            }
        }
        GLES20.glUseProgram(programId);


        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);


        GLES20.glUniform1f(uAlphaFactorLocation,fragAlpha);

//        float [] value = new float[1];
//        value[0] = fragAlpha;
//        GLES20.glUniform1fv(uAlphaFactorLocation, 1,value,0);


//        float[] mAlphaFactorMatrix = new float[4];
//        mAlphaFactorMatrix[0] = mAlphaFactorMatrix[1] = mAlphaFactorMatrix[2] = mAlphaFactorMatrix[3] = fragAlpha;
//        GLES20.glUniformMatrix2fv(uAlphaFactorLocation,1,false,mAlphaFactorMatrix,0);


        System.out.println("sansteve: alphavalue:"+fragAlpha);

        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);

        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glUniform1i(uTextureSamplerLocation, 0);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.d(TAG, "onVideoSizeChanged: " + width + " " + height);
        updateProjection(width, height);
    }

    private void updateProjection(int videoWidth, int videoHeight) {
        float screenRatio = (float) screenWidth / screenHeight;
        float videoRatio = (float) videoWidth / videoHeight;

        if (videoRatio < screenRatio) {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -videoRatio / screenRatio, videoRatio / screenRatio, -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -screenRatio / videoRatio, screenRatio / videoRatio, -1f, 1f, -1f, 1f);
        }


//        float[] rotateMatrix = new float[16];
//        Matrix.setIdentityM(rotateMatrix,0);
//        Matrix.rotateM(rotateMatrix,0,90,0,0,1);
//        Matrix.multiplyMM(projectionMatrix, 0, projectionMatrix, 0, rotateMatrix, 0);

    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


}