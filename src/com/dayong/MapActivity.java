package com.dayong;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.dayong.Info;
import com.dayong.MyOrientationListener;
import com.example.dp2.R;
import com.dayong.MapActivity.MyLocationListener;
import com.dayong.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.RelativeLayout;

public class MapActivity extends Activity {
	private MapView mMapView = null;			//������ʾ��ͼ�Ŀؼ�
	private BaiduMap mBaiduMap;					//��ͼ���󣬵�ͼӦ�þʹ���ڸö�����
	private LocationClient mLocationClient;		//���ڶ�λ�Ķ���
	public MyLocationListener myLocationListener;			//��λ����������λ�н��ʱ����
	private LocationMode mCurrentMode = LocationMode.NORMAL;//Ӧ���Ƕ�λ��ģʽ����ʱ������
	private volatile boolean isFirstLocation = true;		//�Ƿ�Ϊ�״ζ�λ��־λ
	
	private float mCurrentAccuracy;				//��λ���ȣ�����LocationClient.getRadius()
	private double mCurrentLantitude;			//γ�ȣ�����LocationClient.getLantitude()
	private double mCurrentLongitude;			//���ȣ�����LocationClient.getLongitude()
	
	private MyOrientationListener myOrientationListener;//Ӧ����һ����������������ֻ��������仯ʱ����
	private int mXDirection;					//X�᷽��
	//private int mCurrentStyle;					//��λģʽ��������������ж�λģʽһֱ����NORMAL��������������ô�����
	
	private BitmapDescriptor mIconMaker;
	private RelativeLayout mMarkerInfoLy;
	
	private Info info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		
		Intent intent = getIntent();
		info = (Info)intent.getSerializableExtra("SHOPS");
		
		isFirstLocation = true;
		//�õ�mMapView����
		mMapView = (MapView)findViewById(R.id.id_bmapView);
		//�õ�mBaiduMap����
		mBaiduMap = mMapView.getMap();
		//����Map������
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		//�õ�����̼�ͼ���ͼ����Ϣ
		mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		//�����仰��ʱ����֪����ʲô��˼
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		//��ʼ����λ����
		initMyLocation();
		//��ʼ���ֻ����������
		initOritationListener();
	}
	
	private void initMyLocation()
	{
		//ʵ������λ���������
		mLocationClient = new LocationClient(this);
		//�½�һ����λ������
		myLocationListener = new MyLocationListener();
		//�󶨼�����
		mLocationClient.registerLocationListener(myLocationListener);
		//����һ����λ���࣬�������ö�λ��һЩ����
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//ʹ��GPS��λ
		option.setCoorType("bd09ll");//�Ծ�γ�ȵķ�ʽ����λ��
		option.setScanSpan(1000);//ÿ��1000ms��λһ��
		mLocationClient.setLocOption(option);//���ö�λ���ԣ��������mLocationClient��start()�������ܹ�ʵ�ֶ�λ��
	}
	
	private void initOritationListener()
	{
		//����һ���µķ������������
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		//��һ�������������������仯ʱ����
		myOrientationListener.setOnOrientationListener(new OnOrientationListener()
		{
			public void onOrientationChanged(float x)//�����xӦ�þ��Ǳ�ʾ�ֻ��ķ���
			{
				mXDirection = (int)x;
				//Ӧ����ָ����һ����ʾλ�õĶ������ݰ������ȣ����Ƕ�λ��Χ���Ǹ�Ȧ�����򣬾��ȣ�γ�ȣ�
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccuracy).direction(mXDirection)
						.latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				//�����λ�ô�������ͼ�������һ��������ͼ������ܹ���ʾ������
				mBaiduMap.setMyLocationData(locData);
				//����һ��ͼƬ��������������Դ�е�ͼ��
				BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
				//����һ����λ�����ö��󣬽�����������ͼ��ģʽ���Ѿ��ոյ�ͼƬ��������
				MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
				//���øոյĵ�ͼ����
				mBaiduMap.setMyLocationConfigeration(config);
				//��������������֮�󣬵�ͼ�����Ӧ������ʾЧ����
			}
		});
	}
	
	//��γ��������ڵ�ͼ�ϱ��������Ҫ��ǵ�λ�ã���Ҫ�Ĳ���Ϊ��γ��
	public void addInfosOverlay(List<Info> infos)
	{
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		Marker marker = null;
		for(Info info : infos)
		{
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			overlayOptions = new MarkerOptions().position(latLng).icon(mIconMaker).zIndex(5);
			marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			Bundle bundle = new Bundle();
			bundle.putSerializable("info", info);
			marker.setExtraInfo(bundle);
		}
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(u);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onStart()
	{
		mBaiduMap.setMyLocationEnabled(true);//��ͼ�϶�λ��
		if(!mLocationClient.isStarted())
			mLocationClient.start();//�ڼ������ʱ�ʹ򿪶�λ����
		myOrientationListener.start();
		super.onStart();
	}
	
	protected void onStop()
	{
		mBaiduMap.setMyLocationEnabled(false);//��ͼ�϶�λ�ر�
		mLocationClient.stop();//�رն�λ����
		myOrientationListener.stop();
		super.onStop();
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		mMapView.onDestroy();//������ʾ��ͼ�Ŀؼ�
		mMapView = null;
	}
	
	public void onResume()
	{
		super.onResume();
		mMapView.onResume();
	}
	
	public void onPause()
	{
		super.onPause();
		mMapView.onPause();
	}
	//��λ������������λ�н������ʱ����
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null || mMapView == null)
				return;
			//����һ����־��λ�Ķ���������һ���������丳���ͼ����
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			
			mCurrentAccuracy = location.getRadius();
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			//��������ͬ
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			if(isFirstLocation)
			{
				isFirstLocation = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				addInfosOverlay(info.infos);
			}
		}
		
		public void onReceivePoi(BDLocation poiLocation)
		{
			if(poiLocation == null)
				return;
		}
	}
}
