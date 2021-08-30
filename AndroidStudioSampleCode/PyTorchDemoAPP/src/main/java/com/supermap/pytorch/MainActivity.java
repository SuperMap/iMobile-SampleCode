package com.supermap.pytorch;

import android.content.Intent;
import android.os.Bundle;

import com.supermap.pytorch.nlp.NLPListActivity;
import com.supermap.pytorch.vision.VisionListActivity;

import androidx.appcompat.app.AppCompatActivity;
/**
 * <p>
 * Title:pytorch分类识别demo
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
 * 1、范例简介：pytorch框架分类识别，可选择图片识别或摄像头实时势必，并将识别结果保存到地图实时显示
 *
 * 2、使用步骤：
 * （1）点击选择图片识别，进入选择图片识别Activity
 * （2）点击摄像头实时识别，进入摄像头识别Activity
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.main_vision_click_view).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, VisionListActivity.class)));
    findViewById(R.id.main_nlp_click_view).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NLPListActivity.class)));
  }
}
