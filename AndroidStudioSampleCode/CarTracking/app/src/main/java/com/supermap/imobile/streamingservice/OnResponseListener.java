package com.supermap.imobile.streamingservice;

import okhttp3.Response;

/**
 * 网络回调接口
 * @author Tron
 *
 */
public interface OnResponseListener {

	void onFailed(Exception exception);

	void onResponse(Response response);

}
