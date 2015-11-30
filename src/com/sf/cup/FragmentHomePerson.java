package com.sf.cup;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sf.cup.utils.FileUtil;
import com.sf.cup.utils.Utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import sun.geoffery.uploadpic.SelectPicPopupWindow;

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

	List<Map<String, Object>> personList1 = new ArrayList<Map<String, Object>>(); // list
																					// view
																					// ����һֱ��Ū���
	List<Map<String, Object>> personList2 = new ArrayList<Map<String, Object>>(); // list
																					// view
																					// ����һֱ��Ū���

	EditText person_info;

	AlertDialog ad;

	LinearLayout avatar_layout;
	ImageView avatar_image;
	private SelectPicPopupWindow menuWindow; // �Զ����ͷ��༭������
	private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// ͷ���ļ�����
	private String urlpath; // ͼƬ����·��
	private String resultStr = ""; // ����˷��ؽ����
	private static ProgressDialog pd;// �ȴ�����Ȧ
	private static final int REQUESTCODE_PICK = 0; // ���ѡͼ���
	private static final int REQUESTCODE_TAKE = 1; // ������ձ��
	private static final int REQUESTCODE_CUTTING = 2; // ͼƬ���б��

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message paramAnonymousMessage) {
			Utils.Log("handle:" + paramAnonymousMessage);
			switch (paramAnonymousMessage.what) {
			case 1:
				// alertdialog with edittext cant not open im.
				try {
					Thread.sleep(200);
					person_info.dispatchTouchEvent(
							MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
									MotionEvent.ACTION_DOWN, person_info.getRight(), person_info.getRight() + 5, 0));
					person_info.dispatchTouchEvent(
							MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
									MotionEvent.ACTION_UP, person_info.getRight(), person_info.getRight() + 5, 0));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				break;
			case 2:
				break;
			case Utils.GET_SUCCESS_MSG:
	            	JSONObject jsonObject=(JSONObject)paramAnonymousMessage.obj;
	            	//upload pic success
	            	String picUrl = jsonObject.optString("url","");
	            	if(!TextUtils.isEmpty(picUrl)){
	            		SharedPreferences.Editor e=Utils.getSharedPpreferenceEdit(getActivity());
	            		e.putBoolean(Utils.SHARE_PREFERENCE_CUP_AVATAR_IS_MODIFY, false);
	            		e.commit();
	            	}
			}
		}
	};

	public static FragmentHomePerson newInstance(Bundle b) {
		FragmentHomePerson fd = new FragmentHomePerson();
		fd.setArguments(b);
		return fd;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources res = getResources();
		list1Title = res.getStringArray(R.array.person_list_title1);
		list2Title = res.getStringArray(R.array.person_list_title2);
		
		SharedPreferences p=Utils.getSharedPpreference(getActivity());
		String sex=p.getString(Utils.SHARE_PREFERENCE_CUP_SEX, "");
		if(TextUtils.isEmpty(sex)){
			SharedPreferences.Editor e=Utils.getSharedPpreferenceEdit(getActivity());
			e.putString(Utils.SHARE_PREFERENCE_CUP_SEX, "Ů");
			e.commit();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tab_home_person_info, null);

		initList1();
		initList2();

		personlist_view1 = (ListView) view.findViewById(R.id.persionlist_view1);
		hlva1 = new PersonListViewAdapter1(this.getActivity(), getData1(), R.layout.tab_home_list_item,
				new String[] { "title", "info", "img" }, new int[] { R.id.title_text, R.id.info_text, R.id.right_img });
		setHeight(hlva1, personlist_view1);
		personlist_view1.setAdapter(hlva1);

		personlist_view2 = (ListView) view.findViewById(R.id.persionlist_view2);
		hlva2 = new PersonListViewAdapter2(this.getActivity(), getData2(), R.layout.tab_home_list_item,
				new String[] { "title", "info", "img" }, new int[] { R.id.title_text, R.id.info_text, R.id.right_img });
		setHeight(hlva2, personlist_view2);
		personlist_view2.setAdapter(hlva2);

		avatar_image = (ImageView) view.findViewById(R.id.avatar_image);
		SharedPreferences p = Utils.getSharedPpreference(getActivity());
		String avatarFilePath = p.getString(Utils.SHARE_PREFERENCE_CUP_AVATAR, "");
		if(!TextUtils.isEmpty(avatarFilePath)){
			Drawable d = Drawable.createFromPath(avatarFilePath);
			avatar_image.setImageDrawable(d);
		}
		
		
		avatar_layout = (LinearLayout) view.findViewById(R.id.avatar_layout);
		avatar_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menuWindow = new SelectPicPopupWindow(getActivity(), itemsOnClick);
				menuWindow.showAtLocation(view.findViewById(R.id.mainLayout),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});

		return view;
	}

	// Ϊ��������ʵ�ּ�����
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			// ����
			case R.id.takePhotoBtn:
				Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// �������ָ������������պ����Ƭ�洢��·��
				takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
				startActivityForResult(takeIntent, REQUESTCODE_TAKE);
				break;
			// ���ѡ��ͼƬ
			case R.id.pickPhotoBtn:
				Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
				// ���������Ҫ�����ϴ�����������ͼƬ����ʱ����ֱ��д�磺"image/jpeg �� image/png�ȵ�����"
				pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(pickIntent, REQUESTCODE_PICK);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_PICK:// ֱ�Ӵ�����ȡ
			try {
				startPhotoZoom(data.getData());
			} catch (NullPointerException e) {
				e.printStackTrace();// �û����ȡ������
			}
			break;
		case REQUESTCODE_TAKE:// �����������
			File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
			startPhotoZoom(Uri.fromFile(temp));
			break;
		case REQUESTCODE_CUTTING:// ȡ�òü����ͼƬ
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}

	/**
	 * ����ü�֮���ͼƬ����
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			// ȡ��SDCardͼƬ·������ʾ
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(null, photo);
			urlpath = FileUtil.saveFile(getActivity(), "temphead.jpg", photo);
			SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
			e.putString(Utils.SHARE_PREFERENCE_CUP_AVATAR, urlpath);
			e.putBoolean(Utils.SHARE_PREFERENCE_CUP_AVATAR_IS_MODIFY, true);
			e.commit();
			avatar_image.setImageDrawable(drawable);

			// ���̺߳�̨�ϴ������
			// pd = ProgressDialog.show(mContext, null, "�����ϴ�ͼƬ�����Ժ�...");
			// new Thread(uploadImageRunnable).start();
		}
	}

	private class PersonListViewAdapter1 extends SimpleAdapter {
		public PersonListViewAdapter1(Context context, List<Map<String, Object>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			view.setOnClickListener(new MyListener1(position));
			return view;
		}

	}

	private class MyListener1 implements OnClickListener {
		int mPosition;

		public MyListener1(int inPosition) {
			mPosition = inPosition;
		}

		@Override
		public void onClick(View v) {

			switch (mPosition) {
			case 1:
				String sexString = (String) personList1.get(mPosition).get("info");
				int initSexCheck = "��".equals(sexString) ? 0 : 1;
				//TODO    display null at first time         !!!!!!!!!!!!!!!!!!!!!!   fix: init female oncreate
				ad = new AlertDialog.Builder(getActivity()).setTitle((String) personList1.get(mPosition).get("title"))
						.setSingleChoiceItems(new String[] { "��", "Ů" }, initSexCheck,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										String sex = which == 0 ? "��" : "Ů";
										personList1.get(mPosition).put("info", sex);
										SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
										e.putString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[mPosition], sex);
										e.commit();
										doUpdate1();
									}
								})
						.setNegativeButton("ȷ��", null).show();
				break;
			case 2:
				// could not change the phone number
				Toast.makeText(getActivity(), "�ֻ�����󶨣��޷��޸�", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View layout = inflater.inflate(R.layout.tab_home_person_dialog,
						(ViewGroup) v.findViewById(R.id.dialog));
				TextView person_title = (TextView) layout.findViewById(R.id.person_title);
				person_info = (EditText) layout.findViewById(R.id.person_info);
				person_info.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
				person_info.setText((String) personList1.get(mPosition).get("info"));
				person_title.setText(list1Title[mPosition]);
				ad = new AlertDialog.Builder(getActivity())
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								personList1.get(mPosition).put("info", person_info.getText().toString());
								SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
								e.putString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[mPosition],
										person_info.getText().toString());
								e.commit();
								doUpdate1();
							}
						}).setNegativeButton("ȡ��", null).create();
				ad.setTitle("������Ϣ����");
				ad.setView(layout);
				ad.show();
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = 1;
				mHandler.sendMessage(msg);
				break;
			}
		}
	}

	private class PersonListViewAdapter2 extends SimpleAdapter {
		public PersonListViewAdapter2(Context context, List<Map<String, Object>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			view.setOnClickListener(new MyListener2(position));
			return view;
		}

	}
	
	

	private class MyListener2 implements OnClickListener {
		Message msg;
		int mPosition;

		public MyListener2(int inPosition) {
			mPosition = inPosition;
		}

		@Override
		public void onClick(View v) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View layout = inflater.inflate(R.layout.tab_home_person_dialog,
					(ViewGroup) v.findViewById(R.id.dialog));
			TextView person_title = (TextView) layout.findViewById(R.id.person_title);
			person_info = (EditText) layout.findViewById(R.id.person_info);
			person_info.setText((String) personList2.get(mPosition).get("info"));
			person_title.setText(list2Title[mPosition]);

			switch (mPosition) {
			case 2:
			case 3:
				person_info.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
				person_info.setInputType(InputType.TYPE_CLASS_NUMBER);
				ad = new AlertDialog.Builder(getActivity())
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								personList2.get(mPosition).put("info", person_info.getText().toString());
								SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
								e.putString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[mPosition],
										person_info.getText().toString());
								e.commit();
								doUpdate2();
							}
						}).setNegativeButton("ȡ��", null).create();
				ad.setTitle("������Ϣ����");
				ad.setView(layout);
				ad.show();
				msg = new Message();
				msg.what = 1;
				msg.arg1 = 1;
				mHandler.sendMessage(msg);
				break;
			case 0:
			case 1:
				if(mPosition==0){
					person_info.setHint("��˾");
				}else if(mPosition==1)
				{
					person_info.setHint("����");
				}
				person_info.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
				ad = new AlertDialog.Builder(getActivity())
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								personList2.get(mPosition).put("info", person_info.getText().toString());
								SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
								e.putString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[mPosition],
										person_info.getText().toString());
								e.commit();
								doUpdate2();
							}
						}).setNegativeButton("ȡ��", null).create();

				ad.setTitle("������Ϣ����");
				ad.setView(layout);
				ad.show();
				msg = new Message();
				msg.what = 1;
				msg.arg1 = 1;
				mHandler.sendMessage(msg);
				break;

			case 4:
				Calendar c = Calendar.getInstance();
				String birthday = (String) personList2.get(mPosition).get("info");
				String[] dateSpilt = (TextUtils.isEmpty(birthday) ? "1990-01-01" : birthday).split("-");

				DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
						// et.setText("��ѡ���ˣ�" + year + "��" + (month+1) + "��" +
						// dayOfMonth + "��");
						String dateFormat = year + "-" + month + "-" + dayOfMonth;
						personList2.get(mPosition).put("info", dateFormat);
						SharedPreferences.Editor e = Utils.getSharedPpreferenceEdit(getActivity());
						e.putString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[mPosition], dateFormat);
						e.commit();
						doUpdate2();
					}
				}, Integer.parseInt(dateSpilt[0]), // �������
						Integer.parseInt(dateSpilt[1]), // �����·�
						Integer.parseInt(dateSpilt[2]) // ��������
				);
				dialog.show();
				break;
			}
		}

	}

	public void setHeight(BaseAdapter comAdapter, ListView l) {
		int listViewHeight = 0;
		int adaptCount = comAdapter.getCount();
		for (int i = 0; i < adaptCount; i++) {
			View temp = comAdapter.getView(i, null, l);
			temp.measure(0, 0);
			listViewHeight += temp.getMeasuredHeight();
		}
		LayoutParams layoutParams = l.getLayoutParams();
		layoutParams.width = LayoutParams.FILL_PARENT;
		layoutParams.height = listViewHeight + 2;
		l.setLayoutParams(layoutParams);
	}

	private List<Map<String, Object>> getData1() {
		return personList1;
	}

	private void initList1() {
		Map<String, Object> map;
		SharedPreferences p = Utils.getSharedPpreference(getActivity());
		for (int i = 0; i < list1Title.length; i++) {
			map = new HashMap<String, Object>();
			map.put("title", list1Title[i]);
			String info = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[i], "");
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
		SharedPreferences p = Utils.getSharedPpreference(getActivity());
		for (int i = 0; i < list2Title.length; i++) {
			map = new HashMap<String, Object>();
			map.put("title", list2Title[i]);
			String info = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[i], "");
			map.put("info", info);
			map.put("img", ">");
			personList2.add(map);
		}
	}

	private void doUpdate1() {
		if (hlva1 != null) {
			Utils.Log("doUpdate1:" + personList1);
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
		SharedPreferences p = Utils.getSharedPpreference(getActivity());
		final JSONObject result = new JSONObject();
		String nickname = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[0], "");
		String sex = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[1], "");
		final String phone = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_1[2], "");

		String scene = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[0], "");
		String constitution = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[1], "");
		String height = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[2], "");
		String weight = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[3], "");
		String birthday = p.getString(Utils.SHARE_PREFERENCE_CUP_PERSON_2[4], "");
		
		final String accountid = p.getString(Utils.SHARE_PREFERENCE_CUP_ACCOUNTID, "");
		final String avatar = p.getString(Utils.SHARE_PREFERENCE_CUP_AVATAR, "");
		final boolean avatarIsModify = p.getBoolean(Utils.SHARE_PREFERENCE_CUP_AVATAR_IS_MODIFY, false);

		if(TextUtils.isEmpty(accountid)||TextUtils.isEmpty(phone)){
			// it must be a bug   missing the accountid
			return ;
		}
		try {
			result.put("accountid", accountid);
			
			result.put("nickname", nickname);
			result.put("sex", sex);
			result.put("phone", phone);
			
			result.put("scene", scene);
			result.put("constitution", constitution);
			result.put("height", height);
			result.put("weight", weight);
			result.put("birthday", birthday);
			result.put("nickname", nickname);
			Utils.Log("xxxxxxxxxxxxxxxxxx httpPut result:" + result+",avatar:"+avatar+",avatarIsModify:"+avatarIsModify);
			// send to server
			new Thread(new Runnable() {
				@Override
				public void run() {
					// http://121.199.75.79:8280/user/saveme
					Utils.httpPut(Utils.URL_PATH + "/user/saveme", result, mHandler);
					
					if(!TextUtils.isEmpty(avatar)&&avatarIsModify){
						Utils.httpPostFile(Utils.URL_PATH +"/user/updateProfile.do", avatar, mHandler,accountid,phone);
					}
				}
			}).start();
		} catch (Exception e) {
			Utils.Log(TAG,"xxxxxxxxxxxxxxxxxx httpPut error:" + e);
			e.printStackTrace();
		}
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