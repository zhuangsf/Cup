package com.sf.cup.login;

import java.util.HashMap;

import org.json.JSONObject;

import com.sf.cup.MainActivity;
import com.sf.cup.R;
import com.sf.cup.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends Activity {
	private final static String TAG = LoginActivity.class.getPackage().getName() + "."
			+ LoginActivity.class.getSimpleName();
	Button btnLogin;
	EditText phone_num;
	Button send_code;
	EditText check_code;
	String phoneNumString;
	int repeatSend=90;
	private long timestamp=0;
	boolean isCountDown=false;
	Thread countDownThread;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
 
		//start sms init
        SMSSDK.initSDK(this,  Utils.SMS_APP_KEY, Utils.SMS_APP_SECRET);
    	EventHandler eh=new EventHandler(){
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			
		};
		SMSSDK.registerEventHandler(eh);
		
        send_code=(Button)findViewById(R.id.send_code);
        send_code.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1检查号码是否正确
				
				//2，调用发验证码接口
				phoneNumString=phone_num.getText().toString();
				if (!TextUtils.isEmpty(phoneNumString)) {
					SMSSDK.getVerificationCode("86", phoneNumString);
					send_code.setEnabled(false);
					send_code.setBackgroundResource(R.drawable.long_button_shape_disable);
					send_code.setTextColor(Color.WHITE);
					timestamp = System.currentTimeMillis();
					countDownThread = new Thread(new Runnable() {
						@Override
						public void run() {
							isCountDown=true;
							countDown(timestamp, mHandler);
						}
					});
					countDownThread.start();

				}else {
					Toast.makeText(LoginActivity.this, "电话不能为空", Toast.LENGTH_SHORT).show();
				}
				Utils.Log("verification phone ==>>"+phoneNumString);
			}
		});
        
        phone_num = (EditText) findViewById(R.id.phone_num);
        phone_num.setText("");
        phone_num.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0&& !isCountDown) {
					send_code.setEnabled(true);
					send_code.setBackgroundResource(R.drawable.long_button_selector);
				} else {
					send_code.setEnabled(false);
					send_code.setBackgroundResource(R.drawable.long_button_shape_disable);
					send_code.setTextColor(Color.WHITE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
        phone_num.requestFocus();
		if (phone_num.getText().length() > 0&& !isCountDown) {
			send_code.setEnabled(true);
				send_code.setBackgroundResource(R.drawable.long_button_selector);
		}

		check_code = (EditText) findViewById(R.id.check_code);
		check_code.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s.length() > 0 ) {
						btnLogin.setEnabled(true);
						btnLogin.setBackgroundResource(R.drawable.long_button_selector);
					} else {
						btnLogin.setEnabled(false);
						btnLogin.setBackgroundResource(R.drawable.long_button_shape_disable);
						btnLogin.setTextColor(Color.WHITE);
					}
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		
        
        
        
		btnLogin=(Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(check_code.getText().toString())){
					SMSSDK.submitVerificationCode("86", phoneNumString, check_code.getText().toString());
				}else {
					Toast.makeText(LoginActivity.this, "验证码不能为空",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
	                HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
	                Utils.httpGet(Utils.URL_PATH+"/user/phonelogin?phone="+(String) phoneMap.get("phone"), mHandler);
//					Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
//					textView2.setText("提交验证码成功");
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					boolean issmart=(Boolean)data;//发送验证码 true为智能验证，false为普通下发短信
					//不考虑智能验证的了
					Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
//					textView2.setText("验证码已经发送");
					Utils.Log("verification VERIFICATION_CODE send ok,issmart:"+issmart);
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
//					countryTextView.setText(data.toString());
					
				}
			} else {
				((Throwable) data).printStackTrace();
				//#if def{lang} == cn
				// 根据服务器返回的网络错误，给toast提示
				//#elif def{lang} == en
				// show toast according to the error code
				//#endif
				try {
				     Throwable throwable = (Throwable) data;
				     throwable.printStackTrace();
				     JSONObject object = new JSONObject(throwable.getMessage());
				     String des = object.optString("detail");//错误描述
				     int status = object.optInt("status");//错误代码
				     if (status > 0 && !TextUtils.isEmpty(des)) {
					Toast.makeText(LoginActivity.this, des, Toast.LENGTH_SHORT).show();
					Utils.Log(TAG,"verification error status:"+status+" des:"+des);
					return;
				     }
				} catch (Exception e) {
				     //do something							
				}
			}
			
		}
		
	};
	Handler mHandler= new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);                    
            if(msg.what ==Utils.COUNT_DOWN_MSG) {  
            	int lefttime=msg.arg1;
            	if(msg.arg1>0){
            	send_code.setText(lefttime+"秒后可重新发送");
            	}else
            	{
            		send_code.setText("发送验证码");
            		send_code.setEnabled(true);
            		send_code.setBackgroundResource(R.drawable.long_button_selector);
            	}
            }else if(msg.what ==Utils.LOGIN_SUCCESS_MSG){
            	JSONObject jsonObject=(JSONObject)msg.obj;
            	//1,这里需要把这些都写入preferrence 方便后面的界面显示调用。
            	
            	Utils.Log("login success jsonObject:"+jsonObject);
            	
            	//2,启动应用
            	Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            	
            	//3，事情都做完了之后 把自己删了。
            	finish();
            }
        }  	
	};
	private void countDown(long starttime,Handler h){
		boolean isrun=true;
		try {
			while (isrun) {
				Thread.sleep(100);
				int passtime = (int) (System.currentTimeMillis() - starttime) / 1000;
				int lefttime = repeatSend - passtime;
				Message msg = new Message();
				if (lefttime > 0) {
					msg.arg1 = lefttime;
				} else {
					msg.arg1 = 0;
					isrun=false;
					isCountDown=false;
				}
				msg.what = Utils.COUNT_DOWN_MSG;
				h.sendMessage(msg);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}
}
