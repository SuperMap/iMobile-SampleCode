package com.supermap.imobile.mdatacollector;

import android.app.Activity;
import android.content.Context;

import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Rectangle2D;
import com.supermap.mapping.Map;
import com.supermap.mdatacollector.MDataCollector;
import com.supermap.mdatacollector.NetworkAccessMediaFileListener;
import com.supermap.plugin.LocationManagePlugin;
import com.supermap.services.DataDownloadService;
import com.supermap.services.DataUploadService;
import com.supermap.services.FeatureSet;
import com.supermap.services.ResponseCallback;

public class NetworkAccess {

	private MDataCollector m_MDataCollector;
	private Datasource m_Datasource;
	private MyApplication m_App;
	private Activity m_Activity;
	private DataDownloadService m_DownloadService;
	private DataUploadService m_UploadService;
	private Map m_Map;
	private Rectangle2D regionBounds;
	private LoginPopUp m_LoginPopUp;
	
	private String iServerUrl = "";                   // iServer服务地址，如:"http://192.168.0.103:8090"; 初始值设为"",以便正常创建对象
	private String dvName = "media";                  // 存储多媒体文件信息的点数据集的名称
	private String serviceName = "data-media";       // iServer中的服务名
	private String datasourceName = "media";          // iServer上发布的数据源名
	private String urlDataset;                        // iServer上Dataset的完整地址
	private String iPortalUrl;
	private String UserName;
	private String UserPassword;
	
	private Context m_context;
	private boolean isLogined = false;
	private boolean isLogining = false;
	private boolean isDownloading = false;
	private boolean isUploaded = false;
	private boolean isExisted  = false;
	
	public NetworkAccess(Context context, Datasource datasource, Map map){
		m_Activity  = (Activity) context;
		m_context = context;
		m_App = MyApplication.getInstance();
		m_Datasource = datasource;
		m_Map = map;
		
		m_DownloadService = new DataDownloadService(iServerUrl);
		m_UploadService = new DataUploadService(iServerUrl);
		
		m_DownloadService.setResponseCallback(serviceCallback);
		m_UploadService.setResponseCallback(serviceCallback);
		
//		initMDataCollector();
		
	}

	public void initMDataCollector() {
		if(m_MDataCollector == null) {
			m_MDataCollector = new MDataCollector(m_context);
		    m_MDataCollector.openGPS();

		    try {
		    	//8C版本setMediaDataset因为数据集里增加了一个字段处理，所以这里做异常捕捉处理，且需要删掉本地数据集（media）
		    	isExisted = m_MDataCollector.setMediaDataset(m_Datasource, dvName);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		    m_MDataCollector.setLocalFilePath("SuperMap/MediaTemp/");
		    m_MDataCollector.addNetworkAccessMediaFileListener(mediaFileListener);
		}
	}
	
	/**
	 * 下载多媒体数据
	 */
	private void download(){
		if(isLogining){
			changeNetworkStatus(false, "正在更新数据集......");
		}else {
			isDownloading = true;
		}
		if(isExisted){
			m_DownloadService.updateDataset(urlDataset,(DatasetVector) m_Datasource.getDatasets().get(dvName));
		}else{
			m_DownloadService.downloadDataset(urlDataset, m_Datasource);
		}
	}

	private void downloadMediaData() {
		// 更新本地的数据集
		if (m_MDataCollector != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					m_MDataCollector.downloadMediaFiles(regionBounds);
				}
			}).start();
		}
	}

	
	/**
	 * 上传数据集和多媒体数据
	 */
	private void upload(){
		isLogining = false;
		// 上传数据集
		if(iServerUrl != null)
		    m_UploadService.commitDataset(urlDataset, (DatasetVector)m_MDataCollector.getMediaDataset());
		
		// 上传多媒体数据
		if (m_MDataCollector != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					m_MDataCollector.uploadMediaFiles(regionBounds);
				}
			}).start();
		}
	}
	
	/**
	 * 登录iPortal
	 * @param url            iPortal的登录地址
	 * @param UserName       iPortal用户名 
	 * @param UserPassword   iPortal用户密码
	 */
	public void login(final String url, final String userName, final String userPassword){
		iPortalUrl = url;
		UserName = userName;
		UserPassword = userPassword;
		if(!isExisted)
			
		   initMDataCollector();
			
		// 登录iPortal
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(UserName == null || UserPassword == null || iPortalUrl == null){
		    	}else {
		    		try{
						isLogined = m_MDataCollector.login(iPortalUrl, UserName, UserPassword);
						showLoginStatus("");
		    		}catch(IllegalStateException e){
		    			e.printStackTrace();
		    			showLoginStatus(": 用户名或密码错误");
		    		}
		    	}
			}

		}).start();
	}

	
	private void showLoginStatus(final String msg) {
		m_Activity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(isLogined){
					m_LoginPopUp.dismiss();
					m_App.showInfo("登录成功");
					isLogining = true;
				    download();
				}else{
					m_App.showInfo("登录失败" + msg);
					m_LoginPopUp.setBtnEnable(true);
				}
			}
		});
	}
	
	public void setLoginPopUp(LoginPopUp loginPopUp){
		m_LoginPopUp = loginPopUp;
	}
	
	public boolean isLogined(){
		return isLogined;
	}
	
	public boolean isMediaDatasetExisted(){
		return isExisted;
	}
	
	private void showInfo(final String msg){
		m_Activity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				m_App.showInfo(msg);
			}
		});
	}
	
	/**
	 * 设置iServer服务的ip
	 * @param strIP      iServer服务的ip字符串
	 */
	public void setiServerIpAddr (String strIP){
		iServerUrl = "http://" + strIP + ":8090";
		urlDataset = iServerUrl +"/iserver/services/" + serviceName + "/rest/data/datasources/" + datasourceName +"/datasets/" + dvName;
		m_DownloadService.setUrl(iServerUrl);
		m_UploadService.setUrl(iServerUrl);
	} 
	
	public MDataCollector getMDataCollector(){
		return m_MDataCollector;
	}
	
	private ResponseCallback serviceCallback = new ResponseCallback(){

		@Override
		public void dataServiceFinished(String arg0) {
			// TODO Auto-generated method stub
			
			if(isLogining){
				
				initMDataCollector();
				// 初次下载数据集时需要初始化m_MDataCollector
				showInfo("更新数据集完成");
				changeNetworkStatus(true, "");
				isLogining = false;
			}else if(isDownloading){
				downloadMediaData();
				isDownloading = false;
			}else {
				showUploadInfo();
			}
		}

		@Override
		public void receiveResponse(FeatureSet arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void requestFailed(String arg0) {
			// TODO Auto-generated method stub
			
			if(isLogining){
				isLogining = false;
				showInfo("更新数据集失败:\n" + arg0 );
				changeNetworkStatus(true, "");
			}else if(isDownloading){
				isDownloading = false;
				showInfo("更新数据集失败:\n" + arg0 );
				changeNetworkStatus(true, "");
			}else {
				if (!str_uploadInfo.equals("")){
					str_uploadInfo = "上传数据集失败 :\n" + arg0 + "\n" + str_uploadInfo;
				}else{
					str_uploadInfo = "上传数据集失败 :\n" + arg0;
				}
				showUploadInfo();
			}
		}

		@Override
		public void requestSuccess() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addFeatureSuccess(int arg0) {
		}
		
	};
	
	private NetworkAccessMediaFileListener mediaFileListener = new NetworkAccessMediaFileListener(){

		@Override
		public boolean downloadMediaFile(String mediaFileName) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean uploadMediaFile(String mediaFileName) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onDownloadMediaFiles(String downloadInfo) {
			// TODO Auto-generated method stub
			showInfo("下载完成" + "\n" + downloadInfo);
			changeNetworkStatus(true, "");
		}

		@Override
		public void onUploadMediaFiles(String uploadInfo) {
			// TODO Auto-generated method stub
			if (!str_uploadInfo.equals("")){
				str_uploadInfo = uploadInfo + "\n" + str_uploadInfo;
			}else{
				str_uploadInfo = uploadInfo;
			}
			showUploadInfo();
		}
	};
	
	private String str_uploadInfo = "";
    private void showUploadInfo() {
		if (isUploaded) {
			showInfo("上传完成" + "\n" + str_uploadInfo);
			changeNetworkStatus(true, "");
			isUploaded = false;
			str_uploadInfo = "";
		} else {
			isUploaded = true;
		}
	}
    
	private void changeNetworkStatus(final boolean enabled, final String info){
		m_Activity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				((MainActivity)m_Activity).setNetworkStatus(enabled, info);
			}
		});
	}
	
	public void netWorkTransfering(char transmission, Rectangle2D bounds){
		regionBounds = bounds;
		bounds = m_Map.getBounds();
		if(regionBounds == null){
			showInfo("绘制区域为空");
		}
		if(1 == transmission){
			download();
			changeNetworkStatus(false, "正在下载中......");
		}else if(2 == transmission){
			upload();
			changeNetworkStatus(false, "正在上传中......");
		}
	}
}
