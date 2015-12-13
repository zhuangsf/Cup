package com.sf.cup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.sf.cup.utils.Utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentWater extends Fragment {

	ListView temperatureListView; 
	ImageView add_temperature_button;
	Button cancel_temperature_button;
	List<Map<String, Object>> temperatureList = new ArrayList<Map<String, Object>>(); //list view ����һֱ��Ū���
	TemperatureListViewAdapter hlva;
	
	FrameLayout temperature_mode;
	View maskView;
	LinearLayout temperature_setting;
	boolean temperature_mode_enable=false;
	int temperature_setting_value=00;
	int temperature_current_value=20;
	int temperature_mode_index=-1;
	
	private static final String VIEW_INFO_TEXT="info_text";
	private static final String VIEW_TEMPERATURE_TEXT="temperature_text";
	private static final String VIEW_RADIO_BTN="radio_btn";
	
	
	TextView water_status_text1;
	TextView water_status_text2;
	TextView water_status_text3;
	ImageView water_status_pic1;
	ImageView water_status_pic2;
	ImageView water_status_pic3;
	
	TextView current_cup_temperature;
	
	EditText infoString;
	EditText tempString; 
	AlertDialog alertDialog;
	
	private static final int MSG_SHOW_IM=1;
	private static final int MSG_STOP_SEND=2;
	
    // Stops sending after 3 seconds.
    private static final long SEND_PERIOD = 3000;
    private static ProgressDialog pd;// �ȴ�����Ȧ
    private int temp_index=-1; //this is a important  int.   if it !=-1  means send a msg to bt   the msg is this value.
    AlertDialog sendFailAlertDialog;
    
	Handler mHandler = new Handler()
	  {
	    @Override
		public void handleMessage(Message paramAnonymousMessage)
	    {
//	    Utils.Log("handle:"+paramAnonymousMessage);
	     switch (paramAnonymousMessage.what)
	     {
				case MSG_SHOW_IM:
					//alertdialog with edittext cant not open im.  
					try {
						Thread.sleep(200);
						infoString.dispatchTouchEvent( MotionEvent.obtain( SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, infoString.getRight(), infoString.getRight() + 5, 0));
						infoString.dispatchTouchEvent( MotionEvent.obtain( SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, infoString.getRight(), infoString.getRight() + 5, 0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					break;
				case MSG_STOP_SEND:
					if(pd!=null){
						pd.dismiss();
					}
					if(temp_index!=-1){
						Utils.Log("xxxxxxxxxxxxxxxxxx water mHandler stop send some error may happen");
						temp_index=-1;
						if (sendFailAlertDialog == null) {
							sendFailAlertDialog=new AlertDialog.Builder(getActivity())
									.setTitle("��ܰ��ʾ")
									.setMessage("���������ѶϿ�")
//									.setCancelable(false)
									.setPositiveButton("����", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											 boolean result= ((MainActivity)getActivity()).reConnect();
											 if(!result){
												 Toast.makeText(getActivity(), "�޷����ӵ������豸", Toast.LENGTH_SHORT).show();
											 }
										}
									})
									.setNegativeButton("ȡ��",null).create();
						}
						try {
								sendFailAlertDialog.show();
						} catch (Exception e) {
							sendFailAlertDialog=null;
						}
					}
					
					
					
					break;
			}
	    }
	  };
	  
	  
	  /**
	   * update the currentTemperature and temperature status
	   * 
	   */
	  private void updateCurrentTemperature(){
		  //1,update current temperature
		  current_cup_temperature.setText(""+temperature_current_value); 
		  //2,update status
		  water_status_text1.setTextColor(getActivity().getResources().getColor(R.drawable.darkgray));
		  water_status_text1.setTextSize(12);
		  water_status_text2.setTextColor(getActivity().getResources().getColor(R.drawable.darkgray));
		  water_status_text2.setTextSize(12);
		  water_status_text3.setTextColor(getActivity().getResources().getColor(R.drawable.darkgray));
		  water_status_text3.setTextSize(12);
		  water_status_pic1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.water_status_point_disable));
		  water_status_pic2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.water_status_point_disable));
		  water_status_pic3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.water_status_point_disable));
		  if(temperature_mode_index==-1||!temperature_mode_enable){
			  water_status_text1.setTextColor(getActivity().getResources().getColor(R.drawable.cup_pink));
			  water_status_text1.setTextSize(15);
			  water_status_pic1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.water_status_point_focus));
		  } else if(temperature_mode_index!=-1){
			  if(temperature_current_value!=temperature_setting_value){
				  water_status_text2.setTextColor(getActivity().getResources().getColor(R.drawable.cup_pink));
				  water_status_text2.setTextSize(15);
				  water_status_pic2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.water_status_point_focus));
			  }else{
				  water_status_text3.setTextColor(getActivity().getResources().getColor(R.drawable.cup_pink));
				  water_status_text3.setTextSize(15);
				  water_status_pic3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.water_status_point_focus));
				  temperatureComplete();
			  }
		  } 
//		  Utils.Log("updateCurrentTemperature temperature_mode_index:"+temperature_mode_index+" temperature_mode_enable:"+temperature_mode_enable);
	  }
	  
	  private MediaPlayer mp; 

	private void temperatureComplete() {
		try {
			if (alertDialog == null) {
				alertDialog = new AlertDialog.Builder(getActivity()).setMessage("�ף��ѵ��趨��ˮ�¶ȿ���\n�뼰ʱ����Ŷ").setTitle("��ܰ��ʾ")
						.setPositiveButton("ȷ��", null).create();
			}

			alertDialog.show();

			// ����MediaPlayer����
			mp = new MediaPlayer();
			// �����ֱ�����res/raw/xingshu.mp3,R.java���Զ�����{public static final int
			// xingshu=0x7f040000;}
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			// mp = MediaPlayer.create(this, notification);
			mp.setDataSource(getActivity(), notification);
			// ��MediaPlayerȡ�ò�����Դ��stop()֮��Ҫ׼��PlayBack��״̬ǰһ��Ҫʹ��MediaPlayer.prepeare()
			mp.prepare();
			// ��ʼ��������
			mp.start();
			// ���ֲ�����ϵ��¼�����
			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					// ѭ������
					try {
						// mp.start();
						if(mp!=null){
							if(mp.isPlaying())
					            mp.stop();
					        mp.reset();
					        mp.release();
					        mp=null;
						}
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			});
			// ��������ʱ����������¼�����
			mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// �ͷ���Դ
					try {
						if (mp != null) {
							if (mp.isPlaying())
								mp.stop();
							mp.reset();
							mp.release();
							mp = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences p;
		p = getActivity().getSharedPreferences(Utils.SHARE_PREFERENCE_CUP,Context.MODE_PRIVATE);
		temperature_mode_enable=p.getBoolean(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_ENABLE, false);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_water, null);

		temperatureListView = (ListView) v.findViewById(R.id.temperature_list);
		
		SharedPreferences p=Utils.getSharedPpreference(getActivity());
		int isFirst=p.getInt(Utils.SHARE_PREFERENCE_CUP_OPEN_COUNTS, 0);
		Utils.Log("isFirst must be bigger than 2 or there must be a bug :"+isFirst);
		//isFirst must be bigger than 2 or there must be a bug 
		Map<String, Object> m=new HashMap<String, Object>();
		if(isFirst==2){
			m.put(VIEW_INFO_TEXT, "���ϵ�һ��ˮ��");
			m.put(VIEW_TEMPERATURE_TEXT, "45");
			m.put(VIEW_RADIO_BTN, false);
			temperatureList.add(m);
			m=new HashMap<String, Object>();
			m.put(VIEW_INFO_TEXT, "�ݿ���");
			m.put(VIEW_TEMPERATURE_TEXT, "75");
			m.put(VIEW_RADIO_BTN, false);
			temperatureList.add(m);	
			SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_INFO[0], "���ϵ�һ��ˮ��");
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_VALUE[0], "45");
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_INFO[1], "�ݿ���");
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_VALUE[1], "75");
					e.commit();
		}else{
			for(int i=0;i<5;i++){
				String text=p.getString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_INFO[i], "");
				String value=p.getString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_VALUE[i], "");
				temperature_mode_index=p.getInt(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE, -1);
				if(!TextUtils.isEmpty(text)&&!TextUtils.isEmpty(value)){
					m=new HashMap<String, Object>();
					m.put(VIEW_INFO_TEXT, text);
					m.put(VIEW_TEMPERATURE_TEXT, value);
					if(temperature_mode_index==i){
						m.put(VIEW_RADIO_BTN, true);
						temperature_setting_value=Integer.parseInt(value);
					}else{
						m.put(VIEW_RADIO_BTN, false);
					}
					temperatureList.add(m);
				}
			}
		}
		
		
		hlva = new TemperatureListViewAdapter(this.getActivity(), temperatureList,
				R.layout.tab_water_select_item, new String[] { VIEW_INFO_TEXT, VIEW_TEMPERATURE_TEXT, VIEW_RADIO_BTN },
				new int[] { R.id.info_text, R.id.temperature_text, R.id.radio_btn });
		temperatureListView.setAdapter(hlva);
		setHeight(hlva, temperatureListView);

		add_temperature_button = (ImageView) v.findViewById(R.id.add_temperature_button);
		int size = temperatureList.size();
		if (size >= 5) {
			add_temperature_button.setEnabled(false);
			add_temperature_button.setClickable(false);
			add_temperature_button.setBackground(getActivity().getResources().getDrawable(R.drawable.water_mode_add_disable));
		} else {
			add_temperature_button.setEnabled(true);
			add_temperature_button.setClickable(true);
			add_temperature_button.setBackground(getActivity().getResources().getDrawable(R.drawable.mode_add_selector));
		}
		add_temperature_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View layout = inflater.inflate(R.layout.tab_water_select_dialog,
						(ViewGroup) v.findViewById(R.id.dialog));
				infoString = (EditText) layout.findViewById(R.id.info_input);
				tempString = (EditText) layout.findViewById(R.id.temp_input);
				
				final AlertDialog ad = new AlertDialog.Builder(getActivity())
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Map<String, Object> m=new HashMap<String, Object>();
						m.put(VIEW_INFO_TEXT, infoString.getText().toString());
						m.put(VIEW_TEMPERATURE_TEXT, tempString.getText().toString());
						m.put(VIEW_RADIO_BTN, false);
						temperatureList.add(m);
						doUpdate();
					}

				}).setNegativeButton("ȡ��", null).create();
				
				ad.setTitle("�¶�ģʽ�趨");
				ad.setView(layout);
				ad.show();
				ad.getCurrentFocus();
				
				try {  
				    Field mAlert = AlertDialog.class.getDeclaredField("mAlert");  
				    mAlert.setAccessible(true);  
				    Object alertController = mAlert.get(ad);  
				  
				    Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");  
				    mTitleView.setAccessible(true);  
				  
				    TextView title = (TextView) mTitleView.get(alertController);  
//				    title.setTextColor(0xffff0022);   
//				    title.setGravity(Gravity.CENTER);
				} catch (NoSuchFieldException e) {  
				    e.printStackTrace();  
				} catch (IllegalArgumentException e) {  
				    e.printStackTrace();  
				} catch (IllegalAccessException e) {  
				    e.printStackTrace();  
				}  
				
				Button adPosiButton=ad.getButton(AlertDialog.BUTTON_POSITIVE);
				adPosiButton.setEnabled(false);
				
				Message msg=new Message();
				msg.what=MSG_SHOW_IM;
				msg.arg1=1;
				mHandler.sendMessage(msg);
				/*adPosiButton.setBackground(getActivity().getResources().getDrawable(R.drawable.long_button_selector));
				 LinearLayout.LayoutParams lp1=new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
				 lp1.setMargins(50, 0, 0, 0);
				adPosiButton.setLayoutParams(lp1);
				
				LinearLayout l=	(LinearLayout) adPosiButton.getParent();
				l.setGravity(Gravity.CENTER);
				l.setBackground(null);
				l.setDividerDrawable(null);
				
				Button adNegaButton=ad.getButton(AlertDialog.BUTTON_NEGATIVE);
				adNegaButton.setEnabled(false);
				adNegaButton.setBackground(getActivity().getResources().getDrawable(R.drawable.long_button_selector));
				 LinearLayout.LayoutParams lp2=new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
				 lp2.setMargins(0, 0, 50, 0);
				adNegaButton.setLayoutParams(lp2);*/
				
				tempString.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void afterTextChanged(Editable s) {
						ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						if(s!=null&&!"".equals(s.toString())){
							try {
							int a=Integer.parseInt(s.toString());
							String info_text=infoString.getText().toString();
							if(a<=80&&a>=20&&!TextUtils.isEmpty(info_text)){
								ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
							}
							} catch (Exception e) {
								// i dont care this error
							}							
						}
					}
				});
				infoString.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void afterTextChanged(Editable s) {
						ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						if(s!=null&&!"".equals(s.toString())){
							try {
							String temp_text=tempString.getText().toString();
							int a=Integer.parseInt(temp_text.toString());
							if(a<=80&&a>=20&&!TextUtils.isEmpty(s)){
								ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
							}
							} catch (Exception e) {
								// i dont care this error
							}							
						}
					}
				});
				
			}
		});

		
		//visiable gone
		cancel_temperature_button=(Button)v.findViewById(R.id.cancel_temperature_button);
		cancel_temperature_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for(Map<String, Object> m :temperatureList){
					m.put(VIEW_RADIO_BTN,false);
				}
				doUpdate();
			}
		});
		
		
		
		//click to open/close the temperature mode
		temperature_setting=(LinearLayout)v.findViewById(R.id.temperature_setting);
		temperature_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.Log("xxxxxxxxxxxxxxxxxx temperature_setting:" + v);
				//1,show confirm dialog
				
				
				//2 TODO send message to bluetooth
				//show waiting dialog
				
				//3 update ui 
				temperature_mode_enable=!temperature_mode_enable;
				if(!temperature_mode_enable){
					//if disable the temperature mode   unselect mode
					temperature_setting_value=0;
					if(temperature_mode_index!=-1){
						temperatureList.get(temperature_mode_index).put(VIEW_RADIO_BTN, false);
					}
					temperature_mode_index = -1;
					hlva.notifyDataSetChanged();
				}
				updateUiShow();
				
				//4 save value  temperature_mode_enable
				SharedPreferences.Editor e=Utils.getSharedPpreferenceEdit(getActivity());
				e.putBoolean(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_ENABLE, temperature_mode_enable);
				e.commit();
			}
		});
		
		
		temperature_mode=(FrameLayout)v.findViewById(R.id.temperature_mode);
		//create a mask to disable mode change
		maskView=new View(getActivity());
		maskView.setLayoutParams(new ViewGroup.LayoutParams(
		            ViewGroup.LayoutParams.FILL_PARENT,
		            ViewGroup.LayoutParams.FILL_PARENT));
		maskView.setBackgroundColor(0x88000000);
		maskView.setClickable(true);// set true  to disable other view click
		
		
		
		
		//status  view
		water_status_text1=(TextView)v.findViewById(R.id.water_status_text1);
		water_status_text2=(TextView)v.findViewById(R.id.water_status_text2);
		water_status_text3=(TextView)v.findViewById(R.id.water_status_text3);
		water_status_pic1=(ImageView)v.findViewById(R.id.water_status_pic1);
		water_status_pic2=(ImageView)v.findViewById(R.id.water_status_pic2);
		water_status_pic3=(ImageView)v.findViewById(R.id.water_status_pic3);		
		
		
		current_cup_temperature=(TextView)v.findViewById(R.id.current_cup_temperature); 
		//create a thread to get cup temperature period
		askTemperatureFromBT();
		
		updateUiShow();// create first time
		return v;
	}
	
	private Timer timer = new Timer(true);
	public void askTemperatureFromBT(){
		//����
		TimerTask task = new TimerTask() {
		  public void run() {
			  //ask temp  not need to send msg  when bt return temp msg it will user setCurrentTemperatureFromBT
			  ((MainActivity)getActivity()).sentAskTemperature();
			  }
		};
		 
		//������ʱ��
		timer.schedule(task, 60000, 60000);
	}
	public void setCurrentTemperatureFromBT(int t){
		//TODO  first time try to connect bt get the temperature.  have better show a waiting dialog    onstop to reset the bianliang
		temperature_current_value=t;
		updateCurrentTemperature();
	}
	
	/**
	 * update 
	 * 1,disable mask
	 * 2,setting temperture value color
	 * 3,update the currentTemperature and temperature status
	 */
	private void updateUiShow(){
		
		//1,turn on/off the temperature mode
		setMaskToModeSetting(temperature_mode_enable);
		
		//2,change display
		TextView t=(TextView)temperature_setting.findViewById(R.id.temperature_du);
		Utils.Log("xxxxxxxxxxxxxxxxxx temperature_mode_enable:" + temperature_mode_enable +"t color:"+Integer.toHexString(t.getCurrentTextColor()));
		if(temperature_mode_enable){
			t.setTextColor(getActivity().getResources().getColor(R.drawable.cup_pink));
		}else{
			t.setTextColor(getActivity().getResources().getColor(R.drawable.darkgray));
		}
		setTemperaturePic(temperature_setting,temperature_mode_enable);
		
		//3,update the status
		updateCurrentTemperature();
	}

	int enableTemperaturePicId[]={
			R.drawable.num_focus_0,
			R.drawable.num_focus_1,
			R.drawable.num_focus_2,
			R.drawable.num_focus_3,
			R.drawable.num_focus_4,
			R.drawable.num_focus_5,
			R.drawable.num_focus_6,
			R.drawable.num_focus_7,
			R.drawable.num_focus_8,
			R.drawable.num_focus_9
	};
	int disableTemperaturePicId[]={
			R.drawable.num_disable_0,
			R.drawable.num_disable_1,
			R.drawable.num_disable_2,
			R.drawable.num_disable_3,
			R.drawable.num_disable_4,
			R.drawable.num_disable_5,
			R.drawable.num_disable_6,
			R.drawable.num_disable_7,
			R.drawable.num_disable_8,
			R.drawable.num_disable_9
	};
	private void setTemperaturePic(View v, boolean isEnable) {
		ImageView v1 = (ImageView) v.findViewById(R.id.temperature_value1);
		ImageView v2 = (ImageView) v.findViewById(R.id.temperature_value2);
		Resources resources = getActivity().getResources();
		int temperatureValue1 = getSettingValue() / 10;
		int temperatureValue2 = getSettingValue() % 10;
		Drawable bgDrawable1;
		Drawable bgDrawable2;
		if (isEnable) {
			bgDrawable1 = resources.getDrawable(enableTemperaturePicId[temperatureValue1]);
			bgDrawable2 = resources.getDrawable(enableTemperaturePicId[temperatureValue2]);
		} else {
			bgDrawable1 = resources.getDrawable(disableTemperaturePicId[temperatureValue1]);
			bgDrawable2 = resources.getDrawable(disableTemperaturePicId[temperatureValue2]);
		}
		v1.setImageDrawable(bgDrawable1);
		v2.setImageDrawable(bgDrawable2);
		
		TextView water_mode=(TextView)v.findViewById(R.id.water_mode);
		water_mode.setText(getSettingMode());

	}
	/**
	 * get temperature which select mode define 
	 * @return
	 */
	private int getSettingValue(){
		
		return temperature_setting_value;
	}
	/**
	 * get temperature which select mode define 
	 * @return
	 */
	private String getSettingMode(){
		String settingMode="δ�趨";
		if(temperature_mode_index!=-1)
		{
			settingMode=(String)temperatureList.get(temperature_mode_index).get(VIEW_INFO_TEXT);
		}
		
		return settingMode;
	}
	
	
	private void setMaskToModeSetting(boolean isEnable){
//		temperature_mode
		
		if(!isEnable)
		{
			temperature_mode.addView(maskView);
		}else
		{
			temperature_mode.removeView(maskView);
		}
	}
	
	
	
	/**
	 * update the temperature select mode 
	 */
	private void doUpdate() {
		if (hlva != null) {
			int size = temperatureList.size();
			Utils.Log("update listview count:" + size);
			hlva.notifyDataSetChanged();
			setHeight(hlva, temperatureListView);

			SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
			for (int i = 0; i < 5; i++) {
				if (i < size) {
					String text = (String) temperatureList.get(i).get(VIEW_INFO_TEXT);
					String value = (String) temperatureList.get(i).get(VIEW_TEMPERATURE_TEXT);
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_INFO[i], text);
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_VALUE[i], value);
				} else {
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_INFO[i], "");
					e.putString(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE_VALUE[i], "");
				}

			}
			e.commit();

			if (size >= 5) {
				add_temperature_button.setEnabled(false);
				add_temperature_button.setClickable(false);
				add_temperature_button.setBackground(getActivity().getResources().getDrawable(R.drawable.water_mode_add_disable));
			} else {
				add_temperature_button.setEnabled(true);
				add_temperature_button.setClickable(true);
				add_temperature_button.setBackground(getActivity().getResources().getDrawable(R.drawable.mode_add_selector));
			}
		}
	}

	public static FragmentWater newInstance(Bundle b) {
		FragmentWater fd = new FragmentWater();
		fd.setArguments(b);
		return fd;
	}

	public void setHeight(BaseAdapter comAdapter, ListView l) {
		int listViewHeight = 0;
		int adaptCount = comAdapter.getCount();
		for (int i = 0; i < adaptCount; i++) {
			View temp = comAdapter.getView(i, null, l);
			temp.measure(0, 0);
			listViewHeight += temp.getMeasuredHeight()+15;// the divide height
		}
		LayoutParams layoutParams = l.getLayoutParams();
		layoutParams.width = LayoutParams.FILL_PARENT;
		layoutParams.height = listViewHeight + 4;
		l.setLayoutParams(layoutParams);
	}

	protected class TemperatureListViewAdapter extends SimpleAdapter {
		float downX=0f;
		float upX=0f;
		// ���ڼ�¼ÿ��RadioButton��״̬������ֻ֤��ѡһ��
		 HashMap<String, Boolean> states = new HashMap<String, Boolean>();
		  
		public TemperatureListViewAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
//			view.setOnClickListener(new MyListener(position));
			
			final int p=position;
			final RadioButton radio=(RadioButton) view.findViewById(R.id.radio_btn);  
			radio.setOnClickListener(new MyListener(position));
			
			RelativeLayout temperature_mode=(RelativeLayout) view.findViewById(R.id.temperature_mode);  
			temperature_mode.setOnClickListener(new MyListener(position));
			
			ImageView delete_model=(ImageView)view.findViewById(R.id.delete_model);
			delete_model.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//################ for delete must be carefull
					
					new AlertDialog.Builder(getActivity())
					.setMessage("ȷ��ɾ����ģʽ��")
			    	.setTitle("��ܰ��ʾ")
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							temperatureList.remove(p);
							if(temperature_mode_index==p)
							{
								temperature_mode_index=-1;
								//TODO
								//0 ,show a waiting dialog
								
								//1, send request to cup
							}else
							{
								for (int i=0;i<temperatureList.size();i++) {
									if((boolean)temperatureList.get(i).get(VIEW_RADIO_BTN))
									{
										temperature_mode_index=i;
									}
								}
							}
					       // TemperatureListViewAdapter.this.notifyDataSetChanged();
					        doUpdate();
						}
					})
					.setNegativeButton("ȡ��", null)
					.create()
					.show();
					
				
				}
			});

			
			
			/*
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction())//���ݶ�����ִ�д���     
                    {    
                    case MotionEvent.ACTION_MOVE://����     
                    	int moveX = (int) event.getX(); 
                    	int deltaX=(int)(downX-moveX);
                    	if(deltaX<40&&deltaX>0){
                    		Utils.Log("xxxxxxxxxxxxxxxxxx deltaX:" + deltaX);
                    		v.scrollBy(deltaX, 0);
                    	}
                        break;    
                    case MotionEvent.ACTION_DOWN://����     
                        downX = event.getX();  
                        break;    
                    case MotionEvent.ACTION_UP://�ɿ�     
                        upX = event.getX();  
//                        Toast.makeText(context, "up..." + Math.abs(UpX-DownX), Toast.LENGTH_SHORT).show();  
                        Utils.Log("xxxxxxxxxxxxxxxxxx downX-upX:" + (downX-upX));
                        if(downX-upX>20){  
                            v.scrollBy(40, 0);  
                        }  else{
                        	v.scrollBy(0, 0);
                        }
                        break;    
                    default:    
                    }    
                    return true;   
				}
			});
			
			*/
			return view;
		}
		
		@Override
		public void notifyDataSetChanged() {
			//TODO
			//0 ,show a waiting dialog
			
			//1, send request to cup
			
			//2  update the mode select
			super.notifyDataSetChanged();
			 //3,update temperature
			if(temperature_mode_index==-1){
				temperature_setting_value=00;
			}else{
				temperature_setting_value=Integer.parseInt((String)temperatureList.get(temperature_mode_index).get(VIEW_TEMPERATURE_TEXT));
			}
	        setTemperaturePic(temperature_setting,temperature_mode_enable);
	        
	        //4,update current temperature and status
	        updateCurrentTemperature();
	        
	        //5,record this index
	     
	        SharedPreferences.Editor e=Utils.getSharedPpreferenceEdit(getActivity());
			e.putInt(Utils.SHARE_PREFERENCE_CUP_TEMPERATURE_MODE, temperature_mode_index);
			e.commit();
			
		}
		
		

	}

	private class MyListener implements OnClickListener {
		int mPosition;

		public MyListener(int inPosition) {
			mPosition = inPosition;
		}


		@Override
		public void onClick(View v) {
			if(temperature_mode_index==mPosition){
				temperatureList.get(mPosition).put(VIEW_RADIO_BTN, false);
				temperature_mode_index = -1;
				// update temperaturemode
				hlva.notifyDataSetChanged();
			} else {
				// set the select temperature
				int setTemperature = Integer
						.parseInt((String) temperatureList.get(mPosition).get(VIEW_TEMPERATURE_TEXT));
				((MainActivity) getActivity()).sentSetTemperature(setTemperature);
				temp_index = mPosition;
				if(pd==null){
					// there is a bug   some time the progressdialog wont show!!!!   fix it   onDestroy set pd=null  i dont know why,but it work
					pd = new ProgressDialog(getActivity());
					pd.setMessage("�����´�ָ����Ժ�...");
					pd.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								if(pd!=null){
									pd.dismiss();
								}
							}
							return false;
						}
					});
				}
				if(!pd.isShowing())
				{
					pd.show();
					// Stops sending after a pre-defined period.
					Message msg = new Message();
					msg.what = MSG_STOP_SEND;
					msg.arg1 = temp_index;
					mHandler.sendMessageDelayed(msg, SEND_PERIOD);
					
				}
			}
		}
	}
	
	
	public void setSelectTemperatureFromBT(){
		if(pd!=null){
			pd.dismiss();
		}
		if(temp_index!=-1){
			mHandler.removeMessages(MSG_STOP_SEND);
			temperature_mode_index = temp_index;
			// ���ã�ȷ�����ֻ��һ�ѡ��
			for (Map<String, Object> m : temperatureList) {
				m.put(VIEW_RADIO_BTN, false);
			}
			temperatureList.get(temp_index).put(VIEW_RADIO_BTN, true);
			
			hlva.notifyDataSetChanged();
			
			temp_index=-1;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private List<Map<String, Object>> getData() {
		return temperatureList;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();  
		pd=null;
	}
	
	
	
	
	
	/**
	 * to avoid IllegalStateException: No activity
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}
}