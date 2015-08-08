package fragment;


import java.util.ArrayList;
import java.util.Arrays;

import com.efor18.rangeseekbar.RangeSeekBar;
import com.efor18.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.example.dp2.R;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class PlanFragment extends Fragment{

	//wz
	private ArrayAdapter<String> adapter;
	
	private DragSortListView.DropListener onDrop =
	        new DragSortListView.DropListener() {
	            @Override
	            public void drop(int from, int to) {
	                if (from != to) {
	                    DragSortListView list = (DragSortListView) getView().findViewById(R.id.plan_select_listview);//getListView();
	                    String item = adapter.getItem(from);
	                    adapter.remove(item);
	                    adapter.insert(item, to);
	                    list.moveCheckState(from, to);
	                }
	            }
	        };

    private RemoveListener onRemove =
        new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                DragSortListView list = (DragSortListView) getView().findViewById(R.id.plan_select_listview);//getListView();
                String item = adapter.getItem(which);
                adapter.remove(item);
                list.removeCheckState(which);
            }
        };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.plan_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);		

		//添加  rangeSeekBar 到 布局
		View view = getView();
		final TextView min = (TextView)view.findViewById(R.id.plan_minValue);
		final TextView max = (TextView)view.findViewById(R.id.plan_maxValue);
		min.setText("8:00");
		max.setText("20:00");

		//项目选择初始化
		
		String[] array = getResources().getStringArray(R.array.jazz_artist_names);
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(array));

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_checkable, R.id.text, arrayList);
        
        //setListAdapter(adapter);
        
        
        DragSortListView list = (DragSortListView) getView().findViewById(R.id.plan_select_listview);
        
        list.setAdapter(adapter);
        
        list.setDropListener(onDrop);
        
        list.setRemoveListener(onRemove);
		
		
		//时间选择初始化
		Log.d("wzdebug", "before seekbar");
		final RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(480,
				1200, getActivity().getApplicationContext());//
		Log.d("wzdebug", "after seekbar");
		
		
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				// handle changed range values
				Log.i("wzdebug", "User selected new range values: MIN=" + minValue 
						+ ", MAX=" + maxValue);
				System.out.println(minValue + "->");
				String min1 = change(minValue);
				String max1 = change(maxValue);
				min.setText(min1);
				max.setText(max1);
			}
		});
		
		// add RangeSeekBar to pre-defined layout
		ViewGroup layout = (ViewGroup) view.findViewById(R.id.seekbar_layout);
		layout.addView(seekBar);
		
		

		
	}
	public String change(int minute) {
		int h = 0;
		int d = 0;
		d = minute % 60;
		if (minute > 60) {
			h = minute / 60;
		}
		if (d == 0) {
			return h + ":00";
		}
		return h + ":" + d;
	}
}
