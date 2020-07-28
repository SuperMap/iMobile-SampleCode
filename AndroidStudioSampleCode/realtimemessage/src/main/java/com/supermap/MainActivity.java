package com.supermap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.services.cooperation.ConnectServiceListener;
import com.supermap.services.cooperation.GroupMessageListener;
import com.supermap.services.cooperation.MessageService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText_message;
    MessageService messageService;
    final String MSG_IP = "";
    final int MSG_Port = 0;
    final String MSG_HostName = "";
    final String MSG_UserName = "";
    final String MSG_Password = "";
    final String MSG_ID="007";
    final String GROUP_ID="1001";
    private String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.setLicensePath("sdcard/SuperMap/License/");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        editText_message=findViewById(R.id.edit_message);
        initMessageService();
    }
    public void initMessageService(){
        messageService=MessageService.getInstance();
        messageService.setConnectServiceListener(new ConnectServiceListener() {
            @Override
            public void onFailed(String s) {
                showToast(s);
                Log.i(TAG, "Connect onFailed"+s);
            }

            @Override
            public void onSuccess(String s) {
                showToast(s);
                creatMessageGroup();
                Log.i(TAG, "Connect onSuccess"+s);
            }
        });
        messageService.connectService(MSG_UserName, MSG_Password, MSG_HostName, MSG_IP, MSG_Port,MSG_ID);

        messageService.setGroupMessageListener(new GroupMessageListener() {
            @Override
            public void onReceived(String groupId, String userId, long sendTime, String message) {
                showToast(groupId+","+userId+","+sendTime+","+message);
                Log.i(TAG, message);
            }

            @Override
            public void onError(String s) {
                showToast(s);
                Log.i(TAG, s);
            }
        });

    }
    public void sendmessage(View view){
        messageService.sendGroupMessage(GROUP_ID,editText_message.getText().toString());

    }
    public void creatMessageGroup(){
        List<String> list=new ArrayList<>();
        list.add(MSG_ID);
        messageService.creatGroup(GROUP_ID, list, new MessageService.ProcessResultListener() {
            @Override
            public void onComplete(String s) {
                showToast(s);
                MessageService.getInstance().receiveGroupMessage();//接受群组消息
                Log.i(TAG,"creatGroup success"+ s);
            }

            @Override
            public void onError(String s) {
                showToast(s);
                Log.i(TAG, "creatGroup fail"+s);
            }
        });
    }
    public void showToast(String meaage){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,meaage,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
