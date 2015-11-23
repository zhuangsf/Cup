package com.sf.cup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.sf.cup.utils.Utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FragmentHomePerson extends Fragment {
	private final static String TAG = FragmentHomePerson.class.getPackage().getName() + "."
			+ FragmentHomePerson.class.getSimpleName();
	
	ListView personlist_view_pic;
	ListView personlist_view1;
	ListView personlist_view2;
	String[] list1Title;
	String[] list2Title;
	PersonListViewAdapter1 hlva1;
	PersonListViewAdapter2 hlva2;
	
	List<Map<String, Object>> personList1 = new ArrayList<Map<String, Object>>(); //list view 就是一直玩弄这个
	List<Map<String, Object>> personList2 = new ArrayList<Map<String, Object>>(); //list view 就是一直玩弄这个
	
	EditText person_info;
	Handler mHandler = new Handler()
	  {
	    @Override
		public void handleMessage(Message paramAnonymousMessage)
	    {
	    Utils.Log("handle:"+paramAnonymousMessage);
	     switch (paramAnonymousMessage.what)
	     {
				case 1:
					//alertdialog with edittext cant not open im.  
					try {
						Thread.sleep(200);
						person_info.dispatchTouchEvent( MotionEvent.obtain( SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, person_info.getRight(), person_info.getRight() + 5, 0));
						person_info.dispatchTouchEvent( MotionEvent.obtain( SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, person_info.getRight(), person_info.getRight() + 5, 0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
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
    	
    	initList1();
    	initList2();
    	
    	personlist_view1=(ListView) view.findViewById(R.id.persionlist_view1); 
    	hlva1=new PersonListViewAdapter1(this.getActivity(), getData1(), R.layout.tab_home_list_item,
    			new String[]{"title","info","img"},
    			new int[]{R.id.title_text,R.id.info_text,R.id.right_img});
    	setHeight(hlva1,personlist_view1);
    	personlist_view1.setAdapter(hlva1);
    	
    	personlist_view2=(ListView) view.findViewById(R.id.persionlist_view2); 
    	hlva2=new PersonListViewAdapter2(this.getActivity(), getData2(), R.layout.tab_home_list_item,
    			new String[]{"title","info","img"},
    			new int[]{R.id.title_text,R.id.info_text,R.id.right_img});
    	setHeight(hlva2,personlist_view2);
    	personlist_view2.setAdapter(hlva2);
    	
    	
    	
    	
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
			// Toast.makeText(FragmentHome.this.getActivity(),((TextView)v.findViewById(R.id.title_text)).getText()+""+mPosition,
			// Toast.LENGTH_SHORT).show();
			
			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View layout = inflater.inflate(R.layout.tab_home_person_dialog,
					(ViewGroup) v.findViewById(R.id.dialog));
			TextView person_title = (TextView) layout.findViewById(R.id.person_title);
			person_info = (EditText) layout.findViewById(R.id.person_info);
			if (0 == mPosition) {
			} else if (1 == mPosition) {
			} else if (2 == mPosition) {
			}
			person_title.setText(list1Title[mPosition]);
			final AlertDialog ad = new AlertDialog.Builder(getActivity())
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							personList1.get(mPosition).put("info", person_info.getText().toString());
							SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
							e.putString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[mPosition], person_info.getText().toString());
							e.commit();
							doUpdate1();
						}
					}).setNegativeButton("取消", null).create();

			ad.setTitle("个人信息设定");
			ad.setView(layout);
			ad.show();
			Message msg=new Message();
			msg.what=1;
			msg.arg1=1;
			mHandler.sendMessage(msg);
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
        	LayoutInflater inflater = getActivity().getLayoutInflater();
			final View layout = inflater.inflate(R.layout.tab_home_person_dialog,
					(ViewGroup) v.findViewById(R.id.dialog));
			TextView person_title = (TextView) layout.findViewById(R.id.person_title);
			person_info = (EditText) layout.findViewById(R.id.person_info);
			if (0 == mPosition) {
			} else if (1 == mPosition) {
			} else if (2 == mPosition) {
			}
			person_title.setText(list2Title[mPosition]);
			final AlertDialog ad = new AlertDialog.Builder(getActivity())
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							personList2.get(mPosition).put("info", person_info.getText().toString());
							SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
							e.putString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[mPosition], person_info.getText().toString());
							e.commit();
							doUpdate2();
						}
					}).setNegativeButton("取消", null).create();

			ad.setTitle("个人信息设定");
			ad.setView(layout);
			ad.show();
			Message msg=new Message();
			msg.what=1;
			msg.arg1=1;
			mHandler.sendMessage(msg);
        }  
          
    }  
    
    
    
    
    
    
    
    public void setHeight(BaseAdapter comAdapter,ListView l){  
        int listViewHeight = 0;  
        int adaptCount = comAdapter.getCount();  
        for(int i=0;i<adaptCount;i++){  
            View temp = comAdapter.getView(i,null,l);  
            temp.measure(0,0);  
            listViewHeight += temp.getMeasuredHeight(); 
        }  
        LayoutParams layoutParams = l.getLayoutParams();  
        layoutParams.width = LayoutParams.FILL_PARENT;  
        layoutParams.height = listViewHeight+2;  
        l.setLayoutParams(layoutParams);  
    }  
    
	private List<Map<String, Object>> getData1() {
		return personList1;
    }
	private void initList1(){
		Map<String, Object> map;
		SharedPreferences p=Utils.getSharedPpreference(getActivity());
		for (int i = 0; i < list1Title.length; i++) {
			map = new HashMap<String, Object>();
			map.put("title", list1Title[i]);
			String info=p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[i], "");
			map.put("info", info);
			map.put("img", ">");
			personList1.add(map);
		}
	}
	private List<Map<String, Object>> getData2() {
		return personList2;
    }
	private void initList2() {
		Map<String, Object> map;
		SharedPreferences p=Utils.getSharedPpreference(getActivity());
		for (int i = 0; i < list2Title.length; i++) {
			map = new HashMap<String, Object>();
			map.put("title", list2Title[i]);
			String info=p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[i], "");
			map.put("info",info);
			map.put("img", ">");
			personList2.add(map);
		}
    }
    
	
	private void doUpdate1() {
		if (hlva1 != null) {
			 Utils.Log("doUpdate1:"+personList1);
			hlva1.notifyDataSetChanged();
		}
	}
	private void doUpdate2() {
		if (hlva2 != null) {
			hlva2.notifyDataSetChanged();
		}
	}
    
	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences p=Utils.getSharedPpreference(getActivity());
		JSONObject result=new JSONObject();
		String nickname =p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[0], "");
		String sex =p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[1], "");
		String phone=p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[2], "");
		
		String scene =p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[0], "");
		String constitution =p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[1], "");
		String height =p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[2], "");
		String weight =p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[3], "");
		String birthday =p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[4], "");
		String avatar ="";

	   
		//send to server
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