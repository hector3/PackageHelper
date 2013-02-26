package com.android.packagehelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WebService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		// TODO Auto-generated method stub
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
