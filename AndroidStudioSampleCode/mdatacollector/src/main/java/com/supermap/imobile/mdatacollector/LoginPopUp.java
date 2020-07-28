package com.supermap.imobile.mdatacollector;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

public class LoginPopUp extends PopupWindow{

	private View    m_viewContent;
	private MyApplication m_App;
	private EditText m_editUserName;
	private EditText m_editUserPassword;
	private EditText m_editiPortalAddr;
	private EditText m_editiServerAddr;
	private String UserName;
	private String UserPassword;
	private String IPiPortal ;
	private String IPiServer ;
	private String iPortalUrl;
	private NetworkAccess m_NetworkAccess;
	private Button btn_confirm;
	
	public LoginPopUp(Context context, NetworkAccess networkAccess) {
		m_App = MyApplication.getInstance();
		m_NetworkAccess = networkAccess;
		initView(context);
	}
	
	/**
	 * 初始化登录界面
	 * @param context   上下文对象，用于创建view
	 */
	private void initView(Context context) {
		m_viewContent = LayoutInflater.from(context).inflate(R.layout.loginview, null);
		setContentView(m_viewContent);
		m_editUserName = (EditText) m_viewContent.findViewById(R.id.userName);
		m_editUserPassword = (EditText) m_viewContent.findViewById(R.id.userPassword);
		m_editiPortalAddr  =(EditText) m_viewContent.findViewById(R.id.iPortalAddr);
		m_editiServerAddr  = (EditText) m_viewContent.findViewById(R.id.iServerAddr);
		btn_confirm = ((Button) m_viewContent.findViewById(R.id.btn_confirm));
		
		((Button) m_viewContent.findViewById(R.id.btn_confirm)).setOnClickListener(listener);
		((Button) m_viewContent.findViewById(R.id.btn_cancel)).setOnClickListener(listener);
		
	}
	
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_confirm:
		        login();
				break;
			case R.id.btn_cancel:
				dismiss();
				break;
			default:
			    break;
			}
		}
	};
		
	/**
	 * 登录函数
	 */
    private void login() {
    	UserName  = m_editUserName.getText().toString();
    	UserPassword = m_editUserPassword.getText().toString();
    	IPiPortal = m_editiPortalAddr.getText().toString();
    	IPiServer = m_editiServerAddr.getText().toString();
    	 
    	if(UserName.length() ==0 || UserPassword.length() < 6 || IPiPortal.length() < 6 || IPiServer.length() <6){
    		m_App.showInfo("登录信息有误，请重新输入");
		} else {
			iPortalUrl = "http://" + IPiPortal + ":8090/iportal/";

			m_NetworkAccess.setiServerIpAddr(IPiServer);
			m_NetworkAccess.login(iPortalUrl, UserName, UserPassword);
			setBtnEnable(false);
		}
    	
    }

	public String getIPiPortal() {
		return IPiPortal;
	}
	
	public String getIPiServer() {
		return IPiServer;
	}
    
   /**
    * 显示登录界面
    */
    public void show() {
    	this.setFocusable(true);
    	show(480, 360, 0, 0);
    }
    
    private void show(int width, int height, int offsetX, int offsetY) {
    	setWidth(LayoutParams.WRAP_CONTENT);
    	setHeight(LayoutParams.WRAP_CONTENT);
    	setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    	showAtLocation(m_viewContent, Gravity.CENTER, 0, 0);
    }
    
    /**
     * 关闭登录界面
     */
    public void dismiss() {
    	super.dismiss();
    	setBtnEnable(true);
    }
    
    public void setBtnEnable(boolean enabled){
    	btn_confirm.setEnabled(enabled);
    	((Button) m_viewContent.findViewById(R.id.btn_cancel)).setEnabled(enabled);
    }
}
