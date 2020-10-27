package com.supermap.basedemo.appconfig;

import java.io.File;
import java.util.ArrayList;

import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;

public class DataManager {

	private String mWorkspaceServer = "";
	
	private Datasets mOpenedDatasets = null;
	
	private String mDisplayMapName = "";
	
	protected Workspace mWorkspace = null;
	private int indexOfServer = -1;
	private boolean isDataOpen = false;
	private ArrayList <String> mWorkspaceServerList = null;
	private ArrayList <Workspace> mWorkspaceList = null;
	/**
	 * 构造函数
	 */
	public DataManager() {
		mWorkspace = new Workspace();
		mWorkspaceServerList = new ArrayList<String> ();
		mWorkspaceList = new ArrayList<Workspace> ();
	}
	
	/**
	 * 获取工作空间
	 * @return
	 */
	public Workspace getWorkspace(){
		if(isUDBOpen)
			return mUDBWorkspace;
		if(indexOfServer>-1 )
			mWorkspace = mWorkspaceList.get(indexOfServer);
		return mWorkspace;
	}
	
	Workspace mUDBWorkspace = null;
	boolean isUDBOpen = false;
	/**
	 * 打开数据源
	 * @param udb
	 * @return
	 */
	public boolean openUDB(String udb){
		Datasource  ds = null;
		boolean hasOpened = false;
		if(mUDBWorkspace == null)
			mUDBWorkspace = new Workspace();
		
		//先看看有没有再里边
		for(int i=mUDBWorkspace.getDatasources().getCount()-1;i>=0;i--){
			ds  = mUDBWorkspace.getDatasources().get(i);
			if(ds.getConnectionInfo().getServer().equals(udb)){
				hasOpened = true;
				break;
			}
		}
		if(!hasOpened){
			DatasourceConnectionInfo dsInfo = new DatasourceConnectionInfo();
			dsInfo.setServer(udb);
			String alias = udb.replace(".udb", "")+"_temp";
			dsInfo.setAlias(alias);
			dsInfo.setEngineType(EngineType.UDB);
			ds = mUDBWorkspace.getDatasources().open(dsInfo);
		}
		if(ds==null){
			isUDBOpen = false;
			return false;
		}
		mOpenedDatasets = ds.getDatasets();
		isUDBOpen = true;
		return true;
	}
	
	/**
	 * 获取数据集集合
	 * @return
	 */
	public Datasets getOpenedDatasets(){
		return mOpenedDatasets;
	}

	/**
	 * 获取工作空间的路径
	 * @return
	 */
	public String getWorkspaceServer() {
		return mWorkspaceServer;
	}

	/**
	 * 设置工作空间的路径
	 * @param WorkspaceServer
	 */
	public void setWorkspaceServer(String WorkspaceServer) {
		if(!mWorkspaceServer.equals(WorkspaceServer)){
			this.mWorkspaceServer = WorkspaceServer;
			isDataOpen = false;
		}
	}

	/**
	 * 获取当前打开的工作空间的名称
	 * @return
	 */
	public String getDisplayMapName() {
		if(isDataOpen){
			return mDisplayMapName;
		}
		return "Workspace unOpen";
	}

	/**
	 * 设置打开的工作空间的名称
	 * @param DisplayMapName
	 */
	public void setDisplayMapName(String DisplayMapName) {
		if(isDataOpen){
			this.mDisplayMapName = DisplayMapName;
		}
	}
	
	/**
	 * 打开工作空间
	 * @return
	 */
	public boolean open(){
		isUDBOpen = false;
		indexOfServer = mWorkspaceServerList.indexOf(mWorkspaceServer);
		if(indexOfServer>-1)
			return true;
		if(isDataOpen){
			return true;
		}
		File wksFile = new File(mWorkspaceServer);
		if(!wksFile.exists()){
			return false;
		}
		WorkspaceType type = null;
		if(mWorkspaceServer.endsWith(".SMWU")||mWorkspaceServer.endsWith(".smwu"))
		{
			type = WorkspaceType.SMWU;
		}else if(mWorkspaceServer.endsWith(".SXWU")||mWorkspaceServer.endsWith(".sxwu"))
		{
			type = WorkspaceType.SXWU;
		}
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(mWorkspaceServer);
		info.setType(type);
		mWorkspace.close();
		isDataOpen = mWorkspace.open(info);
		if(isDataOpen){
			if(getMapCount()>=1){
				setDisplayMapName(getMapName(0));
			}
		}
		info.dispose();
		info = null;
		return isDataOpen;
	}
	
	/**
	 * 检测工作空间是否打开
	 * @return
	 */
	public boolean isDataOpen()
	{
		return isDataOpen;
	}
	
	public void close(){
		mWorkspace.close();
		isDataOpen = false;
	}
	
	/**
	 * 获取数据源数目
	 * @return
	 */
	public int getDatasourceCount(){
		if(isDataOpen){
			return mWorkspace.getDatasources().getCount();
		}
		return 0;
	}
	
	/**
	 * 获取指定序号的数据源
	 * @param index
	 * @return
	 */
	public Datasource getDatasource(int index){
		if(isDataOpen){
			return mWorkspace.getDatasources().get(index);
		}
		return null;
	}
	
	/**
	 * 获取指定名称的数据源
	 * @param name
	 * @return
	 */
	public Datasource getDatasource(String name){
		if(isDataOpen){
			return mWorkspace.getDatasources().get(name);
		}
		return null;
	}
	
	/**
	 * 获取地图数量
	 * @return
	 */
	public int getMapCount(){
		if(isDataOpen){
			return mWorkspace.getMaps().getCount();
		}
		return 0;
	}
	
	/**
	 * 获取指定序号的名称
	 * @param index
	 * @return
	 */
	public String getMapName(int index){
		if(isDataOpen){
			return mWorkspace.getMaps().get(index);
		}
		return null;
	}

	/**
	 * 初始化工作空间列表
	 * @param serverPath
	 */
	public void initWorkspace(String serverPath){
		if(serverPath == null)
			return;
		File wksFile = new File(serverPath);
		if(!wksFile.exists()){
			return;
		}
		if(mWorkspaceServerList.contains(serverPath)){
		   return;
		}
		 mWorkspaceServerList.add(serverPath);
		 Workspace workspace = openWorkspace(serverPath);
		 mWorkspaceList.add(workspace);
	}
	private Workspace openWorkspace(String serverPath){
		isUDBOpen = false;
		Workspace workspace = new Workspace();
		WorkspaceType type = null;
		if(serverPath.endsWith(".SMWU")||serverPath.endsWith(".smwu"))
		{
			type = WorkspaceType.SMWU;
		}else if(serverPath.endsWith(".SXWU")||serverPath.endsWith(".sxwu"))
		{
			type = WorkspaceType.SXWU;
		}
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(serverPath);
		info.setType(type);
		boolean isOpened = workspace.open(info);				
		if(!isOpened){
			workspace.dispose();
			return null;
		}
		info.dispose();
		info = null;
		return workspace;
	}
}
