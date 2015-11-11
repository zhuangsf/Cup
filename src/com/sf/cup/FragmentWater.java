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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

public class FragmentWater extends Fragment {

	ListView temperatureListView; 
	Button add_temperature_button;
	Button cancel_temperature_button;
	List<Map<String, Object>> temperatureList = new ArrayList<Map<String, Object>>(); //list view 就是一直玩弄这个
	TemperatureListViewAdapter hlva;
	
	private static final String VIEW_INFO_TEXT="info_text";
	private static final String VIEW_TEMPERATURE_TEXT="temperature_text";
	private static final String VIEW_RADIO_BTN="radio_btn";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		add_temperature_button = (Button) v.findViewById(R.id.add_temperature_button);
		add_temperature_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View layout = inflater.inflate(R.layout.tab_water_select_dialog,
						(ViewGroup) v.findViewById(R.id.dialog));
				AlertDialog ad = new AlertDialog.Builder(getActivity())
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText infoString = (EditText) layout.findViewById(R.id.info_input);
						EditText tempString = (EditText) layout.findViewById(R.id.temp_input);
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
				
				
//				Map<String, Object> m=new HashMap<String, Object>();
//				m.put("info_text", "1");
//				m.put("temperature_text", "2");
//				temperatureList.remove(0);
//				doUpdate();
				// TODO 这里有待验证是否能正确弹出输入窗口
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
		
		return v;
	}
	private void doUpdate(){
		if(hlva!=null){
			Utils.Log("update listview");
			hlva.notifyDataSetChanged();
			setHeight(hlva, temperatureListView);
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
		Utils.Log("xxxxxxxxxxxxxxxxxx adaptCount:" + adaptCount);
		for (int i = 0; i < adaptCount; i++) {
			View temp = comAdapter.getView(i, null, l);
			temp.measure(0, 0);
			listViewHeight += temp.getMeasuredHeight();
			Utils.Log("xxxxxxxxxxxxxxxxxx listViewHeight:" + listViewHeight);
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
			
			
			return view;
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