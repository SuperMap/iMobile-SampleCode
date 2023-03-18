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


/**
 * <p>
 * Title:三维室内导航
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范如何运用行业导航模块实现三维室内导航
 * 2、示例数据：数据目录："/sdcard/SuperMap/Demos/3DNaviDemo/"
 *          	许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *      Navigation2.setNetWorkDataset();      方法
 *      Navigation2.loadModel();              方法
 *      Navigation2.load();                   方法
 *	    Navigation2.setStartPoint();          方法
 *      Navigation2.setDestinationPoint();    方法
 *      Navigation2.isGuiding();              方法
 *      Navigation2.cleanPath();              方法
 *      Navigation2.stopGuide();              方法
 *      Navigation2.startGuide();             方法
 *      Navigation2.routeAnalyst();           方法
 *
 * 4、使用步骤：
 *  (1)点击【设置起点】按钮，在地图上长按一点设置起点
 *  (2)点击【设置终点】按钮，在地图上长按另一点设置终点
 *  (3)电机【设置途径点】按钮，在地图上长按设置途径点
 *  (4)点击【路径分析】按钮，进行路径分析，显示导航路径
 *  (5)路径分析结束后，若点击【模拟导航】按钮，将进行模拟引导，并在地图上显示引导过程
 *  (6)导航进行中，若点击【停止导航】，可以停止导航
 *  (7)停止导航后，点击【清除】按钮，可以清除现有路径结果，再重新分析路径
 *  (8)点击【二维】按钮，可以将地图切换为二维
 *  (9)右边为楼层切换 及显示控制按钮
 *
 *
 * 5、注意：
 *	需要依赖navigationplus.aar包
 *	如果运行本范例失败，常见原因是缺少语音资源。
 *  解决办法：请将产品包中Resource文件夹下的voice文件夹拷贝到工程目录中的assets文件夹下。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

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
