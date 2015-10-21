package com.sf.cup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
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
	FragmentWater fWater;
	FragmentMe fMe;
	FragmentTime fTime;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		createFragment();
		mTab.add(fHome);
		mTab.add(fData);
		mTab.add(fTime);
		mTab.add(fWater);
		mTab.add(fMe);
		initView();
		//!!!!!!! 处理屏幕旋转生成多个fragment问题。
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.fragmentfield, fHome).commit();
		}
	}

	private void createFragment() {
		if (fData == null) {
			fData = new FragmentData();
		}
		if (fHome == null) {
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

	private void initView() {
		myTabRg = (RadioGroup) findViewById(R.id.tab_menu);
		myTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for (int i = 0; i < mRadioButton.length; i++) {
					if (mRadioButton[i] == checkedId) {
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						// ft.setCustomAnimations(R.animator.slide_left_in, R.animator.slide_left_out);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
						ft.replace(R.id.fragmentfield, mTab.get(i));
						// ft.addToBackStack(null);
						ft.commit();
						break;
					}
				}
			}
		});
	}
	
	
	public void onBackPressed()
	{
//	    long l = System.currentTimeMillis();
//	    if (l - lastTime > 2000L)
//	    {
//	      lastTime = l;
//	      Toast.makeText(this, "Press the back key again quit.", 0).show();
//	      return;
//	    }
	   
	    super.onBackPressed();
	    
	  }

}
