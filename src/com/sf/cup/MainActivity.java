package com.sf.cup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sf.cup.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
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
//	FragmentData fData;
	FragmentTime fTime;
	FragmentWater fWater;
//	FragmentMe fMe;
	private static final String TAG_HOME="TAG_HOME";
	private static final String TAG_DATA="TAG_DATA";
	private static final String TAG_TIME="TAG_TIME";
	private static final String TAG_WATER="TAG_WATER";
	private static final String TAG_ME="TAG_ME";

	FragmentHomeReset fHome_reset;

	/* 姣忎釜 tab 鐨� item */
	private List<Fragment> mTab = new ArrayList<Fragment>();

	private int[] mRadioButton = {
			R.id.rbTime,
			R.id.rbWater,
			R.id.rbHome, 
			};
	private Fragment[] mFragmentArray = { 
			fTime,
			fWater,
			fHome, 
			};
	private String[] mFragmentTag = { 
			TAG_TIME,
			TAG_WATER,
			TAG_HOME, 
			};


	
	
	private MediaPlayer mp;  
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		createFragment();
		
		initView();
		
		//!!!!!!! 处理屏幕旋转生成多个fragment问题。
		//!!!!!!! 如果用replace是要加这个，判断是否是activity重建。这里是由于在RadioGroup 的状态改变，默认为第一个页签时没进onCheckedChanged，不为第一个页签 都会进onCheckedChanged切换下页签
//		if (savedInstanceState == null)
		{

			Utils.Log("xxxxxxxxxxxxxxxxxx onCreate home:"+fWater);
			getFragmentManager().beginTransaction().show(fWater).commit();
		}
		
		
		// if from alarm show the dialog
		startFromAlarm(getIntent());
		
		
	
	}

	private void createFragment() {
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft= fm.beginTransaction();
		Fragment fragment =null;
		
		
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
		
		fragment= fm.findFragmentByTag(TAG_HOME);
		if (fragment != null) {// 如果有，则使用，处理翻转之后状态未保存问题
			fHome = (FragmentHome) fragment;
		} else {// 如果为空，才去新建，不新建切换的时候就可以保存状态了。
			fHome = FragmentHome.newInstance(null);
			ft.add(R.id.fragmentfield, fHome, TAG_HOME);
		}
		ft.hide(fHome);
		mTab.add(fHome);
		
		
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
						Utils.Log("xxxxxxxxxxxxxxxxxx i:"+i);
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
	
	
	
	
//	onNewIntent(Intent intent)方法里调用setIntent(intent)设置这个传来的最新的intent.
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		startFromAlarm(intent);
	}
	
	private void startFromAlarm(Intent intent){
		boolean isAlarm=intent.getBooleanExtra(Utils.IS_FROM_ALARM, false);
		int index=intent.getIntExtra(Utils.FROM_ALARM_INDEX,-1);
		Utils.Log("isAlarm:"+ isAlarm+" index:"+index);
		if(isAlarm){
		//如果是闹钟响起跳转来的，播个音乐
		 // 初始化音乐资源  
       try {  
       	
       	new AlertDialog.Builder(this)
			.setMessage("亲！已到设定饮水时间咯！\n请及时享用哦")
			.setPositiveButton("确定", null)
			.create()
			.show();
       	
           // 创建MediaPlayer对象  
           mp = new MediaPlayer();  
           // 将音乐保存在res/raw/xingshu.mp3,R.java中自动生成{public static final int xingshu=0x7f040000;}  
           Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  
//           mp = MediaPlayer.create(this, notification);  
           mp.setDataSource(this,notification);
           // 在MediaPlayer取得播放资源与stop()之后要准备PlayBack的状态前一定要使用MediaPlayer.prepeare()  
           mp.prepare();  
           // 开始播放音乐  
           mp.start();  
           // 音乐播放完毕的事件处理  
           mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
               public void onCompletion(MediaPlayer mp) {  
                   // 循环播放  
                   try {  
//                       mp.start();  
                   } catch (IllegalStateException e) {  
                       e.printStackTrace();  
                   }  
               }  
           });  
           // 播放音乐时发生错误的事件处理  
           mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
               public boolean onError(MediaPlayer mp, int what, int extra) {  
                   // 释放资源  
                   try {  
                       mp.release();  
                   } catch (Exception e) {  
                       e.printStackTrace();  
                   }  
                   return false;  
               }  
           });  
       } catch (IllegalStateException e) {  
           e.printStackTrace();  
       } catch (IOException e) {  
           e.printStackTrace();  
       }  
		}
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
