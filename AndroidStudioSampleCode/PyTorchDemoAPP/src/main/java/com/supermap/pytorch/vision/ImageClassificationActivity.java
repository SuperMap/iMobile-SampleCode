package com.supermap.pytorch.vision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import com.supermap.pytorch.Constants;
import com.supermap.pytorch.ObjDialog;
import com.supermap.pytorch.R;
import com.supermap.pytorch.Utils;
import com.supermap.pytorch.vision.view.ResultRowView;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

public class ImageClassificationActivity extends org.pytorch.demo.vision.AbstractCameraXActivity<ImageClassificationActivity.AnalysisResult> {

  public static final String INTENT_MODULE_ASSET_NAME = "INTENT_MODULE_ASSET_NAME";
  public static final String INTENT_INFO_VIEW_TYPE = "INTENT_INFO_VIEW_TYPE";

  private static final int INPUT_TENSOR_WIDTH = 224;
  private static final int INPUT_TENSOR_HEIGHT = 224;
  private static final int TOP_K = 3;
  private static final int MOVING_AVG_PERIOD = 10;
  private static final String FORMAT_MS = "%dms";
  private static final String FORMAT_AVG_MS = "avg:%.0fms";

  private static final String FORMAT_FPS = "%.1fFPS";
  public static final String SCORES_FORMAT = "%.2f";
  //图片均值
  private static final float[] IMAGE_MEAN = {0.5460f, 0.5359f, 0.5124f};
  //图片标准差
  private static final float[] IMAGE_STD = {0.2147f, 0.2073f, 0.2090f};
  private Point2Ds m_Point2Ds;
  private Point2Ds m_CurrentPoints;

  static class AnalysisResult {

    private final String[] topNClassNames;
    private final float[] topNScores;
    private final long analysisDuration;
    private final long moduleForwardDuration;

    public AnalysisResult(String[] topNClassNames, float[] topNScores,
                          long moduleForwardDuration, long analysisDuration) {
      this.topNClassNames = topNClassNames;
      this.topNScores = topNScores;
      this.moduleForwardDuration = moduleForwardDuration;
      this.analysisDuration = analysisDuration;
    }
  }

  private boolean mAnalyzeImageErrorState;
  private ResultRowView[] mResultRowViews = new ResultRowView[TOP_K];
  private TextView mFpsText;
  private TextView mMsText;
  private TextView mMsAvgText;
  private Module mModule;
  private String mModuleAssetName;
  private FloatBuffer mInputTensorBuffer;
  private Tensor mInputTensor;
  private long mMovingAvgSum = 0;
  private Queue<Long> mMovingAvgQueue = new LinkedList<>();
  private MapView m_mapview;
  private MapControl m_mapControl;
  private Workspace m_workspace;
  private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
//  private ImageView m_commitBtn;
  private Bitmap m_currentBitmap;
  private Button m_backBtn;
  private Map<String,Bitmap> map;

  @Override
  protected int getContentViewLayoutId() {
    return R.layout.activity_image_classification;
  }

  @Override
  protected TextureView getCameraPreviewTextureView() {
    return ((ViewStub) findViewById(R.id.image_classification_texture_view_stub))
        .inflate()
        .findViewById(R.id.image_classification_texture_view);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // 初始化环境,设置许可路径
    Environment.setLicensePath(sdcard+"/SuperMap/license/");
    //在onCreate中调用初始化方法，否则组件功能不能正常
    Environment.initialization(this);
    super.onCreate(savedInstanceState);
    final ResultRowView headerResultRowView =
        findViewById(R.id.image_classification_result_header_row);
    headerResultRowView.nameTextView.setText(R.string.image_classification_results_header_row_name);
    headerResultRowView.scoreTextView.setText(R.string.image_classification_results_header_row_score);

    mResultRowViews[0] = findViewById(R.id.image_classification_top1_result_row);
    mResultRowViews[1] = findViewById(R.id.image_classification_top2_result_row);
    mResultRowViews[2] = findViewById(R.id.image_classification_top3_result_row);

    mFpsText = findViewById(R.id.image_classification_fps_text);
    mMsText = findViewById(R.id.image_classification_ms_text);
    mMsAvgText = findViewById(R.id.image_classification_ms_avg_text);
//    m_commitBtn = findViewById(R.id.buttonCommit);
    m_backBtn = findViewById(R.id.buttonBack);
    openMap();
  }

  private void openMap()
  {
    m_mapview = (MapView) findViewById(R.id.Map_view);
    m_mapControl = m_mapview.getMapControl();
    m_workspace = new Workspace();
    m_mapControl.getMap().setWorkspace(m_workspace);

    DatasourceConnectionInfo info = new DatasourceConnectionInfo();
    info.setEngineType(EngineType.GaoDeMaps);
    Datasource datasourcegoogle = m_workspace.getDatasources().open(info);

//    mMapControl.getMap().getLayers().add(datasourcegoogle.getDatasets().get(0),false);
    m_mapControl.getMap().getLayers().add(datasourcegoogle.getDatasets().get(0),false);
//    mMapControl.getMap().viewEntire();
    m_mapControl.getMap().refresh();
    m_mapControl.getMap().setCenter(new Point2D(11586847.363644,3570559.332552));
    m_mapControl.getMap().setScale(1.078229189257065E-4);
    m_Point2Ds = new Point2Ds();
    m_Point2Ds.add(new Point2D(11587325.095071,3570632.186594));
    m_Point2Ds.add(new Point2D(11586823.477073,3570629.797937));
    m_Point2Ds.add(new Point2D(11586823.477073,3570155.649496));
    m_Point2Ds.add(new Point2D(11586295.583847,3570143.706210));
    m_Point2Ds.add(new Point2D(11586290.806532,3570984.513522));
    m_Point2Ds.add(new Point2D(11586818.699759,3570989.290836));
    m_CurrentPoints = new Point2Ds();
    map = new HashMap<>();
  }

  public void tackPic(View view) {

    View dView = getWindow().getDecorView();
    dView.setDrawingCacheEnabled(true);
    dView.buildDrawingCache();
    Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
    bitmap = m_TextureView.getBitmap();
    if (bitmap != null) {
      commitToMap(bitmap);
    }
  }

  int i=0;
  private void commitToMap(Bitmap bitmap)
  {
//    addCallout(bitmap);
    final ResultRowView rowView = mResultRowViews[0];

    addCalloutCar(m_Point2Ds.getItem(i),rowView.nameTextView.getText().toString(), getDayTime(), getCurrentTime(),bitmap);
    m_CurrentPoints.add(m_Point2Ds.getItem(i));
    if (m_CurrentPoints.getCount()>1)
    {
      GeoLine geoLine = new GeoLine(m_CurrentPoints);
      GeoStyle geoStyle = new GeoStyle();
      geoStyle.setLineColor(new com.supermap.data.Color(230,255,0));
      geoStyle.setLineWidth(1.5);
      geoLine.setStyle(geoStyle);
      m_mapControl.getMap().getTrackingLayer().clear();
      m_mapControl.getMap().getTrackingLayer().add(geoLine,"path");
    }
    i++;
    if (i>5) i=0;
    m_mapControl.getMap().refresh();
    mMsAvgText.setVisibility(View.INVISIBLE);
    mFpsText.setVisibility(View.INVISIBLE);
    mMsText.setVisibility(View.INVISIBLE);
    m_mapview.setVisibility(View.VISIBLE);
    m_backBtn.setVisibility(View.VISIBLE);
  }

  private void addCallout(Bitmap bitmap)
  {
    m_currentBitmap = bitmap;
    Point2D point2D = m_mapControl.getMap().getCenter();
    CallOut callOut = new CallOut(this);
    callOut.setLocation(m_Point2Ds.getItem(i).getX(),m_Point2Ds.getItem(i).getY());
    callOut.setCustomize(true);
    LinearLayout relativeLayout = new LinearLayout(this);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 250);
    relativeLayout.setOrientation(LinearLayout.VERTICAL);
    relativeLayout.setLayoutParams(params);
    ImageView image = new ImageView(this);
    image.setImageBitmap(bitmap);
    image.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
    TextView textView = new TextView(this);
    final ResultRowView rowView = mResultRowViews[0];
    String result = rowView.nameTextView.getText().toString() + "\n" +rowView.scoreTextView.getText().toString();
    textView.setText(result);
    textView.setWidth(150);
    textView.setHeight(100);
    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    textView.setTextColor(Color.rgb(0,0,0));
    relativeLayout.addView(image);
    relativeLayout.addView(textView);

    callOut.setContentView(relativeLayout);
    String str_tmp = String.valueOf(i);
    map.put(str_tmp,bitmap);
    callOut.setTransitionName(str_tmp);
    callOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String strName = view.getTransitionName();
        Bitmap bitmap1 = map.get(strName);
//        m_commitBtn.setImageBitmap((bitmap1));
//        m_commitBtn.setVisibility(View.VISIBLE);
      }
    });
    m_mapview.addCallout(callOut,str_tmp);
    m_CurrentPoints.add(m_Point2Ds.getItem(i));
    if (m_CurrentPoints.getCount()>1)
    {
      GeoLine geoLine = new GeoLine(m_CurrentPoints);
      GeoStyle geoStyle = new GeoStyle();
      geoStyle.setLineColor(new com.supermap.data.Color(230,255,0));
      geoStyle.setLineWidth(1.5);
      geoLine.setStyle(geoStyle);
      m_mapControl.getMap().getTrackingLayer().clear();
      m_mapControl.getMap().getTrackingLayer().add(geoLine,"path");
    }
    i++;
    if (i>5) i=0;
  }

  public void hidImageView(View view)
  {
//    m_commitBtn.setVisibility(View.INVISIBLE);
  }

  public void back(View view)
  {
    m_mapview.setVisibility(View.INVISIBLE);
    m_backBtn.setVisibility(View.INVISIBLE);
    mMsAvgText.setVisibility(View.VISIBLE);
    mFpsText.setVisibility(View.VISIBLE);
    mMsText.setVisibility(View.VISIBLE);
//    m_commitBtn.setVisibility(View.INVISIBLE);
    System.out.println("scale" +m_mapControl.getMap().getScale());
  }


  @Override
  protected void applyToUiAnalyzeImageResult(AnalysisResult result) {
    mMovingAvgSum += result.moduleForwardDuration;
    mMovingAvgQueue.add(result.moduleForwardDuration);
    if (mMovingAvgQueue.size() > MOVING_AVG_PERIOD) {
      mMovingAvgSum -= mMovingAvgQueue.remove();
    }

    for (int i = 0; i < TOP_K; i++) {
      final ResultRowView rowView = mResultRowViews[i];
      rowView.nameTextView.setText(result.topNClassNames[i]);
      rowView.scoreTextView.setText(String.format(Locale.US, SCORES_FORMAT,
          result.topNScores[i]*100)+"%");
      rowView.setProgressState(false);
    }

    mMsText.setText(String.format(Locale.US, FORMAT_MS, result.moduleForwardDuration));
    if (mMsText.getVisibility() != View.VISIBLE) {
//      mMsText.setVisibility(View.VISIBLE);
    }
    mFpsText.setText(String.format(Locale.US, FORMAT_FPS, (1000.f / result.analysisDuration)));
    if (mFpsText.getVisibility() != View.VISIBLE) {
//      mFpsText.setVisibility(View.VISIBLE);
    }

    if (mMovingAvgQueue.size() == MOVING_AVG_PERIOD) {
      float avgMs = (float) mMovingAvgSum / MOVING_AVG_PERIOD;
      mMsAvgText.setText(String.format(Locale.US, FORMAT_AVG_MS, avgMs));
      if (mMsAvgText.getVisibility() != View.VISIBLE) {
//        mMsAvgText.setVisibility(View.VISIBLE);
      }
    }
  }

  protected String getModuleAssetName() {
    if (!TextUtils.isEmpty(mModuleAssetName)) {
      return mModuleAssetName;
    }
    final String moduleAssetNameFromIntent = getIntent().getStringExtra(INTENT_MODULE_ASSET_NAME);
    mModuleAssetName = !TextUtils.isEmpty(moduleAssetNameFromIntent)
        ? moduleAssetNameFromIntent
        : "model.pt";

    return mModuleAssetName;
  }

  @Override
  protected String getInfoViewAdditionalText() {
    return getModuleAssetName();
  }

  @Override
  @WorkerThread
  @Nullable
  protected AnalysisResult analyzeImage(ImageProxy image, int rotationDegrees) {
    if (mAnalyzeImageErrorState) {
      return null;
    }

    try {
      if (mModule == null) {
        final String moduleFileAbsoluteFilePath = new File(
            Utils.assetFilePath(this, getModuleAssetName())).getAbsolutePath();
        mModule = Module.load(moduleFileAbsoluteFilePath);

        mInputTensorBuffer =
            Tensor.allocateFloatBuffer(3 * INPUT_TENSOR_WIDTH * INPUT_TENSOR_HEIGHT);
        mInputTensor = Tensor.fromBlob(mInputTensorBuffer, new long[]{1, 3, INPUT_TENSOR_HEIGHT, INPUT_TENSOR_WIDTH});
      }

      final long startTime = SystemClock.elapsedRealtime();
      TensorImageUtils.imageYUV420CenterCropToFloatBuffer(
          image.getImage(), rotationDegrees,
          INPUT_TENSOR_WIDTH, INPUT_TENSOR_HEIGHT,
              IMAGE_MEAN,
              IMAGE_STD,
          mInputTensorBuffer, 0);

      final long moduleForwardStartTime = SystemClock.elapsedRealtime();
      final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();
      final long moduleForwardDuration = SystemClock.elapsedRealtime() - moduleForwardStartTime;

      float[] scores = outputTensor.getDataAsFloatArray();
      scores = softMax(scores);
      final int[] ixs = Utils.topK(scores, TOP_K);

      final String[] topKClassNames = new String[TOP_K];
      final float[] topKScores = new float[TOP_K];
      for (int i = 0; i < TOP_K; i++) {
        final int ix = ixs[i];
        topKClassNames[i] = Constants.IMAGENET_CLASSES[ix];
        topKScores[i] = scores[ix];
      }
      final long analysisDuration = SystemClock.elapsedRealtime() - startTime;
      return new AnalysisResult(topKClassNames, topKScores, moduleForwardDuration, analysisDuration);
    } catch (Exception e) {
      Log.e(Constants.TAG, "Error during image analysis", e);
      mAnalyzeImageErrorState = true;
      runOnUiThread(() -> {
        if (!isFinishing()) {
          showErrorDialog(v -> ImageClassificationActivity.this.finish());
        }
      });
      return null;
    }
  }

  private float[] softMax(float[] value)
  {
    for (int i=0;i<value.length;i++)
    {
      value[i] =(float)Math.pow(Math.E,value[i]);
    }

    float sum = value[0]+value[1]+value[2]+value[3]+value[4]+value[5]+value[6]+value[7]+value[8];
    for (int i=0;i<value.length;i++)
    {
      value[i] =value[i]/sum;
    }
    return value;
  }

  private void addCalloutCar(Point2D point2D, String name,  String day, String time,Bitmap bitmap) {
    LayoutInflater lfCallOut = getLayoutInflater();
    View calloutLayout = lfCallOut.inflate(R.layout.callout_car, null);
    TextView timeView = calloutLayout.findViewById(R.id.time);
    TextView dayView = calloutLayout.findViewById(R.id.day);
    ImageView imageView = calloutLayout.findViewById(R.id.img);

    String time1 = getCurrentTime();
    imageView.setImageBitmap(bitmap);
    timeView.setText(time1);

    imageView.setOnClickListener(v -> {
      ObjDialog dialog = new ObjDialog(this, R.style.SpeechDialog,
              bitmap, name, day, time1);
      dialog.show();
      dialog.setCanceledOnTouchOutside(true);
      dialog.setOnDismissListener(dialog1 -> {

      });
    });
    dayView.setText(day);

    CallOut callout = new CallOut(this);
    // 设置显示内容
    callout.setContentView(calloutLayout);
    // 设置自定义背景图片
    callout.setCustomize(true);
    // 设置显示位置
    callout.setLocation(point2D.getX(), point2D.getY());
    m_mapview.addCallout(callout, "Callout" + time+i);
  }

  private static String getDayTime() {
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对
        Date date = new Date(l);
        //转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        return dateFormat.format(date);

//    return "2019-10-11";
  }

  private static String getCurrentTime() {
    //得到long类型当前时间
    long l = System.currentTimeMillis();
    //new日期对
    Date date = new Date(l);
    //转换提日期输出格式
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分", Locale.CHINA);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_ms", Locale.CHINA);

    return dateFormat.format(date);
  }

  @Override
  protected int getInfoViewCode() {
    return getIntent().getIntExtra(INTENT_INFO_VIEW_TYPE, -1);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mModule != null) {
      mModule.destroy();
    }
  }
}
