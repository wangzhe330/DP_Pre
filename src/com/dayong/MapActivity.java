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
	private MapView mMapView = null;			//用于显示地图的控件
	private BaiduMap mBaiduMap;					//地图对象，地图应该就存放在该对象中
	private LocationClient mLocationClient;		//用于定位的对象
	public MyLocationListener myLocationListener;			//定位监听器，定位有结果时调用
	private LocationMode mCurrentMode = LocationMode.NORMAL;//应该是定位的模式，暂时还不懂
	private volatile boolean isFirstLocation = true;		//是否为首次定位标志位
	
	private float mCurrentAccuracy;				//定位精度，来自LocationClient.getRadius()
	private double mCurrentLantitude;			//纬度，来自LocationClient.getLantitude()
	private double mCurrentLongitude;			//经度，来自LocationClient.getLongitude()
	
	private MyOrientationListener myOrientationListener;//应该是一个方向监听器，当手机方向发生变化时调用
	private int mXDirection;					//X轴方向
	//private int mCurrentStyle;					//定位模式，我们这个程序中定位模式一直都是NORMAL，所以这个参数用处不大
	
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
		//得到mMapView对象
		mMapView = (MapView)findViewById(R.id.id_bmapView);
		//得到mBaiduMap对象
		mBaiduMap = mMapView.getMap();
		//设置Map的类型
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		//得到标记商家图标的图标信息
		mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		//这两句话暂时还不知道是什么意思
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		//初始化定位功能
		initMyLocation();
		//初始化手机方向监听器
		initOritationListener();
	}
	
	private void initMyLocation()
	{
		//实例化定位的这个对象
		mLocationClient = new LocationClient(this);
		//新建一个定位监听器
		myLocationListener = new MyLocationListener();
		//绑定监听器
		mLocationClient.registerLocationListener(myLocationListener);
		//创建一个定位属相，用于配置定位的一些参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//使用GPS定位
		option.setCoorType("bd09ll");//以经纬度的方式返回位置
		option.setScanSpan(1000);//每隔1000ms定位一次
		mLocationClient.setLocOption(option);//设置定位属性，下面调用mLocationClient的start()方法就能够实现定位了
	}
	
	private void initOritationListener()
	{
		//创建一个新的方向监听器对象
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		//绑定一个监听器，当方向发生变化时调用
		myOrientationListener.setOnOrientationListener(new OnOrientationListener()
		{
			public void onOrientationChanged(float x)//这里的x应该就是表示手机的方向
			{
				mXDirection = (int)x;
				//应该是指创建一个表示位置的对象，内容包括精度（就是定位周围的那个圈，方向，经度，纬度）
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccuracy).direction(mXDirection)
						.latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				//将这个位置打对象与地图对象绑定在一起，这样地图对象就能够显示出来了
				mBaiduMap.setMyLocationData(locData);
				//创建一个图片描述对象，来自资源中的图像
				BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
				//创建一个定位的配置对象，将这个对象包括图像模式，已经刚刚的图片描述对象
				MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
				//配置刚刚的地图对象
				mBaiduMap.setMyLocationConfigeration(config);
				//经过这样的配置之后，地图对象就应该能显示效果了
			}
		});
	}
	
	//这段程序用于在地图上标记我们需要标记的位置，需要的参数为经纬度
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
		mBaiduMap.setMyLocationEnabled(true);//地图上定位打开
		if(!mLocationClient.isStarted())
			mLocationClient.start();//在见面出现时就打开定位功能
		myOrientationListener.start();
		super.onStart();
	}
	
	protected void onStop()
	{
		mBaiduMap.setMyLocationEnabled(false);//地图上定位关闭
		mLocationClient.stop();//关闭定位功能
		myOrientationListener.stop();
		super.onStop();
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		mMapView.onDestroy();//销毁显示地图的控件
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
	//定位监听器，当定位有结果返回时调用
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null || mMapView == null)
				return;
			//创建一个标志方位的对象，与上面一样，并将其赋予地图对象
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			
			mCurrentAccuracy = location.getRadius();
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			//与上面相同
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
