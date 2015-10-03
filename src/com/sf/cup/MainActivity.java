package com.sf.cup;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity {
	final int RIGHT = 0;
	final int LEFT = 1;
	RadioGroup myTabRg;
	RadioButton myTabRadioButton;
	FragmentHome fHome;
	FragmentData fData;
	FragmentWater fWater;
	FragmentMe fMe;
	FragmentTime fTime;
	
	 private ViewPager viewPager; 
	 /*每个 tab 的 item*/
	private List<Fragment> mTab = new ArrayList<Fragment>() ;
	
	private int [] mRadioButton={
			R.id.rbHome,
			R.id.rbData,
			R.id.rbTime,
			R.id.rbWater,
			R.id.rbMe,
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewPager = (ViewPager) findViewById(R.id.id_view_pager);
		createFragment();
		mTab.add(fHome);
		mTab.add(fData);
		mTab.add(fTime);
		mTab.add(fWater);
		mTab.add(fMe);
		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		viewPager.setOnPageChangeListener(new ViewPageChangeListener());
		
		initView();

	}
	 private class ViewPagerAdapter extends FragmentPagerAdapter{

	        public ViewPagerAdapter(FragmentManager fm) {
	            super(fm);
	        }

	        @Override
	        public Fragment getItem(int position) {
	            return mTab.get(position);
	        }

	        @Override
	        public int getCount() {
	            return mTab.size();
	        }
	    }
	 private  class ViewPageChangeListener implements OnPageChangeListener {  
	        @Override  
	        public void onPageScrollStateChanged(int arg0) {  
	        }  
	        @Override  
	        public void onPageScrolled(int arg0, float arg1, int arg2) {  
	        }  
	        @Override  
	        public void onPageSelected(int index) {  
	            	    myTabRadioButton = (RadioButton) findViewById(mRadioButton[index]);
		  	            myTabRadioButton.setChecked(true);
	        }  
	    }  

	private void createFragment(){
		if (fData == null) {
			fData = new FragmentData();
		}
		if(fHome == null){
			fHome = new FragmentHome();
		}
		if (fTime == null) {
			fTime = new FragmentTime();
		}
		if (fWater == null) {
			fWater = new FragmentWater();
		}
		if (fMe == null) {
			fMe = new FragmentMe();
		}
	}
	
	public void initView() {
		myTabRg = (RadioGroup) findViewById(R.id.tab_menu);
		myTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for(int i=0;i<mRadioButton.length;i++){
					if(mRadioButton[i]==checkedId){
						viewPager.setCurrentItem(i, false);
						break;
					}
				}
			}
		});
	}

}
