package com.example.volleytest.adapter;

import java.util.ArrayList;

import Cinema.CinemaInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.example.dp2.R;
import com.example.volleytest.bean.Info;
import com.example.volleytest.cache.BitmapCache;

public class MListAdapter extends BaseAdapter{
	private Context ctx;
	private ArrayList<Info> infos;
	private ArrayList<CinemaInfo> cinemaInfos;
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    /*
	public MListAdapter(Context ctx, ArrayList<Info> infos) {
		this.ctx = ctx;
		this.infos = infos;
		mQueue = Volley.newRequestQueue(ctx);
		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
	}*/
	public MListAdapter(Context ctx, ArrayList<CinemaInfo> cinemaInfos) {
		this.ctx = ctx;
		this.cinemaInfos = cinemaInfos;
		mQueue = Volley.newRequestQueue(ctx);
		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
	}
	@Override
	public int getCount() {
		//return infos.size();
		return cinemaInfos.size();
	}

	@Override
	/*
	public Info getItem(int position) {
		//return infos.get(position);
		return cinemaInfos.get(position)l
	}*/
	public CinemaInfo getItem(int position) {
		//return infos.get(position);
		return cinemaInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//获取组布局
		convertView = LayoutInflater.from(ctx).inflate(R.layout.list_raw, null);		
		
		ImageView imageView  = (ImageView) convertView.findViewById(R.id.list_item_image);
		ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
        mImageLoader.get(getItem(position).getImgUrl(), listener);
        
        TextView textView = (TextView)convertView.findViewById(R.id.list_item_name);
        textView.setText(getItem(position).getName());
        
        textView = (TextView)convertView.findViewById(R.id.list_item_location);
        textView.setText(getItem(position).getLocation());
        
        textView = (TextView)convertView.findViewById(R.id.list_item_tel);
        textView.setText(getItem(position).getTel());
        
        //设置imageview自适应大小
        /*
        int screenWidth = getScreenWidth(this);
        ViewGroup.LayoutParams lp = testImage.getLayoutParams();
        lp.width = screenWidth;
        lp.height = LayoutParams.WRAP_CONTENT;
        testImage.setLayoutParams(lp);

        testImage.setMaxWidth(screenWidth);
        testImage.setMaxHeight(screenWidth * 5); 这里其实可以根据需求而定，我这里测试为最大宽度的5倍
        */
        
		return convertView;
	}


}
