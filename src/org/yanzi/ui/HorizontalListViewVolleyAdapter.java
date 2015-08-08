package org.yanzi.ui;

import java.util.ArrayList;

import org.yanzi.util.BitmapUtil;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.dp2.R;
import com.example.volleytest.cache.BitmapCache;

import Cinema.CinemaInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalListViewVolleyAdapter extends BaseAdapter{
	private Context ctx;
	private ArrayList<CinemaInfo> cinemaInfos;
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
	private LayoutInflater mInflater;
	Bitmap iconBitmap;
	private int selectIndex = -1;
	
    public HorizontalListViewVolleyAdapter(Context ctx, ArrayList<CinemaInfo> cinemaInfos) {
		this.ctx = ctx;
		this.cinemaInfos = cinemaInfos;
		mQueue = Volley.newRequestQueue(ctx);
		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cinemaInfos.size();
	}

	@Override
	public CinemaInfo getItem(int position) {
		//return infos.get(position);
		return cinemaInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = LayoutInflater.from(ctx).inflate(R.layout.horizontal_list_item, null);		
		
		ImageView imageView  = (ImageView) convertView.findViewById(R.id.img_list_item);		
		ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
        
		mImageLoader.get(getItem(position).getImgUrl(), listener);
        
        TextView textView = (TextView)convertView.findViewById(R.id.text_list_item);
        textView.setText(getItem(position).getName());
             
        
		return convertView;
	}

	private static class ViewHolder {
		private TextView mTitle ;
		private ImageView mImage;
	}
	private Bitmap getPropThumnail(int id){
		Drawable d = ctx.getResources().getDrawable(id);
		Bitmap b = BitmapUtil.drawableToBitmap(d);
//		Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
		int w = ctx.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
		int h = ctx.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);
		
		Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);
		
		return thumBitmap;
	}
	public void setSelectIndex(int i){
		selectIndex = i;
	}
}
