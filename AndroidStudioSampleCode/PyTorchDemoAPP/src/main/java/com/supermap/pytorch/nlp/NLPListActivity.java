package com.supermap.pytorch.nlp;

import android.content.Intent;
import android.os.Bundle;

import com.supermap.pytorch.AbstractListActivity;
import com.supermap.pytorch.R;
import com.supermap.pytorch.vision.ImageClassificationActivity;
import com.supermap.pytorch.vision.VisionListActivity;

public class NLPListActivity extends AbstractListActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    findViewById(R.id.nlp_card_lstm_click_area).setOnClickListener(v -> {
      final Intent intent = new Intent(NLPListActivity.this, TextClassificationActivity.class);
      startActivity(intent);
    });
  }

  @Override
  protected int getListContentLayoutRes() {
    return R.layout.nlp_list_content;
  }
}
