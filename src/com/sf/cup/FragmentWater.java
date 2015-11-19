package com.sf.cup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sf.cup.utils.Utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FragmentWater extends Fragment {

	ListView temperatureListView; 
	ImageView add_temperature_button;
	Button cancel_temperature_button;
	List<Map<String, Object>> temperatureList = new ArrayList<Map<String, Object>>(); //list view 就是一直玩弄这个
	TemperatureListViewAdapter hlva;
	
	FrameLayout temperature_mode;
	View maskView;
	LinearLayout temperature_setting;
	boolean temperature_mode_enable=false;
	int temperature_setting_value=55;
	
	private static final String VIEW_INFO_TEXT="info_text";
	private static final String VIEW_TEMPERATURE_TEXT="temperature_text";
	private static final String VIEW_RADIO_BTN="radio_btn";
	
	
	TextView water_status_text1;
	TextView water_status_text2;
	TextView water_status_text3;
	ImageView water_status_pic1;
	ImageView water_status_pic2;
	ImageView water_status_pic3;
	
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
		
		Map<String, Object> m=new HashMap<String, Object>();
		m.put("info_text", "aa");
		m.put(VIEW_TEMPERATURE_TEXT, "22");
		m.put(VIEW_RADIO_BTN, false);
		temperatureList.add(m);
		m=new HashMap<String, Object>();
		m.put(VIEW_INFO_TEXT, "a2a");
		m.put(VIEW_TEMPERATURE_TEXT, "40");
		m.put(VIEW_RADIO_BTN, true);
		temperatureList.add(m);
		
		hlva = new TemperatureListViewAdapter(this.getActivity(), temperatureList,
				R.layout.tab_water_select_item, new String[] { VIEW_INFO_TEXT, VIEW_TEMPERATURE_TEXT, VIEW_RADIO_BTN },
				new int[] { R.id.info_text, R.id.temperature_text, R.id.radio_btn });
		temperatureListView.setAdapter(hlva);
		setHeight(hlva, temperatureListView);

		add_temperature_button = (ImageView) v.findViewById(R.id.add_temperature_button);
		add_temperature_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View layout = inflater.inflate(R.layout.tab_water_select_dialog,
						(ViewGroup) v.findViewById(R.id.dialog));
				final EditText infoString = (EditText) layout.findViewById(R.id.info_input);
				final EditText tempString = (EditText) layout.findViewById(R.id.temp_input);
				
				final AlertDialog ad = new AlertDialog.Builder(getActivity())
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Map<String, Object> m=new HashMap<String, Object>();
						m.put(VIEW_INFO_TEXT, infoString.getText().toString());
						m.put(VIEW_TEMPERATURE_TEXT, tempString.getText().toString());
						m.put(VIEW_RADIO_BTN, false);
						temperatureList.add(m);
						Utils.Log("xxxxxxxxxxxxxxxxxx temperatureList:" + temperatureList);
						doUpdate();
					}

				}).setNegativeButton("取消", null).create();
				
				ad.setTitle("温度模式设定");
				ad.setView(layout);
				ad.show();
				
				try {  
				    Field mAlert = AlertDialog.class.getDeclaredField("mAlert");  
				    mAlert.setAccessible(true);  
				    Object alertController = mAlert.get(ad);  
				  
				    Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");  
				    mTitleView.setAccessible(true);  
				  
				    TextView title = (TextView) mTitleView.get(alertController);  
				    title.setTextColor(0xffff0022);   
				    title.setGravity(Gravity.CENTER);
				  
				} catch (NoSuchFieldException e) {  
				    e.printStackTrace();  
				} catch (IllegalArgumentException e) {  
				    e.printStackTrace();  
				} catch (IllegalAccessException e) {  
				    e.printStackTrace();  
				}  
				
				
				
				
				Button adPosiButton=ad.getButton(AlertDialog.BUTTON_POSITIVE);
				adPosiButton.setEnabled(false);
				adPosiButton.setBackground(getActivity().getResources().getDrawable(R.drawable.long_button_selector));
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
				adNegaButton.setLayoutParams(lp2);
				
				tempString.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						Utils.Log("xxxxxxxxxxxxxxxxxx onTextChanged:" + s);
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						Utils.Log("xxxxxxxxxxxxxxxxxx beforeTextChanged start:" + start+" count:"+count+" after:"+after);
					}
					@Override
					public void afterTextChanged(Editable s) {
						Utils.Log("xxxxxxxxxxxxxxxxxx afterTextChanged:" + s);
						ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						if(s!=null&&!"".equals(s)){
							int a=Integer.parseInt(s.toString());
							if(a<=80&&a>=20){
								ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
							}
							
						}
					}
				});
				
//				Map<String, Object> m=new HashMap<String, Object>();
//				m.put("info_text", "1");
//				m.put("temperature_text", "2");
//				temperatureList.remove(0);
//				doUpdate();
				// layout.post(new Runnable() {
				// @Override
				// public void run() {
				// InputMethodManager im = (InputMethodManager)
				// getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				// im.showSoftInput(layout, InputMethodManager.SHOW_FORCED);
				// }
				// });
			}
		});

		
		
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
		
		
		updateUiShow();// create first time
		return v;
	}
	
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
		View v1 = (View) v.findViewById(R.id.temperature_value1);
		View v2 = (View) v.findViewById(R.id.temperature_value2);
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
		v1.setBackground(bgDrawable1);
		v2.setBackground(bgDrawable2);

	}
	private int getSettingValue(){
		if(temperature_setting_value<10||temperature_setting_value>99){
			temperature_setting_value=55;
		}
		//get temperature which select mode define 
		
		return temperature_setting_value;
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
	
	
	
	
	private void doUpdate(){
		if(hlva!=null){
			Utils.Log("update listview");
			hlva.notifyDataSetChanged();
			setHeight(hlva, temperatureListView);
			
			
			Utils.Log("xxxxxxxxxxxxxxxxxx count:"+temperatureList.size());
			if(temperatureList.size()>=5)
			{
				add_temperature_button.setEnabled(false);
			}else{
				add_temperature_button.setEnabled(true);
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
		layoutParams.height = listViewHeight + 2;
		l.setLayoutParams(layoutParams);
	}

	private class TemperatureListViewAdapter extends SimpleAdapter {
		float downX=0f;
		float upX=0f;
		// 用于记录每个RadioButton的状态，并保证只可选一个
		 HashMap<String, Boolean> states = new HashMap<String, Boolean>();
		  
		public TemperatureListViewAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			view.setOnClickListener(new MyListener(position));
			
			final int p=position;
			final RadioButton radio=(RadioButton) view.findViewById(R.id.radio_btn);  
			radio.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					 // 重置，确保最多只有一项被选中
			        for (String key : states.keySet()) {
			          states.put(key, false);
			        }
			        states.put(String.valueOf(p), radio.isChecked());
			        for(Map<String, Object> m :temperatureList){
						m.put(VIEW_RADIO_BTN,false);
					}
			        temperatureList.get(p).put(VIEW_RADIO_BTN, true);
			        TemperatureListViewAdapter.this.notifyDataSetChanged();
			        
			        
				}
			});
			
			ImageView delete_model=(ImageView)view.findViewById(R.id.delete_model);
			delete_model.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
			        temperatureList.remove(p);
			        TemperatureListViewAdapter.this.notifyDataSetChanged();
			        //better to show a confirm dialog
			        
			        
				}
			});
			
			/*
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction())//根据动作来执行代码     
                    {    
                    case MotionEvent.ACTION_MOVE://滑动     
                    	int moveX = (int) event.getX(); 
                    	int deltaX=(int)(downX-moveX);
                    	if(deltaX<40&&deltaX>0){
                    		Utils.Log("xxxxxxxxxxxxxxxxxx deltaX:" + deltaX);
                    		v.scrollBy(deltaX, 0);
                    	}
                        break;    
                    case MotionEvent.ACTION_DOWN://按下     
                        downX = event.getX();  
                        break;    
                    case MotionEvent.ACTION_UP://松开     
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
			super.notifyDataSetChanged();
			//TODO
			//0 ,show a waiting dialog
			
			//1, send request to cup
			
			//2,  update the mode
			
			//3, save status to perferrence
		}
		
		

	}

	private class MyListener implements OnClickListener {
		int mPosition;

		public MyListener(int inPosition) {
			mPosition = inPosition;
		}

		@Override
		public void onClick(View v) {

		}

	}

	private List<Map<String, Object>> getData() {
		return temperatureList;
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