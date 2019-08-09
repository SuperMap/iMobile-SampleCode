package com.supermap.dataconversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.supermap.data.Charset;
import com.supermap.data.DataConversion;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.EncodeType;
import com.supermap.data.conversion.ImportMode;
import com.supermap.data.conversion.ImportSettingIMG;
import com.supermap.data.conversion.ImportSettingTIF;
import com.supermap.data.conversion.MultiBandImportMode;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;

public class ImportData extends PopupWindow implements OnClickListener {
	private MapControl mMapControl = null;
	private LayoutInflater mInflater = null;
	private View mContentView = null;
	private Datasource udbDatasource = null;
	private Dataset dataset = null;
	private Layer mLayer = null;
	private Context context = null;
	/**
	 * 构造函数
	 * 
	 * @param mapcontrol
	 */
	public ImportData(MapControl mapcontrol,Context context) {
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
	 * 显示导入绘制工具栏
	 */
	public void showImport(View anchorView) {
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
			importDwg(context);
			break;
		case R.id.btn_dxf:
			importDxf(context);
			break;
		case R.id.btn_mif:
			importMif(context);
			break;
		case R.id.btn_shp:
			importShp(context);
			break;
		case R.id.btn_tif:
			importTif(context);
			break;
		case R.id.btn_img:
			importImg(context);
			break;

		default:
			break;
		}

	}
	
	/**
	 * 导入img数据
	 */
	private void importImg(Context context) {
		// TODO Auto-generated method stub
		removeLayer();
		mMapControl.getMap().refresh();
		try {
			boolean d = udbDatasource.getDatasets().delete("imgImport");
			
			ImportSettingIMG importSettingIMG = new ImportSettingIMG();
			importSettingIMG.setSourceFilePath(MainActivity.importIMG);
			importSettingIMG.setTargetDatasetName("imgImport");
			importSettingIMG.setTargetDatasource(udbDatasource);
			importSettingIMG.setSourceFileCharset(Charset.UTF8);
			
			importSettingIMG.setMultiBandImportMode(MultiBandImportMode.COMPOSITE);
			importSettingIMG.setTargetEncodeType(EncodeType.DCT);
			importSettingIMG.setImportMode(ImportMode.OVERWRITE);
			importSettingIMG.setBuildPyramid(true);

			
			// 将CAD数据添加到数据集
			boolean img = DataConversion.importIMG(importSettingIMG);

			if (img == true) {
				dataset = udbDatasource.getDatasets().get(
						"imgImport");
				addMap();
				Toast.makeText(context, "导入img格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导入img格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 导入tif数据
	 */
	private void importTif(Context context) {
		removeLayer();
		mMapControl.getMap().refresh();
		try {
			boolean d = udbDatasource.getDatasets().delete("tifImport");
			
			ImportSettingTIF importSettingTIF = new ImportSettingTIF();
			importSettingTIF.setSourceFilePath(MainActivity.importTIF);
			importSettingTIF.setTargetDatasetName("tifImport");
			importSettingTIF.setTargetDatasource(udbDatasource);
			importSettingTIF.setSourceFileCharset(Charset.UTF8);
			
			importSettingTIF.setMultiBandImportMode(MultiBandImportMode.COMPOSITE);
			importSettingTIF.setTargetEncodeType(EncodeType.DCT);
			importSettingTIF.setImportingAsGrid(false);
			importSettingTIF.setImportMode(ImportMode.OVERWRITE);
			importSettingTIF.setBuildPyramid(true);
			
			// 将CAD数据添加到数据集
			boolean tif = DataConversion.importTIF(importSettingTIF);

			if (tif == true) {
				dataset = udbDatasource.getDatasets().get(
						"tifImport");
				addMap();
				Toast.makeText(context, "导入tif格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导入tif格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 导入shp数据
	 */
	private void importShp(Context context) {
		removeLayer();
		mMapControl.getMap().refresh();
		try {
			boolean d = udbDatasource.getDatasets().delete("shpImport");
			// 将CAD数据添加到数据集
			boolean shp = DataConversion.importSHP(MainActivity.importSHP,
					udbDatasource);

			if (shp == true) {
				dataset = udbDatasource.getDatasets().get(
						"shpImport");
				addMap();
				Toast.makeText(context, "导入shp格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导入shp格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入mif数据
	 */
	private void importMif(Context context) {
		removeLayer();
		mMapControl.getMap().refresh();
		try {
			boolean d = udbDatasource.getDatasets().delete("mifImportR");
			// 将mif数据添加到数据集
			boolean mif = DataConversion.importMIF(MainActivity.importMIF,
					udbDatasource);

			if (mif == true) {
				dataset =  udbDatasource.getDatasets().get(
						"mifImportR");
				addMap();
				Toast.makeText(context, "导入mif格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导入mif格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入dxf数据
	 */
	private void importDxf(Context context) {
		removeLayer();
		mMapControl.getMap().refresh();
		try {
			boolean d = udbDatasource.getDatasets().delete("dxfImport");
			// 将CAD数据添加到数据集
			boolean dxf = DataConversion.importDXF(MainActivity.importDXF,
					udbDatasource, true);

			if (dxf == true) {
				dataset = udbDatasource.getDatasets().get(
						"dxfImport");
				addMap();
				Toast.makeText(context, "导入dxf格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导入dxf格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入dwg数据
	 */
	private void importDwg(Context context) {
		removeLayer();
		mMapControl.getMap().refresh();
		try {
			boolean d = udbDatasource.getDatasets().delete("dwgImport");
			// 将CAD数据添加到数据集
			boolean dwg = DataConversion.importDWG(MainActivity.importDWG,
					udbDatasource, true);
			if (dwg == true) {
				dataset =  udbDatasource.getDatasets().get(
						"dwgImport");
				addMap();
				Toast.makeText(context, "导入dwg格式文件成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "导入dwg格式文件失败", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加到地图
	 */
	private void addMap() {
		mLayer = mMapControl.getMap().getLayers().add(dataset, true);
		mLayer.setVisible(true);
		mLayer.setEditable(true);
		mMapControl.getMap().viewEntire();
		mMapControl.getMap().refresh();

	}

	/**
	 * 移除图层
	 */
	private void removeLayer() {
		mMapControl.getMap().close();  // 导入的数据集可能拥有不同的投影坐标系,且数据范围也差异较大,影像显示,因而直接关闭地图,再添加图层
//		mMapControl.getMap().getLayers().remove("dwgImport@World");
//		mMapControl.getMap().getLayers().remove("dxfImport@World");
//		mMapControl.getMap().getLayers().remove("tifImport@World");
//		mMapControl.getMap().getLayers().remove("mifImport@World");
//		mMapControl.getMap().getLayers().remove("shpImport@World");
	}

}
