package com.android.packagehelper;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements IOCallback {

	private static final String TAG = MainActivity.class.getName();

	private EditText mContentEdit;
	private EditText mNicknameEdit;
	private EditText mMessageEdit;
	private Button mSendButton;
	private SocketIO mSocketIO;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContentEdit = (EditText) findViewById(R.id.chat_content);
		mNicknameEdit = (EditText) findViewById(R.id.chat_nickname);
		mMessageEdit = (EditText) findViewById(R.id.chat_message);
		mSendButton = (Button) findViewById(R.id.chat_send);
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
		try {
			mSocketIO = new SocketIO("http://172.16.100.131/");
			mSocketIO.connect(this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSocketIO.disconnect();
	}

	protected void sendMessage() {
		if (mSocketIO == null) {
			Toast.makeText(this, "SocketIO not ready", Toast.LENGTH_SHORT).show();
			return;
		}
		String nickname = mNicknameEdit.getText().toString();
		String message = mMessageEdit.getText().toString();
		if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(message)) {
			Toast.makeText(this, "Nickname or Message empty", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			JSONObject data = new JSONObject();
			data.put("nickname", nickname);
			data.put("message", message);
			mSocketIO.emit("chat", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		Log.d(TAG, "on: event = " + event);
		try {
			JSONObject data = (JSONObject) args[0];
			final String nickname = data.getString("nickname");
			final String message = data.getString("message");
			mContentEdit.post(new Runnable() {
				@Override
				public void run() {
					mContentEdit.append(nickname + ": " + message + "\n");
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnect() {
		Log.d(TAG, "onConnect");
	}

	@Override
	public void onDisconnect() {
		Log.d(TAG, "onDisconnect");
	}

	@Override
	public void onError(SocketIOException socketIOException) {
		Log.d(TAG, "onError");
		socketIOException.printStackTrace();
	}

	@Override
	public void onMessage(String data, IOAcknowledge ack) {
		Log.d(TAG, "onMessage: data = " + data);
	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		Log.d(TAG, "onMessage:  json = " + json);
	}

}
