package com.sf.cup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FragmentHomeAbout extends FragmentPack {

	Button goBackButton;
	ListView about_list_view;
	String[] listTitle;
	private static final int GO_WEB_INDEX = 0;
	private static final int GO_BUY_INDEX = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_home_about, null);
		Resources res = getResources();
		listTitle = res.getStringArray(R.array.home_about_list_title);
		about_list_view = (ListView) v.findViewById(R.id.about_list_view);
		AboutListViewAdapter alva = new AboutListViewAdapter(this.getActivity(), getData(), R.layout.tab_home_list_item,
				new String[] { "title", "info", "img" }, new int[] { R.id.title_text, R.id.info_text, R.id.right_img });
		setHeight(alva, about_list_view);
		about_list_view.setAdapter(alva);

		return v;
	}

	private class AboutListViewAdapter extends SimpleAdapter {
		public AboutListViewAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from,
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
			if (GO_WEB_INDEX == mPosition) {
				Uri uri = Uri.parse("http://www.8amcup.com");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			} else if (GO_BUY_INDEX == mPosition) {
				Uri uri = Uri.parse("https://shop152288103.taobao.com");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		}

	}

	public void setHeight(BaseAdapter comAdapter, ListView l) {
		int listViewHeight = 0;
		int adaptCount = comAdapter.getCount();
		for (int i = 0; i < adaptCount; i++) {
			View temp = comAdapter.getView(i, null, l);
			temp.measure(0, 0);
			listViewHeight += temp.getMeasuredHeight();
		}
		LayoutParams layoutParams = l.getLayoutParams();
		layoutParams.width = LayoutParams.FILL_PARENT;
		layoutParams.height = listViewHeight + 4;
		l.setLayoutParams(layoutParams);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		for (int i = 0; i < listTitle.length; i++) {
			map = new HashMap<String, Object>();
			map.put("title", listTitle[i]);
			map.put("info", "");
			map.put("img", ">");
			list.add(map);
		}

		return list;
	}

	
	
	
	
	
	
	
	
	
	
	
	public static FragmentHomeAbout newInstance(Bundle b) {
		FragmentHomeAbout fd = new FragmentHomeAbout();
		fd.setArguments(b);
		return fd;
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
	
	
	@Override
	protected String getPageName() {
		return FragmentHomeAbout.class.getName();
	}
}