package com.supermap.dataconversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.supermap.data.DataConversion;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.mapping.MapControl;

public class ExportData extends PopupWindow implements OnClickListener {
	private MapControl mMapControl = null;
	private LayoutInflater mInflater = null;
	private View mContentView = null;
	private Datasource udbDatasource = null;
	private Context context = null;

	/**
	 * 构造函数
	 * 
	 * @param mapcontrol
	 */
	public ExportData(MapControl mapcontrol,Context context) {
		mMapControl = mapcontrol;
		this.context = context;
		mInflater = LayoutInflater.from(mMapControl.getContext());
		loadView();
		setContentView(mContentView);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		udbDatasource = mMapControl.getMap().getWorkspace().getDatasources()
				.get("World");
	}

	/**
	 * 初始化界面控件
	 */
	private void loadView() {
		mContentView = mInflater.inflate(R.layout.dataoperation, null);
		mContentView.findViewById(R.id.btn_dwg).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_dxf).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_mif).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_shp).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_tif).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_img).setOnClickListener(this);
	}

	/**
	 * 显示导出绘制工具栏
	 */
	public void showExport(View anchorView) {
		showAsDropDown(anchorView, 0, -2);
	}

	/**
	 * 关闭工具栏
	 */
	public void dismiss() {
		super.dismiss();
	}

	// 导入数据
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dwg:
			exportDwg(context);
			break;
		case R.id.btn_dxf:
			exportDxf(context);
			break;
		case R.id.btn_mif:
			exportMif(context);
			break;
		case R.id.btn_shp:
			exportShp(context);
			break;
		case R.id.btn_tif:
			exportTif(context);
			break;
		case R.id.btn_img:
			exportImg(context);
			break;

		default:
			break;
		}

	}

	/**
	 * 导出img数据
	 */
	private void exportImg(Context context) {
		// TODO Auto-generated method stub
		// 获取数据集
		Dataset imgDataset = udbDatasource.getDatasets()
				.get("imgImport");
		if (imgDataset == null) {
			Toast.makeText(context, "请先导入数据", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// 将数据集导出为img格式
			boolean imgExport = DataConversion.exportIMG(MainActivity.exportIMG,
					imgDataset);
			if (imgExport == true) {
				Toast.makeText(context, "导出img格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导出img格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出tif数据
	 */
	private void exportTif(Context context) {
		// 获取数据集
		Dataset tifDataset = udbDatasource.getDatasets()
				.get("tifImport");
		if (tifDataset == null) {
			Toast.makeText(context, "请先导入数据", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// 将数据集导出为tif格式
			boolean tifExport = DataConversion.exportTIF(MainActivity.exportTIF,
					tifDataset);
			if (tifExport == true) {
				Toast.makeText(context, "导出tif格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导出tif格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出shp数据
	 */
	private void exportShp(Context context) {
		// 获取数据集
		Dataset shpDataset =  udbDatasource.getDatasets()
				.get("shpImport");
		if (shpDataset == null) {
			Toast.makeText(context, "请先导入数据", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// 将数据集导出为shp格式
			boolean shpExport = DataConversion.exportSHP(MainActivity.exportSHP,
					shpDataset);
			if (shpExport == true) {
				Toast.makeText(context, "导出shp格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导出shp格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出mif数据
	 */
	private void exportMif(Context context) {
		// 获取数据集
		Dataset mifDataset =  udbDatasource.getDatasets()
				.get("mifImportR");
		if (mifDataset == null) {
			Toast.makeText(context, "请先导入数据", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// 将数据集导出为CAD格式
			boolean mifExport = DataConversion.exportMIF(MainActivity.exportMIF,
					mifDataset);
			if (mifExport == true) {
				Toast.makeText(context, "导出mif格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导出mif格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出dxf数据
	 */
	private void exportDxf(Context context) {
		// 获取数据集
		Dataset dxfDataset = udbDatasource.getDatasets()
				.get("dxfImport");
		if (dxfDataset == null) {
			Toast.makeText(context, "请先导入数据", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// 将数据集导出为CAD格式
			boolean dxfExport = DataConversion.exportDXF(MainActivity.exportDXF,
					dxfDataset);
			if (dxfExport == true) {
				Toast.makeText(context, "导出dxf格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导出dxf格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出dwg数据
	 */
	private void exportDwg(Context context) {
		// 获取数据集
		Dataset dwgDataset = udbDatasource.getDatasets()
				.get("dwgImport");
		if (dwgDataset == null) {
			Toast.makeText(context, "请先导入数据", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// 将数据集导出为dwg格式
			boolean dwgExport = DataConversion.exportDWG(MainActivity.exportDWG,
					dwgDataset);
			if (dwgExport == true) {
				Toast.makeText(context, "导出dwg格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导出dwg格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
