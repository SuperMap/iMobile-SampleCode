package com.supermap.imobile.iportalservices;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.supermap.iportalservices.IPortalService;
import com.supermap.iportalservices.OnResponseListener;
import com.supermap.imobile.bean.WebMapBean;
import okhttp3.Response;

import java.io.IOException;

/**
 * WebMap 资源是地图的内容资源。
 */
public class WebMapActivity extends AppCompatActivity implements OnResponseListener {

    private static final String TAG = "WebMapActivity";
    private int mapId = -1;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private WebMapBean webMapBean = null;

    private AutoCompleteTextView maptitle = null;
    private AutoCompleteTextView mapdescription = null;
    private AutoCompleteTextView mapversion = null;
    private AutoCompleteTextView mapleftBottom = null;
    private AutoCompleteTextView maprightTop = null;
    private AutoCompleteTextView maplevel = null;
    private AutoCompleteTextView mapcenter = null;
    private AutoCompleteTextView baselayer = null;
    private AutoCompleteTextView maplayers = null;
    private AutoCompleteTextView mapprojection = null;

    private Button update = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "自定义操作", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
        fab.setOnClickListener(v -> {
            switchMode();
        });

        initView();
    }

    private void initView() {
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        maptitle = findViewById(R.id.maptitle);
        mapdescription = findViewById(R.id.mapdescription);
        mapversion = findViewById(R.id.mapversion);
        maplevel = findViewById(R.id.maplevel);
        mapleftBottom = findViewById(R.id.mapleftBottom);
        maprightTop = findViewById(R.id.maprightTop);
        mapcenter = findViewById(R.id.mapcenter);
        baselayer = findViewById(R.id.baselayer);
        maplayers = findViewById(R.id.maplayers);
        mapprojection = findViewById(R.id.mapprojection);

        update = findViewById(R.id.update);
        update.setOnClickListener(v -> {
            IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
                @Override
                public void onFailed(Exception exception) {

                }

                @Override
                public void onResponse(Response response) {
                    try {
                        String string = response.body().string();
                        runOnUiThread(() -> {
                            Toast.makeText(WebMapActivity.this, string, Toast.LENGTH_SHORT).show();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            //更新WebMapBean
            webMapBean.setTitle(maptitle.getText().toString());
            webMapBean.setDescription(mapdescription.getText().toString());
            webMapBean.setVersion(mapversion.getText().toString());
            webMapBean.setLevel(Double.parseDouble(maplevel.getText().toString()));

            Gson gson = new Gson();
            String json = gson.toJson(webMapBean);
            Log.e("JSONREQUEST", json);
            Log.e("JSONREQUEST","-------------------------");
//            IPortalService.getInstance().updateWebMap(mapId, responseBody);
            Log.e("JSONREQUEST", responseBody);
            IPortalService.getInstance().updateWebMap(mapId, json);
        });

        Intent intent = getIntent();
        mapId = intent.getIntExtra("MapId", -1);
        String thumbnail = intent.getStringExtra("Thumbnail");
        Glide.with(this)
                .load(thumbnail)
                .centerCrop()
                .placeholder(R.drawable.banner)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        collapsingToolbarLayout.setBackground(resource);
                    }
                });

        if (mapId != -1) {
            IPortalService.getInstance().addOnResponseListener(this);
            IPortalService.getInstance().getWebMap(mapId);
        } else {
            Toast.makeText(this, "获取资源失败", Toast.LENGTH_SHORT).show();
        }
        switchMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            switchMode();
            return true;
        } else if (id == R.id.update) {
            IPortalService.getInstance().addOnResponseListener(this);
            IPortalService.getInstance().getWebMap(mapId);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFailed(Exception exception) {
        runOnUiThread(() -> Toast.makeText(WebMapActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show());
    }

    String responseBody = null;
    @Override
    public void onResponse(Response response) {
        try {
            if (response.code() != 200) {
                runOnUiThread(() -> Toast.makeText(WebMapActivity.this, "请检查服务地址是否正确:\n" + response.code() + ": " + response.request().url(), Toast.LENGTH_LONG).show());
                return;
            }
            responseBody = response.body().string();
            Gson gson = new Gson();
            webMapBean = gson.fromJson(responseBody, WebMapBean.class);
            runOnUiThread(() -> {
                maptitle.setText(webMapBean.getTitle());
                mapdescription.setText(webMapBean.getDescription());
                mapversion.setText(webMapBean.getVersion());

                mapleftBottom.setText(gson.toJson(webMapBean.getExtent().getLeftBottom()));
                maprightTop.setText(gson.toJson(webMapBean.getExtent().getRightTop()));

                maplevel.setText(String.valueOf(webMapBean.getLevel()));
                mapcenter.setText(gson.toJson(webMapBean.getCenter()));

                baselayer.setText(gson.toJson(webMapBean.getBaseLayer()));
                maplayers.setText(gson.toJson(webMapBean.getLayers()));
                mapprojection.setText(webMapBean.getProjection());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchMode() {
        boolean enable = false;
        if (!maptitle.isEnabled()) {
            enable = true;
            update.setVisibility(View.VISIBLE);
        } else {
            update.setVisibility(View.GONE);
        }

        maptitle.setEnabled(enable);
        mapdescription.setEnabled(enable);
        mapversion.setEnabled(enable);
        mapleftBottom.setEnabled(enable);
        maprightTop.setEnabled(enable);
        maplevel.setEnabled(enable);
        mapcenter.setEnabled(enable);
        baselayer.setEnabled(enable);
        maplayers.setEnabled(enable);
        mapprojection.setEnabled(enable);
    }
}
