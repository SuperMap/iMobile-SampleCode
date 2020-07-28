package com.supermap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


import com.supermap.onlineservices.OnlineCallBack;
import com.supermap.onlineservices.OnlineService;
import com.supermap.onlineservices.utils.GroupRole;
import com.supermap.supermap.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 创建群组
 */
public class CreateGroupActivity extends AppCompatActivity {

    private static final String TAG = CreateGroupActivity.class.getSimpleName();

    private EditText edt_group_name;
    private EditText edt_group_tags;
    private EditText edt_group_desc;

    private RadioGroup rg_resource;


    private RadioGroup rg_group_type;
    private CheckBox cb_check;

    private RelativeLayout rl_progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initView();
    }

    private void initView() {
        rl_progress = findViewById(R.id.rl_progress);

        edt_group_name = findViewById(R.id.edt_group_name);
        edt_group_tags = findViewById(R.id.edt_group_tags);
        edt_group_desc = findViewById(R.id.edt_group_desc);
        rg_resource = findViewById(R.id.rg_resource);
        rg_group_type = findViewById(R.id.rg_group_type);
        cb_check = findViewById(R.id.cb_check);

        findViewById(R.id.cancel_create).setOnClickListener(v -> finish());
        findViewById(R.id.create_group).setOnClickListener(v -> {
            runOnUiThread(() -> rl_progress.setVisibility(View.VISIBLE));
            create();
        });

        rg_resource.check(R.id.rb_members);
        rg_group_type.check(R.id.rb_public);

        rg_group_type.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_private) {
                cb_check.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_public) {
                cb_check.setVisibility(View.VISIBLE);
            }
        });
    }

    private void create() {
        String groupName = edt_group_name.getText().toString().trim();
        String groupTags = edt_group_tags.getText().toString().trim();
        String groupDesc = edt_group_desc.getText().toString().trim();

        int checkedRadioButtonId = rg_resource.getCheckedRadioButtonId();
        GroupRole groupRole = null;
        if (checkedRadioButtonId == R.id.rb_creator) {
            groupRole = GroupRole.CREATOR;
        } else if (checkedRadioButtonId == R.id.rb_members) {
            groupRole = GroupRole.MEMBER;
        }

        int checkedRadioButtonId1 = rg_group_type.getCheckedRadioButtonId();
        boolean isPublic = false;
        boolean isNeedCheck = false;
        if (checkedRadioButtonId1 == R.id.rb_private) {
            isPublic = false;
        } else if (checkedRadioButtonId1 == R.id.rb_public) {
            isPublic = true;
            isNeedCheck = cb_check.isChecked();
        }

        if (groupName == null || groupName.isEmpty()) {
            runOnUiThread(() -> {
                rl_progress.setVisibility(View.GONE);
            });
            return;
        }
        if (groupTags == null || groupTags.isEmpty()) {

            runOnUiThread(() -> {
                rl_progress.setVisibility(View.GONE);
            });
            return;
        }

        OnlineService.createGroup(
                groupName,
                groupTags,
                isPublic,
                groupDesc,
                groupRole,
                isNeedCheck,
                new OnlineCallBack.CallBackString() {
                    @Override
                    public void onSucceed(String response) {
                        Log.d(TAG, "createGroup: " + response);

                        finish();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        Log.e(TAG, "createGroup-errorInfo: " + errorInfo);
                        try {
                            JSONObject errorjson=new JSONObject(errorInfo);
                            String errotMsg = errorjson.getJSONObject("error").getString("errorMsg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {

                            rl_progress.setVisibility(View.GONE);
                        });
                    }
                });

    }



}
