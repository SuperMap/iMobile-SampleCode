package com.supermap.dnavi;

import com.example.a3dnavi.R;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.indoor.FloorListView;
import com.supermap.indoor3d.FloorListView3D;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.Navigation3D;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;
import com.supermap.realspace.SceneView;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private MapView mMapView;
	private MapControl mMapControl; // 地图显示控件	
	
	private SceneView mSceneView; // 地图显示控件	
	private SceneControl mSceneControl; // 地图显示控件	
	private Workspace mWorkspace; // 工作空间	
	private Scene mScene;
	private Navigation3D mNavigation3D = null;
	
	private FloorListView3D mFloorListView3D;
	private FloorListView mFloorListView;
	
	private Button mBtnChange;
	private NaviManager mNaviManager = null;
	
	private ImageView mImgMask;
	private ImageView mImgMap;
	
	private boolean mIs3D = true; //是否打开三维
	
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        openMap();
        openScene();
        initView();
    }

    // 打开三维场景
    private boolean openScene(){	
    	if(openWorkspace3d()){ 
    		// 将地图显示空间和 工作空间关联    
    		mSceneView= (SceneView)findViewById(R.id.scene_control);
    		mSceneControl= mSceneView.getSceneControl();
    		mSceneControl.setNavigationControlVisible(true);
    		
    		mSceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {
				
				@Override
				public void onSuccess(String arg0) {
					// TODO Auto-generated method stub
					mNavigation3D = mSceneView.getNavigation();
    		
		    		mScene = mSceneControl.getScene();
		    		mScene.setWorkspace(mWorkspace); 
		    		
		    		// 打开工作空间中地图的第2幅地图 
		    		String mapName = mWorkspace.getScenes().get(0);
		    		mScene.open(mapName);
		    		
					mFloorListView3D.setVisibility(View.VISIBLE);
					mFloorListView3D.setNavigation3D(mNavigation3D);
					mFloorListView3D.linkScenepControl(mSceneControl, mWorkspace);

					mNaviManager.initNavi3D(mSceneControl, mNavigation3D);					
					mFloorListView.setVisibility(View.INVISIBLE);
				}
			});

    		return true;
    	}
    	return false;
    }
        
    // 打开 三维工作空间
    private boolean openWorkspace3d(){
        mWorkspace = new Workspace();
        WorkspaceConnectionInfo m_info = new WorkspaceConnectionInfo();
        m_info.setServer(sdcard + "/SuperMap/Demos/3DNaviDemo/凯德Mall/凯德Mall.sxwu");
        m_info.setType(WorkspaceType.SXWU);
        return mWorkspace.open(m_info);
    } 

    // 打开地图
    private boolean openMap(){
	    mMapView = (MapView)findViewById(R.id.mapView);
    	mMapControl = mMapView.getMapControl();
    	
        if(openWorkspace2d()){ 	
            // 将地图显示空间和 工作空间关联    
        	mMapControl.getMap().setWorkspace(mWorkspace);      	
        		
            // 打开工作空间中地图的第2幅地图 
            String mapName = mWorkspace.getMaps().get(0);
            boolean isOpenMap = mMapControl.getMap().open(mapName);

//            mMapControl.getMap().setFullScreenDrawModel(true);
            if(isOpenMap){
                // 刷新地图，涉及地图的任何操作都需要调用该接口进行刷新
                mMapControl.getMap().refresh();
            }  
            return true;
        }
        
        return false;
    }
        
    // 打开 二维工作空间
    private boolean openWorkspace2d(){
        mWorkspace = new Workspace();

        WorkspaceConnectionInfo m_info = new WorkspaceConnectionInfo();
        m_info.setServer(sdcard+"/SuperMap/Demos/3DNaviDemo/室内外导航/beijing.smwu");
        m_info.setType(WorkspaceType.SMWU);;
        
        return mWorkspace.open(m_info);
    } 
    
    // 初始化控件，绑定监听器
	private void initView(){
		mBtnChange = (Button)findViewById(R.id.change_2d);
		mBtnChange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mIs3D) {
					mIs3D = false;
					Drawable drawable = getResources().getDrawable(R.drawable.change_3d);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					mBtnChange.setCompoundDrawables(null, drawable, null, null);
					mBtnChange.setText("三维");
					changeTo2D();
				} else {
					mIs3D = true;
					Drawable drawable = getResources().getDrawable(R.drawable.change_2d);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					mBtnChange.setCompoundDrawables(null, drawable, null, null);
					mBtnChange.setText("二维");
					changeTo3D();
				}
			}
		});
		
		mImgMask = (ImageView)findViewById(R.id.img_mask);
		mImgMap = (ImageView)findViewById(R.id.img_map);
		
		mFloorListView3D = (FloorListView3D)findViewById(R.id.floor_list_view_3d);
		mFloorListView = (FloorListView)findViewById(R.id.floor_list_view);

		mNaviManager = new NaviManager();
		mNaviManager.initNaviIndoor(mMapView);
		
		View view = findViewById(R.id.ly_left_tool);
		mNaviManager.setView(view);
	}
	
	private void changeTo2D() {
		mImgMask.setVisibility(View.VISIBLE);
		mImgMap.setVisibility(View.VISIBLE);
		applyRotation(70, 0);
		
		mImgMap.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ViewGroup vg = (ViewGroup) mMapView.getParent();
				ViewGroup.LayoutParams lp = mMapView.getLayoutParams();
				lp.width = vg.getWidth();
				mMapView.setLayoutParams(lp);
				
				mFloorListView.linkMapControl(mMapControl);
				mNaviManager.setFloorListView(mFloorListView);  
				mNaviManager.changeTo2D(true);
				mFloorListView3D.setVisibility(View.INVISIBLE);
			}
		}, 200);
	}
	
	
	
	private void changeTo3D() {
		mImgMask.setVisibility(View.VISIBLE);
		mImgMap.setVisibility(View.VISIBLE);
		applyRotation(0, 55);
		
		mImgMap.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ViewGroup.LayoutParams lp = mMapView.getLayoutParams();
				lp.width = 1;
				mMapView.setLayoutParams(lp);
				
				mNaviManager.changeTo2D(false);
				mFloorListView3D.setVisibility(View.VISIBLE);
				mFloorListView.setVisibility(View.INVISIBLE);
			}
		}, 200);
	}

	private void applyRotation(float start, float end) {
        // 计算中心点
        final float centerX = mImgMap.getWidth() / 2.5f;
        final float centerY = mImgMap.getHeight() / 2.0f;

        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
                centerX, centerY, 10.0f, false);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        // 设置监听
        rotation.setAnimationListener(new DisplayNextView());

        mImgMap.startAnimation(rotation);
    }
    
    private final class DisplayNextView implements Animation.AnimationListener {

        public void onAnimationStart(Animation animation) {
        }

        // 动画结束
        public void onAnimationEnd(Animation animation) {
        	//imgText.post(new SwapViews());
    		
    		mImgMap.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mImgMask.setVisibility(View.INVISIBLE);
					mImgMap.setVisibility(View.INVISIBLE);
					mImgMap.clearAnimation();
					mImgMap.invalidate();
				}
			}, 500);
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }
}
