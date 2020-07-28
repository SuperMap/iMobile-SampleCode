package com.supermap.onlinedemo;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DemoInterfaceActivity extends Activity implements OnClickListener {

	private Button btnNavigation;
	private Button btnPOIQuery;
	private Button btnGeocoding;
	private Button btnTrafficTransfer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_demo_interface);
		btnNavigation=(Button) findViewById(R.id.btnNavigation);
		btnPOIQuery=(Button) findViewById(R.id.btnPOIQuery);
		btnGeocoding=(Button) findViewById(R.id.btnGeocoding);
		btnTrafficTransfer=(Button) findViewById(R.id.btnTrafficTransfer);
		btnNavigation.setOnClickListener(this);
		btnPOIQuery.setOnClickListener(this);
		btnGeocoding.setOnClickListener(this);
		btnTrafficTransfer.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNavigation:
			Intent navigation=new Intent(this,NavigationOnlineActivity.class);
			startActivity(navigation);
			break;
			
		case R.id.btnGeocoding:
			Intent geocoding=new Intent(this,GeocodingActivity.class);
			startActivity(geocoding);
			break;
			
		case R.id.btnPOIQuery:
			Intent poiQuery=new Intent(this,POIQueryActivity.class);
			startActivity(poiQuery);
			break;
		case R.id.btnTrafficTransfer:
			Intent coordconvert=new Intent(this,TrafficTransferActivity.class);
			startActivity(coordconvert);
			break;
		default:
			break;
		}
	}

}
