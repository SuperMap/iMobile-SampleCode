package com.supermap.onlinedemo;


import android.app.Activity;
import android.os.Bundle;


public class LoginActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
//		TextView tvHidden=(TextView) findViewById(R.id.tvHidden);
//		tvHidden.requestFocus();
		
//		final EditText etName=(EditText) findViewById(R.id.etName);
//		final EditText etPassword=(EditText) findViewById(R.id.etPassword);
//		final OnlineService login=new OnlineService(this);
//		Button btnLogin=(Button)findViewById(R.id.btnLogin);
//		btnLogin.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				String name=etName.getText().toString();
//				String password=etPassword.getText().toString();
//				login.login(name, password);
//				login.setLoginCallback(new LoginCallback() {
//					
//					@Override
//					public void loginSuccess() {
//						// TODO Auto-generated method stub
//						Intent intent=new Intent(LoginActivity.this, DemoInterfaceActivity.class);
//						startActivity(intent);
//					}
//					
//					@Override
//					public void loginFailed(String errInfo) {
//						// TODO Auto-generated method stub
//						Log.e("MainActity", errInfo);
//					}
//				});
//			}
//		});
		
	}

}
