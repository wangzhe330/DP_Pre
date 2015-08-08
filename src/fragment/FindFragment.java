package fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yanzi.ui.HorizontalListView;
import org.yanzi.ui.HorizontalListViewAdapter;
import org.yanzi.ui.HorizontalListViewVolleyAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dp2.GlobalData;
import com.example.dp2.R;
import com.example.volleytest.adapter.MListAdapter;




import Cinema.CinemaInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings.Global;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class FindFragment extends Fragment{

	private ViewPager viewPager; // android-support-v4�еĻ������

	private List<ImageView> imageViews; // ������ͼƬ����

	private String[] titles; // ͼƬ����
	private int[] imageResId; // ͼƬID
	private List<View> dots; // ͼƬ�������ĵ���Щ��
	private TextView tv_title;
	private int currentItem = 0; // ��ǰͼƬ��������

	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService;
	
	
	HorizontalListView hListView;
	//HorizontalListViewAdapter hListViewAdapter;
	HorizontalListViewVolleyAdapter hListViewAdapter;
	ImageView previewImg;
	View olderSelectView = null;
	
	//volley
	private RequestQueue mRequestQueue;
	
	//GlobalData globalData = (GlobalData)getActivity().getApplicationContext();
	
	//��ҳ��URL
	private String indexURL = "http://192.168.95.3:10000/";//"http://121.40.63.118:5000/post_test";//"http://121.249.22.68:10000";//"http://121.249.28.157:10000/search";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.find_fragment, container, false);

	}
	
	@Override
	public void onDestroy(){
		// ��fragemnt���ɼ���ʱ��ֹͣ�л�
		scheduledExecutorService.shutdown();
		//wz�������֮���˳��Ͳ�������
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((TextView)getView().findViewById(R.id.find_tv_0)).setText("������ӳ");
		
		mRequestQueue = Volley.newRequestQueue(getActivity());  
		
		
		//�������post
		indexByPost("test");

		View view = getView();
		
		//pageView
		myPageView(view);
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// ����ʾ������ÿ�������л�һ��ͼƬ��ʾ
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
		
		hListView = (HorizontalListView)view.findViewById(R.id.horizon_listview);
		//previewImg = (ImageView)view.findViewById(R.id.image_preview);
		String[] titles = {"mov1", "mov2", "mov2"};
		final int[] ids = {R.drawable.mov1, R.drawable.mov2,
				R.drawable.mov3};
		//hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(),titles,ids);
		/*
		hListViewAdapter = new HorizontalListViewAdapter(this.getActivity(),titles,ids);
		hListView.setAdapter(hListViewAdapter);
		*/
		hListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//previewImg.setImageResource(ids[position]);
				
				hListViewAdapter.setSelectIndex(position);
				hListViewAdapter.notifyDataSetChanged();
				
			}
		});
		
	}
	
	private void indexByPost(final String postPara) {
		//�����--------------------------------------------------------------------
		//POST��д��
		StringRequest stringRequest = new StringRequest(Method.POST,
				indexURL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("wzdebug","response by string : ->");
						Log.d("wzdebug", response);
						dealPostData(response);
					}
				}, 
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("wzdebug","error : ->");
						Log.e("wzdebug", error.getMessage(), error);
					}
				}) 
        	{
	        	@Override
	        	protected Map<String, String> getParams() throws AuthFailureError {
	        		Map<String, String> map = new HashMap<String, String>();
	        		/*
	        		 * wz ���Է���key-value  ���õ�
	        		map.put("keyword", postPara);
	        		map.put("item1", "KTV");
	        		map.put("item2", "KFC");
	        		map.put("movie_name", "�����֮��");
	        		*/
	        		return map;
	        	}
        	};
        mRequestQueue.add(stringRequest);
   
	}
	
	public void dealPostData(String retData){
		try {
			GlobalData globalData = (GlobalData)getActivity().getApplicationContext();
			ArrayList<CinemaInfo> cinemaDate = new ArrayList<CinemaInfo>();
			
			//JSONObject jsonObj= new JSONObject(retData);
			JSONArray jsonArray = new JSONArray(retData);
			
			globalData.setCinemaJsonArray(jsonArray);
			globalData.setCinemaDataByJsonArray(jsonArray);
			
			/*
			int len = jsonArray.length();
			Log.d("wzdebug","the length of JSONArray is : " + len);
			
			int i = 0;
			for(i=0;i<len;i++){
				CinemaInfo cinemaInfo = new CinemaInfo();
				//cinemaInfo.setImgUrl( jsonArray[i].getString(""));
				JSONObject oj = jsonArray.getJSONObject(i);
				cinemaInfo.setImgUrl(oj.getString("photo_url"));
				cinemaInfo.setName(oj.getString("name"));
				Log.d("wzdebug","url is ---" + oj.getString("photo_url"));
				cinemaDate.add(cinemaInfo);
			}
			*/
			cinemaDate = globalData.getCinemaData();
			
			//�����ݸ�������
			View view = getView();	
			hListView=(HorizontalListView)view.findViewById(R.id.horizon_listview); 				       				       				        
	        hListViewAdapter = new HorizontalListViewVolleyAdapter(getActivity(), cinemaDate);          
	        hListView.setAdapter(hListViewAdapter); 
	        HorizontalListViewVolleyAdapter hListViewAdapter = new HorizontalListViewVolleyAdapter(this.getActivity(),cinemaDate);
			hListView.setAdapter(hListViewAdapter);
				
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
		
	private void myPageView(View view) {
		imageResId = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e };
		titles = new String[imageResId.length];
		titles[0] = "���������ף��ҾͲ��ܵ���";
		titles[1] = "�����ֻ��������ٳ������ϸ������˴�ϳ�";
		titles[2] = "���ر�����Ӱ�������";
		titles[3] = "������TV�������";
		titles[4] = "��Ѫ��˿�ķ�ɱ";

		imageViews = new ArrayList<ImageView>();

		// ��ʼ��ͼƬ��Դ
		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
		}

		
		dots = new ArrayList<View>();
		dots.add(view.findViewById(R.id.v_dot0));
		dots.add(view.findViewById(R.id.v_dot1));
		dots.add(view.findViewById(R.id.v_dot2));
		dots.add(view.findViewById(R.id.v_dot3));
		dots.add(view.findViewById(R.id.v_dot4));

		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(titles[0]);//

		viewPager = (ViewPager) view.findViewById(R.id.vp);
		viewPager.setAdapter(new MyAdapter());// �������ViewPagerҳ���������
		// ����һ������������ViewPager�е�ҳ��ı�ʱ����
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}
	// �л���ǰ��ʾ��ͼƬ
		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if(getActivity()!=null){
					viewPager.setCurrentItem(currentItem);// �л���ǰ��ʾ��ͼƬ
				}
				else {
					return;
				}
			};
		};

		/**
		 * �����л�����
		 * 
		 * @author Administrator
		 * 
		 */
		private class ScrollTask implements Runnable {

			public void run() {
				//wz
				if(getActivity()!=null){
					synchronized (viewPager) {
						System.out.println("currentItem: " + currentItem);
						currentItem = (currentItem + 1) % imageViews.size();
						handler.obtainMessage().sendToTarget(); // ͨ��Handler�л�ͼƬ
					}
				}else{
					return;
				}
			}

		}

		/**
		 * ��ViewPager��ҳ���״̬�����ı�ʱ����
		 * 
		 * @author Administrator
		 * 
		 */
		private class MyPageChangeListener implements OnPageChangeListener {
			private int oldPosition = 0;

			/**
			 * This method will be invoked when a new page becomes selected.
			 * position: Position index of the new selected page.
			 */
			public void onPageSelected(int position) {
				currentItem = position;
				tv_title.setText(titles[position]);
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				dots.get(position).setBackgroundResource(R.drawable.dot_focused);
				oldPosition = position;
			}

			public void onPageScrollStateChanged(int arg0) {

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
		}	
		
		/**
		 * ���ViewPagerҳ���������
		 * 
		 * @author Administrator
		 * 
		 */
		private class MyAdapter extends PagerAdapter {

			@Override
			public int getCount() {
				return imageResId.length;
			}

			@Override
			public Object instantiateItem(View arg0, int arg1) {
				((ViewPager) arg0).addView(imageViews.get(arg1));
				return imageViews.get(arg1);
			}

			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView((View) arg2);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {

			}

			@Override
			public Parcelable saveState() {
				return null;
			}

			@Override
			public void startUpdate(View arg0) {

			}

			@Override
			public void finishUpdate(View arg0) {

			}
		}
}
