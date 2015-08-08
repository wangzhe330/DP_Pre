package fragment;

import com.example.dp2.R;

import Cinema.CinemaInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShopShowFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_shopshow, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		//((TextView)getView().findViewById(R.id.fragment_shopshow_tv0)).setText("商户信息展示");
	
		//getArguments() 返回值就是一个bundle
		CinemaInfo cinemaInfo =  (CinemaInfo) getArguments().getSerializable("shop");
		String shopNameString = cinemaInfo.getName();
		((TextView)getView().findViewById(R.id.fragment_shopshow_tv0)).setText(cinemaInfo.getName());
		
	}
}

