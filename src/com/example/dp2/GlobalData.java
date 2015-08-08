package com.example.dp2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import Cinema.CinemaInfo;
import android.app.Application;
import android.util.Log;

public class GlobalData extends Application{
	private JSONArray cinemaJsonArray ;
	private ArrayList<CinemaInfo> cinemaInfos = new ArrayList<CinemaInfo>();
	
	public void setCinemaJsonArray(JSONArray jsonArray){
		cinemaJsonArray = jsonArray;
	}
	
	public JSONArray getCinemaJsonArray(){
		return cinemaJsonArray;
	}
	
	public void setCinemaDataByJsonArray(JSONArray jsonArray){
		int i = 0;
		int len = jsonArray.length();
		try{	
			for(i=0;i<len;i++){
				CinemaInfo cinemaInfo = new CinemaInfo();
				//cinemaInfo.setImgUrl( jsonArray[i].getString(""));
				JSONObject oj = jsonArray.getJSONObject(i);
				
				cinemaInfo.setImgUrl(oj.getString("photo_url"));
				
				Log.d("wzdebug","url is ---" + oj.getString("photo_url") +">>>>>>" + oj.getString("business_id")+">>>>>>>>>>"
						+oj.getString("latitude")+">>>>>>>>>>>>>"+oj.getString("longitude")+">>>>>>>>>"+oj.getString("address")
						+">>>>>>>>>>>>"+oj.getString("name"));
				
				cinemaInfo.setBusinessId(oj.getString("business_id"));
				cinemaInfo.setLatitude(oj.getString("latitude"));
				cinemaInfo.setLongitude(oj.getString("longitude"));
				cinemaInfo.setLocation(oj.getString("address"));
				
				//ȥ����������Ķ���
				String nameString = oj.getString("name");
				if(nameString.indexOf("(")!=-1){
					String nameString2 = nameString.substring(0, nameString.indexOf("(") );
					cinemaInfo.setName(nameString2);
				}else{
					cinemaInfo.setName(nameString);
				}
				
				cinemaInfos.add(cinemaInfo);
			}
		}catch(Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
	
	public ArrayList<CinemaInfo> getCinemaData(){
		return cinemaInfos;
	}
	
	
	/***
	 * ��ȫ�������и���  bussinessID ���ҵ�ӰԺ   
	 * @param bussinessId
	 * @return  �ҵ��˾ͷ��ص�ӰԺ����û�ҵ��ͷ���null
	 */
	public CinemaInfo findCinemaById(String bussinessId){
		CinemaInfo result;
		int len = cinemaInfos.size();
		for(int cnt =0 ; cnt < len ; cnt++){
			result = cinemaInfos.get(cnt);
			if( result.getBusinessId().equals(bussinessId) )
				return result;
		}
		
		return null;
	}
	
}
