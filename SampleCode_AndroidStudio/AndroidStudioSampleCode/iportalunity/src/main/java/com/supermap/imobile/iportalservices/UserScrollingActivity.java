package com.supermap.imobile.iportalservices;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.supermap.imobile.bean.LogoutEvent;
import com.supermap.imobile.bean.MyAccountBean;
import com.supermap.iportalservices.IPortalService;
import com.supermap.iportalservices.OnResponseListener;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserScrollingActivity extends AppCompatActivity implements OnResponseListener {

    private static final String TAG = "UserScrollingActivity";
    private MyAccountBean mMyAccountBean;

    private TextView mUserName = null;
    private TextView mNickName = null;
    private TextView mEmail = null;

    private RadioGroup mRadioGroup = null;
    private CardView mCVnickname;
    private CardView mCVpawword;
    private CardView mCVquestion;
    private CardView mCVemail;

    private EditText edt_nickname = null;
    private EditText edt_origin_password = null;
    private EditText edt_new_passowrd = null;
    private EditText edt_confirm_new_passowrd = null;

    private Spinner mSpinner = null;
    private EditText edt_security_answer = null;

    private EditText edt_email = null;

    private Button update = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "修改用户资料", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        initView();
    }

    private void initView() {
        mUserName = findViewById(R.id.tv_username);
        mNickName = findViewById(R.id.tv_nickname);
        mEmail = findViewById(R.id.tv_email);
        mRadioGroup = findViewById(R.id.radio_group);
        mCVnickname = findViewById(R.id.cardview_nickname);
        mCVpawword = findViewById(R.id.cardview_password);
        mCVquestion = findViewById(R.id.cardview_question);
        mCVemail = findViewById(R.id.cardview_email);

        edt_nickname = findViewById(R.id.edt_nickname);
        edt_origin_password = findViewById(R.id.origin_password);
        edt_new_passowrd = findViewById(R.id.new_passowrd);
        edt_confirm_new_passowrd = findViewById(R.id.confirm_new_passowrd);

        edt_email = findViewById(R.id.edt_email);

        mSpinner = findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edt_security_answer = findViewById(R.id.security_answer);

        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rb_nickname:
                        String trim = edt_nickname.getText().toString().trim();
                        if (trim == null || trim.isEmpty()) {
                            Toast.makeText(UserScrollingActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
                            @Override
                            public void onFailed(Exception exception) {
                                runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(Response response) {
                                JSONObject root;
                                try {
                                    String responseBody = response.body().string();
                                    root = new JSONObject(responseBody);
                                    if (root.has("succeed")) {
                                        boolean succeed = root.getBoolean("succeed");
                                        if (succeed) {
                                            runOnUiThread(() -> {
                                                mNickName.setText(trim);
                                                Toast.makeText(UserScrollingActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                            });
                                        } else {
                                            if (root.has("error")) {
                                                JSONObject error = new JSONObject(root.getString("error"));
                                                final String errorMsg = error.getString("errorMsg");
                                                final String code = error.getString("code");
                                                runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, "" + code + ": " + errorMsg, Toast.LENGTH_LONG).show());
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        IPortalService.getInstance().updateNickname(trim);
                        break;
                    case R.id.rb_password:
                        String originPassword = edt_origin_password.getText().toString().trim();
                        if (originPassword == null || originPassword.isEmpty()) {
                            Toast.makeText(UserScrollingActivity.this, "原密码不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String newPassword = edt_new_passowrd.getText().toString().trim();
                        if (newPassword == null || newPassword.isEmpty()) {
                            Toast.makeText(UserScrollingActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String confirmNewPassword = edt_confirm_new_passowrd.getText().toString().trim();
                        if (confirmNewPassword == null || confirmNewPassword.isEmpty()) {
                            Toast.makeText(UserScrollingActivity.this, "确认新密码不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!newPassword.equals(confirmNewPassword)) {
                            Toast.makeText(UserScrollingActivity.this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
                            @Override
                            public void onFailed(Exception exception) {
                                runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(Response response) {
                                JSONObject root;
                                try {
                                    String responseBody = response.body().string();
                                    if (response.code() != 200) {
                                        root = new JSONObject(responseBody);
                                        if (root.has("error")) {
                                            String error = root.getString("error");
                                            runOnUiThread(() -> {
                                                Toast.makeText(UserScrollingActivity.this, error, Toast.LENGTH_LONG).show();
                                            });
                                        }
                                        return;
                                    }

                                    root = new JSONObject(responseBody);
                                    if (root.has("succeed")) {
                                        boolean succeed = root.getBoolean("succeed");
                                        if (succeed) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(UserScrollingActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                            });
                                        } else {
                                            runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, "更新失败", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        IPortalService.getInstance().updatePassword(newPassword, originPassword);
                        break;
                    case R.id.rb_question:
                        String security_answer = edt_security_answer.getText().toString().trim();
                        if (security_answer == null || security_answer.isEmpty()) {
                            Toast.makeText(UserScrollingActivity.this, "答案不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
                            @Override
                            public void onFailed(Exception exception) {
                                runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(Response response) {
                                JSONObject root;
                                try {
                                    String responseBody = response.body().string();
                                    root = new JSONObject(responseBody);
                                    if (root.has("succeed")) {
                                        boolean succeed = root.getBoolean("succeed");
                                        if (succeed) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(UserScrollingActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                            });
                                        } else {
                                            runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, "更新失败", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        int selectedItemPosition = mSpinner.getSelectedItemPosition();
                        String pwdQuestion = "";
                        switch (selectedItemPosition) {
                            case 0:
                                pwdQuestion = "school";
                                break;
                            case 1:
                                pwdQuestion = "food";
                                break;
                            case 2:
                                pwdQuestion = "car";
                                break;
                            case 3:
                                pwdQuestion = "ball";
                                break;
                            case 4:
                                pwdQuestion = "mother";
                                break;
                            case 5:
                                pwdQuestion = "job";
                                break;
                            case 6:
                                pwdQuestion = "dream";
                                break;
                            case 7:
                                pwdQuestion = "love";
                                break;
                            case 8:
                                pwdQuestion = "home";
                                break;
                        }
                        IPortalService.getInstance().updateSecurityQuestion(pwdQuestion, security_answer);
                        break;
                    case R.id.rb_email:
                        String email = edt_email.getText().toString().trim();
                        if (email == null || email.isEmpty()) {
                            Toast.makeText(UserScrollingActivity.this, "邮箱地址不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
                            @Override
                            public void onFailed(Exception exception) {
                                runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(Response response) {
                                JSONObject root;
                                try {
                                    if (response.code() != 200) {
                                        runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, "请检查服务地址是否正确:\n" + response.code() + ": " + response.request().url(), Toast.LENGTH_LONG).show());
                                        return;
                                    }

                                    String responseBody = response.body().string();
                                    root = new JSONObject(responseBody);
                                    if (root.has("succeed")) {
                                        boolean succeed = root.getBoolean("succeed");
                                        if (succeed) {
                                            runOnUiThread(() -> {
                                                mEmail.setText(email);
                                                Toast.makeText(UserScrollingActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                            });

                                        } else {
                                            runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, "更新失败", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        IPortalService.getInstance().updateEmail(email);
                        break;
                }
            }
        });

        mRadioGroup.check(R.id.rb_nickname);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_nickname:
                        mCVnickname.setVisibility(View.VISIBLE);
                        mCVpawword.setVisibility(View.GONE);
                        mCVquestion.setVisibility(View.GONE);
                        mCVemail.setVisibility(View.GONE);
                        break;
                    case R.id.rb_password:
                        mCVnickname.setVisibility(View.GONE);
                        mCVpawword.setVisibility(View.VISIBLE);
                        mCVquestion.setVisibility(View.GONE);
                        mCVemail.setVisibility(View.GONE);
                        edt_origin_password.setText("");
                        edt_new_passowrd.setText("");
                        edt_confirm_new_passowrd.setText("");
                        break;
                    case R.id.rb_question:
                        mCVnickname.setVisibility(View.GONE);
                        mCVpawword.setVisibility(View.GONE);
                        mCVquestion.setVisibility(View.VISIBLE);
                        mCVemail.setVisibility(View.GONE);
                        edt_security_answer.setText("");
                        break;
                    case R.id.rb_email:
                        mCVnickname.setVisibility(View.GONE);
                        mCVpawword.setVisibility(View.GONE);
                        mCVquestion.setVisibility(View.GONE);
                        mCVemail.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        IPortalService.getInstance().addOnResponseListener(this);
        IPortalService.getInstance().getMyAccount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            logout();
            return true;
        } else if (id == R.id.update_account) {
            IPortalService.getInstance().addOnResponseListener(this);
            IPortalService.getInstance().getMyAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        IPortalService.getInstance().logout("http://" + IPortalService.getInstance().getIPortalServiceHost() + "/services");
        finish();
        EventBus.getDefault().post(new LogoutEvent.Builder().setMode("MainActivity").build());
        startActivity(new Intent(UserScrollingActivity.this, LoginActivity.class));
    }

    @Override
    public void onFailed(Exception exception) {
        runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onResponse(Response response) {
        try {
            if (response.code() != 200) {
                runOnUiThread(() -> Toast.makeText(UserScrollingActivity.this, "请检查服务地址是否正确:\n" + response.code() + ": " + response.request().url(), Toast.LENGTH_LONG).show());
                return;
            }

            String responseBody = response.body().string();
            Gson gson = new Gson();
            mMyAccountBean = gson.fromJson(responseBody, MyAccountBean.class);
            runOnUiThread(() -> {
                mUserName.setText(mMyAccountBean.getName());
                mNickName.setText(mMyAccountBean.getNickname());
                mEmail.setText(mMyAccountBean.getEmail());

                edt_nickname.setText(mMyAccountBean.getNickname());
                edt_nickname.setSelection(edt_nickname.getText().length());

                edt_email.setText(mMyAccountBean.getEmail());
                edt_email.setSelection(edt_email.getText().length());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
