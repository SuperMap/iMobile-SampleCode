package com.supermap.imobile.iportalservices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.supermap.imobile.bean.OpenRestMapEvent;
import com.supermap.imobile.fragment.MapViewFragment;
import com.supermap.imobile.fragment.RestMapsFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 查看REST地图和地图列表
 */
public class MapViewActivity extends AppCompatActivity {

    private static final String TAG = "MapViewActivity";
    private Toolbar toolbar = null;
    private MapViewFragment mMapviewFragment;
    private RestMapsFragment mRestMapsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_mapview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());
        initView();
    }

    private String restUrl;
    private void initView() {
        Intent intent = getIntent();
        restUrl = intent.getStringExtra("restUrl");

        showMapviewFragment();
        showRestMapsFragment();
    }

    private void showRestMapsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mRestMapsFragment == null) {
            mRestMapsFragment = new RestMapsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("restUrl", this.restUrl);
            mRestMapsFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.content_framelayout, mRestMapsFragment, "mRestMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mRestMapsFragment)
                .commit();
    }

    private void showMapviewFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMapviewFragment == null) {
            mMapviewFragment = new MapViewFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMapviewFragment, "mMapviewFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMapviewFragment)
                .commit();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mMapviewFragment != null) {
            fragmentTransaction.hide(mMapviewFragment);
        }
        if (mRestMapsFragment != null) {
            fragmentTransaction.hide(mRestMapsFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.maps) {
            showRestMapsFragment();
            return true;
        } else if (id == R.id.viewmap) {
            showMapviewFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenRestMapEvent(OpenRestMapEvent event) {
        if (!event.getMode().equals("MapViewFragment")) {
            return;
        }
        showMapviewFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
