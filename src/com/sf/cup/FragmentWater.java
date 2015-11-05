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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FragmentWater extends Fragment {

	ListView temperatureListView;
	Button add_temperature_button;
	List<Map<String, Object>> temperatureList = new ArrayList<Map<String, Object>>();
	TemperatureListViewAdapter hlva;
	
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
		m.put("temperature_text", "22");
		m.put("radio_btn", false);
		temperatureList.add(m);
		m=new HashMap<String, Object>();
		m.put("info_text", "a2a");
		m.put("temperature_text", "40");
		m.put("radio_btn", true);
		temperatureList.add(m);
		
		hlva = new TemperatureListViewAdapter(this.getActivity(), temperatureList,
				R.layout.tab_water_select_item, new String[] { "info_text", "temperature_text", "radio_btn" },
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
						m.put("info_text", infoString.getText().toString());
						m.put("temperature_text", tempString.getText().toString());
						m.put("radio_btn", false);
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

		public TemperatureListViewAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			view.setOnClickListener(new MyListener(position));
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