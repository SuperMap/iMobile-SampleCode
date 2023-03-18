package com.supermap.osgbmodelcolor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osgbmodelcolor.R;
import com.supermap.data.AltitudeMode;
import com.supermap.data.Color;
import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.osgbmodelcolor.ColorPickerView.OnColorChangedListener;
import com.supermap.realspace.Camera;
import com.supermap.realspace.Layer3DOSGBFile;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Title:OSGB图层设置模型颜色的范例代码程序
 *
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 *
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 *
 * 1、范例简介：示范用户Layer3DOSGBFile图层设置模型颜色。
 * 2、示例数据：将SampleData/CBD_android/中的数据拷贝到安装目录\SuperMap\data\ 3、关键类型/成员:
 * SceneControl.getScene 方法 Scene.open 方法
 * layer3dosgbFile.setObjectsColor(ids, Color);
 *
 *s
 * 4、使用步骤：
 *  (1)运行程序。
 *  (2)读取assets/OSGBModel.json,获取指定id的建筑。
 *  (3)点击颜色按钮选择颜色，选择透明度，设置指定id建筑的颜色。
 * -----------------------------------------------------------------------------
 * - ==========================================================================
 * ==>
 *
 *
 * Company: 北京超图软件股份有限公司
 *
 */
public class MainActivity extends Activity {

	private Workspace workspace;
	private Scene scene;
	private SceneControl sceneControl;
	// 离线三维场景数据名称
	private String workspacePath = "/sdcard/SuperMap/data/CBD_android/CBD_android.sxwu";
	// 三维场景名称
	private String sceneName = "CBD_android";
	// 三维场景名称
	private WorkspaceConnectionInfo info;
	private WorkspaceType workspaceTypetemp = null;
	private boolean isLicenseAvailable = false;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	private BaseAdapter adapter_main;
	private String[] itemNames;
	private ListView listView_main;
	private MyView myView;
	private int color_r = 0, color_g = 0, color_b = 0;
	private TextView tv_dialog;
	private HashMap<Integer, Boolean> faMap = new HashMap<Integer, Boolean>();
	static class ViewHolder {

		public TextView textView;
		public ImageView iv_modelcolor;

	}
	private List<Map<String, String>> data;
	private final static String fileName = "OSGBModel.json";
	private ProgressDialog pd;
	private int Position = 1000;
	private int temp = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.initialization(this);
		Environment.setLicensePath(sdcard + "/SuperMap/license/");
		info = new WorkspaceConnectionInfo();
		setContentView(R.layout.activity_main);
		// 获取当前许可的状态，返回true 许可可用，返回false 许可不可用，不可用情况下无法打开本地场景。
		isLicenseAvailable = isLicenseAvailable();
		tv_dialog = (TextView) findViewById(R.id.tv_dialog);
		myView = new MyView(this);
		listView_main = (ListView) findViewById(R.id.listView_main);
		sceneControl = (SceneControl) findViewById(R.id.sceneControl);
		data = new ArrayList<Map<String, String>>();
		pd = new ProgressDialog(this);
		pd.setMessage("数据加载中……");
		new DataThread().start();
		// 在非按钮事件、非触摸事件中,需要在此接口中写有关scene的方法，防止场景控件绘制失败。
		sceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {

			@Override
			public void onSuccess(String success) {
				if (isLicenseAvailable) {
					openLocalScene();
				}

			}

		});
		listView_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

				double x = Double.parseDouble(data.get(position).get("longitude").trim());
				double y = Double.parseDouble(data.get(position).get("latitude").trim());
				double z = Double.parseDouble(data.get(position).get("altitude").trim()) * 3;
				Camera camera = new Camera(x, y, z, AltitudeMode.ABSOLUTE, 0.0, 40.0);
				sceneControl.getScene().flyToCamera(camera, AltitudeMode.ABSOLUTE.value(), 1);

			}

		});
		myView.colorPickerView.setOnColorChangedListenner(new OnColorChangedListener() {
			/**
			 * 手指抬起，选定颜色时
			 */
			@Override
			public void onColorChanged(int r, int g, int b) {
				if (r == 0 && g == 0 && b == 0) {
					return;
				}
				color_r = r;
				color_g = g;
				color_b = b;

			}

			/**
			 * 颜色移动的时候
			 */
			@Override
			public void onMoveColor(int r, int g, int b) {
				if (r == 0 && g == 0 && b == 0) {
					return;
				}
			}
		});

		myView.btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				myView.dismiss();
				int id = Integer.parseInt(data.get(Position).get("ID").trim());
				Layer3DOSGBFile layer3dosgbFile = (Layer3DOSGBFile) sceneControl.getScene().getLayers()
						.get("Building@CBD");
				int ids[] = { id };
				layer3dosgbFile.setObjectsColor(ids, new Color(color_r, color_g, color_b, temp * 25 / 10));
				update(Position, listView_main);

			}
		});

		myView.btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				color_r = 0;
				color_g = 0;
				color_b = 0;
			}
		});

		// seekbar监听器
		myView.mSeekbar.setOnSeekBarChangeListener(new myListener());

	}

	/**
	 * 加载数据线程
	 */
	class DataThread extends Thread {

		@Override
		public void run() {
			String jsonStr = getJson(fileName);
			setData(jsonStr);
			dataHandler.sendMessage(dataHandler.obtainMessage());
		}

	}

	/**
	 * 加载数据线程完成处理Handler
	 */
	Handler dataHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (pd != null) {
				pd.dismiss();
			}
			initAdapterMain();
			listView_main.setAdapter(adapter_main);
		}
	};

	/**
	 * @author：Supermap
	 * @注释 ：初始化ListView_main对应的适配器
	 */
	private void initAdapterMain() {

		int count = data.size();
		itemNames = new String[count];
		for (int i = 0; i < count; i++) {
			itemNames[i] = data.get(i).get("ID");
			faMap.put(i, false);
		}
		adapter_main = new BaseAdapter() {
			ViewHolder viewHolder;

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					viewHolder = new ViewHolder();
					convertView = getLayoutInflater().inflate(R.layout.item, null);
					viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_modelid);
					viewHolder.iv_modelcolor = (ImageView) convertView.findViewById(R.id.iv_modelcolor);
					convertView.setTag(viewHolder);

				} else {
					viewHolder = (ViewHolder) convertView.getTag();

				}
				viewHolder.textView.setText(itemNames[position]);
				viewHolder.iv_modelcolor.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Position = position;
						myView.show(tv_dialog);
					}
				});
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return itemNames.length;
			}
		};
	}

	/**
	 * 读取本地文件中JSON字符串
	 *
	 * @param fileName
	 * @return
	 */
	private String getJson(String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
			String line;
			while ((line = bf.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	/**
	 * 将JSON字符串转化为Adapter数据
	 *
	 * @param str
	 */
	private void setData(String str) {
		try {

			JSONObject object = new JSONObject(str);

			JSONArray array = object.getJSONArray("models");
			int len = array.length();
			Map<String, String> map;
			for (int i = 0; i < len; i++) {
				JSONObject jobject = array.getJSONObject(i);
				map = new HashMap<String, String>();
				map.put("ID", jobject.getString("ID"));
				map.put("longitude", jobject.getString("longitude"));
				map.put("latitude", jobject.getString("latitude"));
				map.put("altitude", jobject.getString("altitude"));
				data.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 打开一个本地场景
	private void openLocalScene() {

		// 新建一个工作空间对象
		if (workspace == null) {
			workspace = new Workspace();
		}
		// 根据工作空间类型，设置服务路径和类型信息。
		workspaceTypetemp = WorkspaceType.SXWU;
		info.setServer(workspacePath);
		info.setType(workspaceTypetemp);
		// 场景关联工作空间
		if (workspace.open(info)) {
			scene = sceneControl.getScene();
			scene.setWorkspace(workspace);
		}
		// 打开场景
		boolean successed = scene.open(sceneName);
		if (successed) {
			Toast.makeText(MainActivity.this, "打开场景成功", Toast.LENGTH_LONG);
		}
	}

	// 判断许可是否可用
	private boolean isLicenseAvailable() {
		LicenseStatus licenseStatus = Environment.getLicenseStatus();
		if (!licenseStatus.isLicenseExsit()) {
			Toast.makeText(MainActivity.this, "许可不存在，场景打开失败，请加入许可", Toast.LENGTH_LONG).show();
			return false;
		} else if (!licenseStatus.isLicenseValid()) {
			Toast.makeText(MainActivity.this, "许可过期，场景打开失败，请更换有效许可", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// 时长
	class myListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			temp = progress;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}

	}

	private void update(int position, ListView lv) {
		int visiblePosition = lv.getFirstVisiblePosition();
		View view = lv.getChildAt(position - visiblePosition);
		ImageView iv_modelcolor = (ImageView) view.findViewById(R.id.iv_modelcolor);
		GradientDrawable gradientDrawable=(GradientDrawable)iv_modelcolor.getBackground();
		gradientDrawable.setColor(android.graphics.Color.rgb(color_r, color_g, color_b));
	}

}