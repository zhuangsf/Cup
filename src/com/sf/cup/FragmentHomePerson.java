package com.sf.cup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sf.cup.utils.Utils;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FragmentHomePerson extends Fragment {
	private final static String TAG = FragmentHomePerson.class.getPackage().getName() + "."
			+ FragmentHomePerson.class.getSimpleName();
	
	ListView persionlist_view_pic;
	ListView persionlist_view1;
	ListView persionlist_view2;
	String[] list1Title;
	String[] list2Title;
	
	
	Handler mHandler = new Handler()
	  {
	    @Override
		public void handleMessage(Message paramAnonymousMessage)
	    {
	    Utils.Log("handle:"+paramAnonymousMessage);
	     switch (paramAnonymousMessage.what)
	     {
				case 1:
					break;
				case 2:
					break;
			}
	    }
	  };
	  
	  public static FragmentHomePerson newInstance(Bundle b){
		  FragmentHomePerson fd=new FragmentHomePerson();
			fd.setArguments(b);
			return fd;
		}
	  
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res =getResources();
        list1Title=res.getStringArray(R.array.person_list_title1);
        list2Title=res.getStringArray(R.array.person_list_title2);
        
        
        
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view=inflater.inflate(R.layout.tab_home_person_info, null);
    	persionlist_view1=(ListView) view.findViewById(R.id.persionlist_view1); 
    	PersonListViewAdapter1 hlva=new PersonListViewAdapter1(this.getActivity(), getData1(), R.layout.tab_home_list_item,
    			new String[]{"title","info","img"},
    			new int[]{R.id.title_text,R.id.info_text,R.id.right_img});
    	setHeight(hlva,persionlist_view1);
    	persionlist_view1.setAdapter(hlva);
    	
    	persionlist_view2=(ListView) view.findViewById(R.id.persionlist_view2); 
    	PersonListViewAdapter2 hlva2=new PersonListViewAdapter2(this.getActivity(), getData2(), R.layout.tab_home_list_item,
    			new String[]{"title","info","img"},
    			new int[]{R.id.title_text,R.id.info_text,R.id.right_img});
    	setHeight(hlva2,persionlist_view2);
    	persionlist_view2.setAdapter(hlva2);
    	
        return view;
    }
    private class PersonListViewAdapter1 extends SimpleAdapter{
		public PersonListViewAdapter1(Context context, List<Map<String, Object>> data, int resource, String[] from,int[] to) {
			super(context, data, resource, from, to);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=super.getView(position, convertView, parent);
			view.setOnClickListener(new MyListener1(position));
			return view;
		}
    	
    }
    private class MyListener1 implements OnClickListener{  
        int mPosition;  
        public MyListener1(int inPosition){  
            mPosition= inPosition;  
        }  
        @Override  
        public void onClick(View v) {  
//            Toast.makeText(FragmentHome.this.getActivity(),((TextView)v.findViewById(R.id.title_text)).getText()+""+mPosition, Toast.LENGTH_SHORT).show();
            if(0==mPosition){
            	FragmentTransaction ft=getActivity().getFragmentManager().beginTransaction();
            	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            	ft.add(R.id.fragmentfield, new FragmentHomeAbout());
            	ft.remove(FragmentHomePerson.this);
            	ft.addToBackStack(null);
				ft.commit();
            }else if (1==mPosition){
            	FragmentTransaction ft=getActivity().getFragmentManager().beginTransaction();
            	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            	ft.add(R.id.fragmentfield, new FragmentHomePairInfo());
            	ft.remove(FragmentHomePerson.this);
            	ft.addToBackStack(null);
				ft.commit();
            }else if(2==mPosition){
            	FragmentTransaction ft=getActivity().getFragmentManager().beginTransaction();
            	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            	ft.add(R.id.fragmentfield, new FragmentHomeReset());
            	ft.remove(FragmentHomePerson.this);
            	ft.addToBackStack(null);
				ft.commit();
            }
        }  
    } 
    
    private class PersonListViewAdapter2 extends SimpleAdapter{
		public PersonListViewAdapter2(Context context, List<Map<String, Object>> data, int resource, String[] from,int[] to) {
			super(context, data, resource, from, to);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=super.getView(position, convertView, parent);
			view.setOnClickListener(new MyListener2(position));
			return view;
		}
    	
    }
    private class MyListener2 implements OnClickListener{  
        int mPosition;  
        public MyListener2(int inPosition){  
            mPosition= inPosition;  
        }  
        @Override  
        public void onClick(View v) {  
            if(0==mPosition){
            	//go to personal info
            	FragmentTransaction ft=getActivity().getFragmentManager().beginTransaction();
            	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            	ft.add(R.id.fragmentfield, new FragmentHomeAbout());
            	ft.remove(FragmentHomePerson.this);
            	ft.addToBackStack(null);
				ft.commit();
            }
        }  
          
    }  
    
    
    
    
    
    
    
    public void setHeight(BaseAdapter comAdapter,ListView l){  
        int listViewHeight = 0;  
        int adaptCount = comAdapter.getCount();  
        Utils.Log("xxxxxxxxxxxxxxxxxx adaptCount:"+adaptCount);
        for(int i=0;i<adaptCount;i++){  
            View temp = comAdapter.getView(i,null,l);  
            temp.measure(0,0);  
            listViewHeight += temp.getMeasuredHeight(); 
            Utils.Log("xxxxxxxxxxxxxxxxxx listViewHeight:"+listViewHeight);
        }  
        LayoutParams layoutParams = l.getLayoutParams();  
        layoutParams.width = LayoutParams.FILL_PARENT;  
        layoutParams.height = listViewHeight+2;  
        l.setLayoutParams(layoutParams);  
    }  
    
	private List<Map<String, Object>> getData1() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		SharedPreferences p=Utils.getSharedPpreference(getActivity());
		for (int i = 0; i < list1Title.length; i++) {
			map = new HashMap<String, Object>();
			map.put("title", list1Title[i]);
			String info=p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[i], "");
			map.put("info", info);
			map.put("img", ">");
			list.add(map);
		}

       
        return list;
    }
	
	private List<Map<String, Object>> getData2() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		SharedPreferences p=Utils.getSharedPpreference(getActivity());
		for (int i = 0; i < list2Title.length; i++) {
			map = new HashMap<String, Object>();
			map.put("title", list2Title[i]);
			String info=p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[i], "");
			map.put("info",info);
			map.put("img", ">");
			list.add(map);
		}

       
        return list;
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