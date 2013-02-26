package com.android.packagehelper;

import java.io.IOException;
import java.util.Properties;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WebService extends Service {

	private static final String TAG = WebService.class.getName();
	private static final int PORT = 8787;

	private NanoHTTPD mHttpd;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		try {
			mHttpd = new NanoHTTPD(PORT, null) {
				@Override
				public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
					Log.d(TAG, method + " " + uri);
					// TODO
					return null;
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		if (mHttpd != null) {
			mHttpd.stop();
			mHttpd = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
