package com.android.packagehelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;

import com.android.packagehelper.NanoHTTPD.Response;

public class WebService extends Service {

	private static final String TAG = WebService.class.getName();
	private static final int PORT = 8787;

	private NanoHTTPD mHttpd;
	private PackageManager mPackageManager;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		try {
			mHttpd = new NanoHTTPD(PORT, null) {
				@Override
				public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
					Log.d(TAG, method + " " + uri);
					if (method.equals("GET")) {
						if (uri.equals("/")) {
							return getHome();
						} else if (uri.equals("/list")) {
							return getList();
						} else if (uri.equals("/icon")) {
							return getIcon(parms.getProperty("p"));
						}
					}
					return null;
				}
			};
			mPackageManager = getPackageManager();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected Response getIcon(String packageName) {
		Log.d(TAG, "getIcon: " + packageName);
		try {
			ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(packageName, 0);
			Drawable drawable = applicationInfo.loadIcon(mPackageManager);
			if (drawable instanceof BitmapDrawable) {
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (bitmap != null && bitmap.compress(CompressFormat.PNG, 0, baos)) {
					return mHttpd.new Response(NanoHTTPD.HTTP_OK, "image/png", new ByteArrayInputStream(
							baos.toByteArray()));
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Response getList() {
		List<PackageInfo> packageInfos = mPackageManager.getInstalledPackages(0);
		try {
			JSONArray rootJson = new JSONArray();
			for (PackageInfo packageInfo : packageInfos) {
				JSONObject json = new JSONObject();
				json.put("packageName", packageInfo.packageName);
				json.put("versionCode", packageInfo.versionCode);
				json.put("versionName", packageInfo.versionName);
				json.put("uid", packageInfo.applicationInfo.uid);
				json.put("publicSourceDir", packageInfo.applicationInfo.publicSourceDir);
				json.put("sourceDir", packageInfo.applicationInfo.sourceDir);
				json.put("label", packageInfo.applicationInfo.loadLabel(mPackageManager).toString());
				rootJson.put(json);
			}
			return mHttpd.new Response(NanoHTTPD.HTTP_OK, "text/json", rootJson.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Response getHome() {
		// TODO
		return null;
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
