/**
 *
 */
package com.supermap.mqdemo.mqdemo;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;

import com.supermap.data.Point2D;
import com.supermap.messagequeue.AMQPExchangeType;
import com.supermap.messagequeue.AMQPManager;
import com.supermap.messagequeue.AMQPReceiver;
import com.supermap.messagequeue.AMQPReturnMessage;
import com.supermap.messagequeue.AMQPSender;


/**
 * @author zhengyl
 *
 */
public class MessageQueue {

	//	private String sExchange = "MQDemo_AndroidExchange";
	private String sExchange = "MQDemo_topic_exchange";

	//	private String sQueue = "testAndroidQueue";
	private String sQueue_TxtMessage = "txtMessage";
	private String sQueue_MultiMedia = "mulMedia";
	private String sQueue_Location = "locationUp";
	private String sQueue_Plot = "plot";

	//	private String sRoutingKey = "testAndroid_RoutingKey";
	private String sRoutingKey_TxtMessage = "txtMessage";
	private String sRoutingKey_MultiMedia = "mulMedia";
	private String sRoutingKey_Location = "locationUp";
	private String sRoutingKey_Plot = "plot";



	private String sIP = "182.92.150.115";
	private int sPort = 5672;
	private String sHostName = "/sm/sensors";
	private String sUserName = "supermap";
	private String sPassword = "supermap123";
	private String sUserId = "";

	private AMQPSender mAMQPSender = null;
	//	private AMQPReceiver mAMQPReceiver = null;
	private AMQPReceiver mReceiver_TextMessage = null;
	private AMQPReceiver mReceiver_MultiMedia = null;
	private AMQPReceiver mReceiver_Plot = null;
	private AMQPReceiver mReceiver_Location = null;
	private AMQPManager mAMQPManager = null;

	private Thread			m_Thread_TextMessage = null;
	private Thread			m_Thread_Plot = null;
	private Thread			m_Thread_MultiMedia = null;
	private Thread			m_Thread_Location = null;

	private List<String>			m_listMessages;
	private List<String>			m_listClientId;

	private boolean					m_bFirstTime = true;

	private MessageReceivedListener 	m_MessageReceivedListener= null;

	private boolean						m_MessageSwitch = true;

	@SuppressLint("MissingPermission")
	public MessageQueue(Context context) {
		mAMQPManager = new AMQPManager();

		//先获取MAC试试，要是获取不到就跳转到设置界面
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		//要是wifi不可用则，将其打开
		for(int i=0;i<20 && wifiMgr.getWifiState() != WifiManager.WIFI_STATE_ENABLED;i++){
			wifiMgr.setWifiEnabled(true);
			try {
				//最长时间等三秒
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		String temp = wifiInfo.getMacAddress();
		if(temp == null){
			//如果不能获取到Mac地址那可能就是模拟器了
			temp = "none";
		}
		initQueueAndRoutingKeyWithMac(temp);
		sUserId = android.os.Build.MODEL;
		sUserId += "_";
		sUserId += temp;
	}

	private void initQueueAndRoutingKeyWithMac(String mac) {
		sQueue_TxtMessage += "_";
		sQueue_TxtMessage += mac;
		sQueue_Plot += "_";
		sQueue_Plot += mac;
		sQueue_MultiMedia += "_";
		sQueue_MultiMedia += mac;
		sQueue_Location += "_";
		sQueue_Location += mac;

	}
	private boolean declareQueue() {
		boolean bOk = mAMQPManager.declareQueue(sQueue_TxtMessage);
		bOk &= mAMQPManager.declareQueue(sQueue_MultiMedia);
		bOk &= mAMQPManager.declareQueue(sQueue_Plot);
		bOk &= mAMQPManager.declareQueue(sQueue_Location);
		return bOk;
	}

	private boolean bindQueue() {
		boolean bOk = mAMQPManager.bindQueue(sExchange, sQueue_TxtMessage,  sRoutingKey_TxtMessage);
		bOk &= mAMQPManager.bindQueue(sExchange, sQueue_MultiMedia,  sRoutingKey_MultiMedia);
		bOk &= mAMQPManager.bindQueue(sExchange, sQueue_Plot,  sRoutingKey_Plot);
		bOk &= mAMQPManager.bindQueue(sExchange, sQueue_Location,  sRoutingKey_Location);
		return bOk;
	}

	public boolean init() {
		m_listMessages = new LinkedList<String>();
		m_listClientId = new LinkedList<String>();

		boolean bOk = mAMQPManager.connection(sIP, sPort, sHostName, sUserName, sPassword, sUserId);

		if (bOk) {
//			bOk = mAMQPManager.declareExchange(sExchange, AMQPExchangeType.FANOUT);
			bOk = mAMQPManager.declareExchange(sExchange, AMQPExchangeType.TOPIC);
			if (!bOk) {
				return false;
			}
//			bOk = mAMQPManager.declareQueue(sQueue);
			bOk = declareQueue();
			if (!bOk) {
				return false;
			}
			mAMQPSender = mAMQPManager.newSender();
//			mAMQPReceiver = mAMQPManager.newReceiver(sQueue);
			mReceiver_Location = mAMQPManager.newReceiver(sQueue_Location);
			mReceiver_MultiMedia = mAMQPManager.newReceiver(sQueue_MultiMedia);
			mReceiver_Plot = mAMQPManager.newReceiver(sQueue_Plot);
			mReceiver_TextMessage = mAMQPManager.newReceiver(sQueue_TxtMessage);

			bOk = bindQueue();
//			bOk = mAMQPManager.bindQueue(sExchange, sQueue, sRoutingKey);
			if (!bOk) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}


	}

//	public boolean sendMessage(String geoJson) {
//		if(mAMQPSender != null)
//		{
//			// publish message
//			if (geoJson.isEmpty()) {
//				return true;
//			}
//			else {
//
//				boolean bSend =  mAMQPSender.sendMessage(sExchange, geoJson, sRoutingKey);
//				System.out.println("send:"+bSend);
//			}
//		}
//		else
//		{
//			System.out.println("No connection has been made, please create the connection");
//			return false;
//		}
//
//		return true;
//	}

	public boolean sendMessageByType(String geoJson, int type) {
		if(mAMQPSender != null)
		{
			// publish message
			if (geoJson.isEmpty()) {
				return true;
			}
			else {
				boolean bSend = false;
				if (type == 1) {// multimedia
					bSend = mAMQPSender.sendMessage(sExchange, geoJson, sRoutingKey_MultiMedia);
				} else if (type == 2) {//plot
					bSend = mAMQPSender.sendMessage(sExchange, geoJson, sRoutingKey_Plot);
				} else if (type == 3) {//text
					bSend = mAMQPSender.sendMessage(sExchange, geoJson, sRoutingKey_TxtMessage);
				} else if (type == 0) {//location
					bSend = mAMQPSender.sendMessage(sExchange, geoJson, sRoutingKey_Location);
				}
				System.out.println("send:"+bSend);
			}
		}
		else
		{
			System.out.println("No connection has been made, please create the connection");
			return false;
		}

		return true;
	}
	public boolean sendMessageByRoutingKey(String json, String key) {
		if(mAMQPSender != null)
		{
			// publish message
			if (json.isEmpty()) {
				return true;
			}
			else {
				boolean bSend =  mAMQPSender.sendMessage(sExchange, json, key);
				System.out.println("send:"+bSend);
			}
		}
		else
		{
			System.out.println("No connection has been made, please create the connection");
			return false;
		}

		return true;
	}

	public void startReceive() {
		startReceiveTextMessage();
		startReceiveMultiMedia();
		startReceivePlot();
		startReceiveLocation();
//		if (m_Thread == null) {
//				m_Thread =  new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						Looper.prepare();
//						while (mAMQPReceiver != null) {
//							while (m_MessageSwitch) {
//								AMQPReturnMessage returnMsg = mAMQPReceiver.receiveMessage();
//
//								if (!returnMsg.getMessage().isEmpty()) {
//									if (returnMsg.getQueue().equals(sRoutingKey)) {
//										continue;// 自己发的消息，忽略
//									}
//
//									String msg = returnMsg.getMessage();
//									String type = "";
//									if (msg.length() > 16) {
//										type = msg.substring(0, 16);
//										int pos = type.indexOf("=");
//										type = type.substring(pos+1, type.length() - 1);
//									} else {
//										continue;
//									}
//									try {
//										int contentType = Integer.parseInt(type);
//										msg = msg.substring(16, msg.length());
//										if ( contentType == 0) {//location
////											if (!(returnMsg.getQueue().equals(sExchange))) {
//												Point2D pnt = new Point2D();
//												pnt.fromJson(msg);
//
//												m_MessageReceivedListener.SynchronousLocationReceived(pnt, returnMsg.getQueue());
////											}
//										} else if ( contentType == 3) {//text
//											m_listMessages.add(msg);
//											m_listClientId.add(returnMsg.getQueue());
//											m_MessageReceivedListener.NewMessageReceived();
//
//										}else if ( contentType == 1) {//media
//											// 添加回调到多媒体下载
//											m_MessageReceivedListener.MultiMediaInfoReceived(msg);
//										}else if ( contentType == 2) {//plot
//											// 添加回调到多媒体下载
//											m_MessageReceivedListener.PlotObjectsReceived(msg);
//										} else if (true) {
//											m_listMessages.add(msg);
//											m_listClientId.add(returnMsg.getQueue());
//										}
//
//									} catch (Exception ex) {
//											if ( msg.length() > 23 && msg.substring(0, 23).equals("{content_type=location}")) {
//											msg = msg.substring(23, msg.length());
//											Point2D pnt = new Point2D();
//											pnt.fromJson(msg);
////											if (true ||!(returnMsg.getQueue().equals(sQueue))) {
//												m_MessageReceivedListener.SynchronousLocationReceived(pnt, returnMsg.getQueue());
////											}
//										} else if ( msg.length() > 19 && msg.substring(0, 19).equals("{content_type=text}")) {
//											msg = msg.substring(19, msg.length());
//											m_listMessages.add(msg);
//											m_listClientId.add(returnMsg.getQueue());
//
//										}else if ( msg.length() > 20 && msg.substring(0, 20).equals("{content_type=media}")) {
//											msg = msg.substring(20, msg.length());
//											// 添加回调到多媒体下载
//											m_MessageReceivedListener.MultiMediaInfoReceived(msg);
//										}else if ( msg.length() > 19 && msg.substring(0, 19).equals("{content_type=plot}")) {
//											msg = msg.substring(19, msg.length());
//											// 添加回调到多媒体下载
//											m_MessageReceivedListener.PlotObjectsReceived(msg);
//										} else if (true) {
//											m_listMessages.add(msg);
//											m_listClientId.add(returnMsg.getQueue());
//										}
//
//									}
//
//
//								}//if
//							}//while
//						}//if
//					}//run
//				});//new
//				m_Thread.start();
//		}
//		else {
//			m_MessageSwitch = true;
//		}
	}

	public void stopReceive() {
		m_MessageSwitch = false;
	}

	public List<String> getMessages() {
		return m_listMessages;
	}

	public List<String> getClientIds() {
		return m_listClientId;
	}

	public void dispose() {
		m_listClientId.clear();
		m_listMessages.clear();
		if (m_Thread_TextMessage != null) {
			m_Thread_TextMessage.stop();
			m_Thread_TextMessage.destroy();
		}
		if (m_Thread_MultiMedia != null) {
			m_Thread_MultiMedia.stop();
			m_Thread_MultiMedia.destroy();
		}
		if (m_Thread_Plot != null) {
			m_Thread_Plot.stop();
			m_Thread_Plot.destroy();
		}
		if (m_Thread_Location != null) {
			m_Thread_Location.stop();
			m_Thread_Location.destroy();
		}
		mAMQPManager.disconnection();
		if (mReceiver_MultiMedia != null) {
			mReceiver_MultiMedia.dispose();
		}
		if (mReceiver_TextMessage != null) {
			mReceiver_TextMessage.dispose();
		}
		if (mReceiver_Plot != null) {
			mReceiver_Plot.dispose();
		}
		if (mReceiver_Location != null) {
			mReceiver_Location.dispose();
		}
	}

	public void suspend() {
//		mAMQPManager.disconnection();
		stopReceive();
	}

	public void resume() {
		if (!m_bFirstTime) {
//			mAMQPManager.connection(sIP, sPort, sHostName, sUserName, sPassword);
		}
		startReceive();
	}

	public void setSynLocationReceivedListener(MessageReceivedListener listener) {
		m_MessageReceivedListener = listener;
	}

	private void startReceiveTextMessage() {
		if (m_Thread_TextMessage == null) {
			m_Thread_TextMessage =  new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					while (mReceiver_TextMessage != null) {
						while (m_MessageSwitch) {
							AMQPReturnMessage returnMsg = mReceiver_TextMessage.receiveMessage();

							if (!returnMsg.getMessage().isEmpty()) {
								if (returnMsg.getQueue().equals(sUserId)) {
									continue;// 自己发的消息，忽略
								}

								String msg = returnMsg.getMessage();
								String type = "";
								if (msg.length() > 16) {
									type = msg.substring(0, 16);
									int pos = type.indexOf("=");
									type = type.substring(pos+1, type.length() - 1);
								} else {
									continue;
								}
								try {
									int contentType = Integer.parseInt(type);
									msg = msg.substring(16, msg.length());
									if ( contentType == 3) {//text
										m_listMessages.add(msg);
										m_listClientId.add(returnMsg.getQueue());
										m_MessageReceivedListener.NewMessageReceived();

									}

								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}//if
						}//while
					}//if
				}//run
			});//new
			m_Thread_TextMessage.start();
		}
		else {
			m_MessageSwitch = true;
		}

	}

	private void startReceiveMultiMedia() {
		if (m_Thread_MultiMedia == null) {
			m_Thread_MultiMedia =  new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					while (mReceiver_MultiMedia != null) {
						while (m_MessageSwitch) {
							AMQPReturnMessage returnMsg = mReceiver_MultiMedia.receiveMessage();

							if (!returnMsg.getMessage().isEmpty()) {
								if (returnMsg.getQueue().equals(sUserId)) {
									continue;// 自己发的消息，忽略
								}

								String msg = returnMsg.getMessage();
								String type = "";
								if (msg.length() > 16) {
									type = msg.substring(0, 16);
									int pos = type.indexOf("=");
									type = type.substring(pos+1, type.length() - 1);
								} else {
									continue;
								}
								try {
									int contentType = Integer.parseInt(type);
									msg = msg.substring(16, msg.length());
									if ( contentType == 1) {//media
										// 添加回调到多媒体下载
										m_MessageReceivedListener.MultiMediaInfoReceived(msg);
									} else if (true) {
										m_listMessages.add(msg);
										m_listClientId.add(returnMsg.getQueue());
									}

								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}//if
						}//while
					}//if
				}//run
			});//new
			m_Thread_MultiMedia.start();
		}
		else {
			m_MessageSwitch = true;
		}

	}

	private void startReceivePlot() {
		if (m_Thread_Plot == null) {
			m_Thread_Plot =  new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					while (mReceiver_Plot != null) {
						while (m_MessageSwitch) {
							AMQPReturnMessage returnMsg = mReceiver_Plot.receiveMessage();

							if (!returnMsg.getMessage().isEmpty()) {
								if (returnMsg.getQueue().equals(sUserId)) {
									continue;// 自己发的消息，忽略
								}

								String msg = returnMsg.getMessage();
								String type = "";
								if (msg.length() > 16) {
									type = msg.substring(0, 16);
									int pos = type.indexOf("=");
									type = type.substring(pos+1, type.length() - 1);
								} else {
									continue;
								}
								try {
									int contentType = Integer.parseInt(type);
									msg = msg.substring(16, msg.length());
									if ( contentType == 2) {//plot
										// 添加回调到多媒体下载
										m_MessageReceivedListener.PlotObjectsReceived(msg);
									} else if (contentType == 5) {
										//解析delete msg
										m_MessageReceivedListener.DeleteOrderReceived(msg);
									}

								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}//if
						}//while
					}//if
				}//run
			});//new
			m_Thread_Plot.start();
		}
		else {
			m_MessageSwitch = true;
		}

	}

	private void startReceiveLocation() {
		if (m_Thread_Location == null) {
			m_Thread_Location =  new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					while (mReceiver_Location != null) {
						while (m_MessageSwitch) {
							AMQPReturnMessage returnMsg = mReceiver_Location.receiveMessage();

							if (!returnMsg.getMessage().isEmpty()) {
								if (returnMsg.getQueue().equals(sUserId)) {
									continue;// 自己发的消息，忽略
								}

								String msg = returnMsg.getMessage();
								String type = "";
								if (msg.length() > 16) {
									type = msg.substring(0, 16);
									int pos = type.indexOf("=");
									type = type.substring(pos+1, type.length() - 1);
								} else {
									continue;
								}
								try {
									int contentType = Integer.parseInt(type);
									msg = msg.substring(16, msg.length());
									if ( contentType == 0) {//location
										Point2D pnt = new Point2D();
										pnt.fromJson(msg);

										m_MessageReceivedListener.SynchronousLocationReceived(pnt, returnMsg.getQueue());
									} else if (true) {
										m_listMessages.add(msg);
										m_listClientId.add(returnMsg.getQueue());
									}

								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}//if
						}//while
					}//if
				}//run
			});//new
			m_Thread_Location.start();
		}
		else {
			m_MessageSwitch = true;
		}

	}
}
