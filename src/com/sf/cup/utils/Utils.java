package com.sf.cup.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class Utils {
	private final static boolean isDebug = true;
	private final static String TAG = Utils.class.getPackage() + "."
			+ Utils.class.getSimpleName();
	public final static String APK_NAME_SUFFIX = "apk";
	public final static String SEPARATOR_DOT = ".";
	public final static String SEPARATOR_SLASH = "/";
	public final static String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public final static String URL_PATH="http://121.199.75.79:8180/cup-0.1/";
	public static final int TARGET_ACTIVITY = 0;
	public static final int TARGET_SERVICE = 1;
	public static final String FROM = "extra.from";
	public static final int FROM_EXTERNAL = 0;
	public static final int FROM_INTERNAL = 1;
	public static final String SMS_APP_KEY="c104bd01f0ba";
	public static final String SMS_APP_SECRET="35cca6958f0f1192aac5ddf7c4bebab9";
	
	public static final String SHARE_PREFERENCE_CUP="CUP";
	public static final String SHARE_PREFERENCE_CUP_PHONE="PHONE";
	public static final String SHARE_PREFERENCE_CUP_BIRTHDAY="BIRTHDAY";
	public static final String SHARE_PREFERENCE_CUP_NICKNAME="NICKNAME";
	public static final String SHARE_PREFERENCE_CUP_AVATAR="AVATAR";
	public static final String SHARE_PREFERENCE_CUP_HEIGHT="HEIGHT";
	public static final String SHARE_PREFERENCE_CUP_CITY="CITY";
	public static final String SHARE_PREFERENCE_CUP_ACCOUNTID="ACCOUNTID";
	public static final String SHARE_PREFERENCE_CUP_SEX="SEX";
	public static final String SHARE_PREFERENCE_CUP_SCENE="SCENE";
	public static final String SHARE_PREFERENCE_CUP_CONSTITUTION="CONSTITUTION";
	public static final String SHARE_PREFERENCE_CUP_WEIGHT="WEIGHT";
	
	
	
	//msg define
	public static final int COUNT_DOWN_MSG=0x8001; //login count down msg
	public static final int LOGIN_SUCCESS_MSG=0x8002; //login success msg
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

	
	public static void httpGet(String url,Handler mHandler) {
		Utils.Log(" xxxxxxxxxxxxxxxxxxxxx http httpGet url:"+url);
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
//					JSONArray jsonArray= new JSONArray(result);
					JSONObject jsonObject=new JSONObject(result);
					Message msg=new Message();
					msg.what=LOGIN_SUCCESS_MSG;
					msg.arg1=1;
					msg.obj=jsonObject;
//					mHandler.sendEmptyMessage(1);
					mHandler.sendMessage(msg);
					Utils.Log(" xxxxxxxxxxxxxxxxxxxxx http httpGet finish output 2"+jsonObject);
				}
			} catch (Exception e) {
				Utils.Log(TAG, "httpGet error:" + e);
			}
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
	public static File downLoadFile(String httpUrl,String dirPath,String fileName) {
		Utils.Log("downLoadFile start");
		File dir =new File(dirPath);
		final File file = new File(dir.getAbsolutePath(),fileName);
		try {
			URL url = new URL(httpUrl);
			try {
				if(!dir.exists()){
					dir.mkdirs();
					dir.setWritable(Boolean.TRUE);
				}
				if (!file.exists()) {
					file.createNewFile();
					file.setWritable(Boolean.TRUE);
				}
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
