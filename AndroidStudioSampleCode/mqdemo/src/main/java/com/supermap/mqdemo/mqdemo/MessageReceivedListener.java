/**
 * 
 */
package com.supermap.mqdemo.mqdemo;

import com.supermap.data.Point2D;

/**
 * @author zhengyl
 *
 */
public interface MessageReceivedListener {
	public void SynchronousLocationReceived(Point2D location, String clientID);
	public void MultiMediaInfoReceived(String msg);
	
	public void PlotObjectsReceived(String msg);
	
	public void NewMessageReceived();
	
	public void DataDistributionReceived(String msg);
	
	public void DeleteOrderReceived(String msg);
}
