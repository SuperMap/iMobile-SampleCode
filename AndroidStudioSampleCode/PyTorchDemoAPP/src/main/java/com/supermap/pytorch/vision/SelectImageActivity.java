package com.supermap.pytorch.vision;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import com.supermap.pytorch.Constants;
import com.supermap.pytorch.R;
import com.supermap.pytorch.Utils;
import com.supermap.pytorch.vision.view.ResultRowView;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;
import androidx.core.app.ActivityCompat;

public class SelectImageActivity extends Activity {

  public static final String INTENT_MODULE_ASSET_NAME = "nineClass.pt";
  public static final String INTENT_INFO_VIEW_TYPE = "2";

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
  private Button btnselect;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_classification);
    initPermissions();
    final ResultRowView headerResultRowView =
        findViewById(R.id.image_classification_result_header_row);
    headerResultRowView.nameTextView.setText(R.string.image_classification_results_header_row_name);
    headerResultRowView.scoreTextView.setText(R.string.image_classification_results_header_row_score);

    mResultRowViews[0] = findViewById(R.id.image_classification_top1_result_row);
    mResultRowViews[1] = findViewById(R.id.image_classification_top2_result_row);
    mResultRowViews[2] = findViewById(R.id.image_classification_top3_result_row);

    btnselect = findViewById(R.id.button);
    btnselect.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
//        getFilesAllName("");
      }
    });
    Bitmap bitmap = null;
    mModule = null;
    try {
      // creating bitmap from packaged into app android asset 'image.jpg',
      // app/src/main/assets/image.jpg
      // loading serialized torchscript module from packaged into app android asset model.pt,
      // app/src/model/assets/model.pt
      mModule = Module.load(assetFilePath(this, "nineClass.pt"));
    } catch (IOException e) {
      Log.e("PytorchHelloWorld", "Error reading assets", e);
      finish();
    }
  }

  /**
   * Copies specified asset to the file in /files app directory and returns this file absolute path.
   *
   * @return absolute file path
   */
  public static String assetFilePath(Context context, String assetName) throws IOException {
    File file = new File(context.getFilesDir(), assetName);
    if (file.exists() && file.length() > 0) {
      return file.getAbsolutePath();
    }

    try (InputStream is = context.getAssets().open(assetName)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
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

  private static final int PERMISSION_REQUEST = 1001;
  String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE,Manifest.permission.READ_EXTERNAL_STORAGE};
  List<String> permissionsList = new ArrayList<>();
  /**
   * 请求权限
   */
  private void initPermissions() {
    permissionsList.clear();

    //判断哪些权限未授予
    for(String permission : permissions){
      if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
        permissionsList.add(permission);
      }
    }

    //请求权限
    if(!permissionsList.isEmpty()){
      String[] permissions = permissionsList.toArray(new String[permissionsList.size()]);//将List转为数组
      ActivityCompat.requestPermissions(SelectImageActivity.this, permissions, PERMISSION_REQUEST);
    }
  }

  /**
   * 权限回调,
   * @param requestCode
   * @param permissions
   * @param grantResults
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode){
      case PERMISSION_REQUEST:
        break;
      default:
        break;
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mModule != null) {
      mModule.destroy();
    }
  }

  private static final int RESULT_LOAD_IMAGE = 1;
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
      Uri selectedImage = data.getData();
      String[] filePathColumn = {MediaStore.Images.Media.DATA};

      Cursor cursor = getContentResolver().query(selectedImage,
              filePathColumn, null, null, null);
      cursor.moveToFirst();

      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      String picturePath = cursor.getString(columnIndex);
      cursor.close();
      final Bitmap originImage = BitmapFactory.decodeFile(picturePath);
      Bitmap bitmap = Bitmap.createScaledBitmap(originImage, INPUT_TENSOR_WIDTH, INPUT_TENSOR_HEIGHT, false);
      ImageView imageView = findViewById(R.id.imageResult);
      imageView.setImageBitmap(bitmap);
      bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_TENSOR_WIDTH, INPUT_TENSOR_HEIGHT, false);
      mInputTensor  = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
              IMAGE_MEAN, IMAGE_STD);
//    mInputTensorBuffer =
//            Tensor.allocateFloatBuffer(3 * INPUT_TENSOR_WIDTH * INPUT_TENSOR_HEIGHT);
//    mInputTensor = Tensor.fromBlob(mInputTensorBuffer, new long[]{1, 3, INPUT_TENSOR_HEIGHT, INPUT_TENSOR_WIDTH});

      // running the model
      final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();

      // getting tensor content as java array of floats
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

      for (int i = 0; i < TOP_K; i++) {
        final ResultRowView rowView = mResultRowViews[i];
        rowView.nameTextView.setText(topKClassNames[i]);
        rowView.scoreTextView.setText(String.format(Locale.US, SCORES_FORMAT,
                topKScores[i]*100)+"%");
        rowView.setProgressState(false);
      }
    }
  }
}
