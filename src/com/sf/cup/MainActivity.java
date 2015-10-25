package com.sf.cup;

import java.util.ArrayList;
import java.util.List;

import com.sf.cup.utils.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	long lastTime = 0L;
	RadioGroup myTabRg;
	RadioButton myTabRadioButton;
	FragmentHome fHome;
	FragmentData fData;
	FragmentTime fTime;
	FragmentWater fWater;
	FragmentMe fMe;
	private static final String TAG_HOME="TAG_HOME";
	private static final String TAG_DATA="TAG_DATA";
	private static final String TAG_TIME="TAG_TIME";
	private static final String TAG_WATER="TAG_WATER";
	private static final String TAG_ME="TAG_ME";

	FragmentHomeReset fHome_reset;

	/* 姣忎釜 tab 鐨� item */
	private List<Fragment> mTab = new ArrayList<Fragment>();

	private int[] mRadioButton = {
			R.id.rbHome,
			R.id.rbData, 
			R.id.rbTime,
			R.id.rbWater,
			R.id.rbMe, 
			};
	private Fragment[] mFragmentArray = { 
			fHome,
			fData, 
			fTime,
			fWater,
			fMe, 
			};
	private String[] mFragmentTag = { 
			TAG_HOME,
			TAG_DATA, 
			TAG_TIME,
			TAG_WATER,
			TAG_ME, 
			};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		createFragment();
		
		initView();
		
		//!!!!!!! 处理屏幕旋转生成多个fragment问题。
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().show(fHome).commit();
		}
	}

	private void createFragment() {
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft= fm.beginTransaction();
		Fragment fragment =null;
		
		fragment= fm.findFragmentByTag(TAG_HOME);
		if (fragment != null) {// 如果有，则使用，处理翻转之后状态未保存问题
			fHome = (FragmentHome) fragment;
		} else {// 如果为空，才去新建，不新建切换的时候就可以保存状态了。
			fHome = FragmentHome.newInstance(null);
			ft.add(R.id.fragmentfield, fHome, TAG_HOME);
		}
		ft.hide(fHome);
		mTab.add(fHome);
		
		fragment= fm.findFragmentByTag(TAG_DATA);
		if (fragment != null) {// 如果有，则使用，处理翻转之后状态未保存问题
			fData = (FragmentData) fragment;
		} else {// 如果为空，才去新建，不新建切换的时候就可以保存状态了。
			fData = FragmentData.newInstance(null);
			ft.add(R.id.fragmentfield, fData, TAG_DATA);
		}
		ft.hide(fData);
		mTab.add(fData);
		
		fragment= fm.findFragmentByTag(TAG_TIME);
		if (fragment != null) {// 如果有，则使用，处理翻转之后状态未保存问题
			fTime = (FragmentTime) fragment;
		} else {// 如果为空，才去新建，不新建切换的时候就可以保存状态了。
			fTime = FragmentTime.newInstance(null);
			ft.add(R.id.fragmentfield, fTime, TAG_TIME);
		}
		ft.hide(fTime);
		mTab.add(fTime);
		
		fragment= fm.findFragmentByTag(TAG_WATER);
		if (fragment != null) {// 如果有，则使用，处理翻转之后状态未保存问题
			fWater = (FragmentWater) fragment;
		} else {// 如果为空，才去新建，不新建切换的时候就可以保存状态了。
			fWater = FragmentWater.newInstance(null);
			ft.add(R.id.fragmentfield, fWater, TAG_WATER);
		}
		ft.hide(fWater);
		mTab.add(fWater);
		
		fragment= fm.findFragmentByTag(TAG_ME);
		if (fragment != null) {// 如果有，则使用，处理翻转之后状态未保存问题
			fMe = (FragmentMe) fragment;
		} else {// 如果为空，才去新建，不新建切换的时候就可以保存状态了。
			fMe = FragmentMe.newInstance(null);
			ft.add(R.id.fragmentfield, fMe, TAG_ME);
		}
		ft.hide(fMe);
		mTab.add(fMe);
		
		
		//处理其他fragment
		fragment=fm.findFragmentById(R.id.fragmentfield);
		if(mTab!=null&&fragment!=null&&!mTab.contains(fragment) && fragment.isAdded()){
			ft.remove(fragment);
			int count=fm.getBackStackEntryCount();
			Utils.Log("fragment backstack count:"+count);
			for(int i=0;i<count;i++){
			fm.popBackStack();//切换也签，处理掉已有的backstack
			}
		}
		
		ft.commit();
		
	}
	
	
	private void initView() {
		myTabRg = (RadioGroup) findViewById(R.id.tab_menu);
		myTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				createFragment();
				for (int i = 0; i < mRadioButton.length; i++) {
					if (mRadioButton[i] == checkedId) {
						FragmentManager fm=getFragmentManager();
						
						FragmentTransaction ft = fm.beginTransaction();
						// ft.setCustomAnimations(R.animator.slide_left_in, R.animator.slide_left_out);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//						ft.replace(R.id.fragmentfield, mTab.get(i),mFragmentTag[i]);
						ft.show( mTab.get(i));
//						 ft.addToBackStack(null);
						ft.commit();
						break;
					}
				}
			}
		});
	}
	
	
	public void onBackPressed()
	{
	    int count=getFragmentManager().getBackStackEntryCount();
		if(count==0){
			long l = System.currentTimeMillis();
	    if (l - lastTime > 2000L)
	    {
	      lastTime = l;
	      Toast.makeText(this, "Press the back key again quit.", 0).show();
	      return;
	    }
		}
	   
		super.onBackPressed();
	    
	  }

}
