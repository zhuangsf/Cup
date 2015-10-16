package com.sf.cup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.sf.cup.utils.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentHome extends Fragment {
	private final static String TAG = FragmentHome.class.getPackage().getName() + "."
			+ FragmentHome.class.getSimpleName();
	
	Button buttonGet;
	TextView textViewGet;
	ListView homeListView;
	String[] listTitle;
	
	Handler mHandler = new Handler()
	  {
	    @Override
		public void handleMessage(Message paramAnonymousMessage)
	    {
	    Log.i("xxxxxxxxxxxxxxxxxxx", "handle:"+paramAnonymousMessage);
	     switch (paramAnonymousMessage.what)
	     {
				case 1:
					Log.i("xxxxxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxSG_WAT_START-1---:"+paramAnonymousMessage.obj);
					textViewGet.setText(paramAnonymousMessage.obj.toString());
					break;
				case 2:
					break;
			}
	    }
	  };
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res =getResources();
        listTitle=res.getStringArray(R.array.home_list_title);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view=inflater.inflate(R.layout.tab_home, null);
    	buttonGet=((Button)view.findViewById(R.id.getButton));
    	textViewGet=((TextView)view.findViewById(R.id.getText));
    	homeListView=(ListView) view.findViewById(R.id.homeListView); 
    	HomeListViewAdapter hlva=new HomeListViewAdapter(this.getActivity(), getData(), R.layout.tab_home_list_item,
    			new String[]{"title","info","img"},
    			new int[]{R.id.title_text,R.id.info_text,R.id.right_img});
    	setHeight(hlva);
    	homeListView.setAdapter(hlva);
    	
    	buttonGet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						httpGet("http://121.204.243.79:8080/Cup/user/jsonfeed");
					}
				}).start();
			}
		});
    	
        return view;
    }
    private class HomeListViewAdapter extends SimpleAdapter{

		public HomeListViewAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from,int[] to) {
			super(context, data, resource, from, to);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=super.getView(position, convertView, parent);
			view.setOnClickListener(new MyListener(position));
			return view;
		}
    	
    }
    private class MyListener implements OnClickListener{  
        int mPosition;  
        public MyListener(int inPosition){  
            mPosition= inPosition;  
        }  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub  
            Toast.makeText(FragmentHome.this.getActivity(),((TextView)v.findViewById(R.id.title_text)).getText()+""+mPosition, Toast.LENGTH_SHORT).show();
            
        }  
          
    }  
    public void setHeight(BaseAdapter comAdapter){  
        int listViewHeight = 0;  
        int adaptCount = comAdapter.getCount();  
        Log.i("xxxxxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxxxx adaptCount:"+adaptCount);
        for(int i=0;i<adaptCount;i++){  
            View temp = comAdapter.getView(i,null,homeListView);  
            temp.measure(0,0);  
            listViewHeight += temp.getMeasuredHeight(); 
            Log.i("xxxxxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxxxx listViewHeight:"+listViewHeight);
        }  
        LayoutParams layoutParams = this.homeListView.getLayoutParams();  
        layoutParams.width = LayoutParams.FILL_PARENT;  
        layoutParams.height = listViewHeight+20;  
        homeListView.setLayoutParams(layoutParams);  
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
    
    private void httpGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpClient httpClinet = new DefaultHttpClient();
			HttpResponse httpResponse = httpClinet.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				Utils.Log(" httpGet status " + httpResponse.getStatusLine());
				Utils.Log(" xxxxxxxxxxxxxxxxxxxxx http httpGet start output 2");
				String result=EntityUtils.toString(entity, "UTF-8");
				// 下面这种方式写法更简单，可是没换行。
				Utils.Log("httpGet 2" + result);
				// 生成 JSON 对象
//				JSONArray jsonArray= new JSONArray(result);
				JSONObject jsonObject=new JSONObject(result);
				Message msg=new Message();
				msg.what=1;
				msg.arg1=1;
				msg.obj=jsonObject;
//				mHandler.sendEmptyMessage(1);
				mHandler.sendMessage(msg);
				Utils.Log(" xxxxxxxxxxxxxxxxxxxxx http httpGet finish output 2"+jsonObject);
			}
		} catch (Exception e) {
			Utils.Log(TAG, "httpGet error:" + e);
		}
	}
 
}