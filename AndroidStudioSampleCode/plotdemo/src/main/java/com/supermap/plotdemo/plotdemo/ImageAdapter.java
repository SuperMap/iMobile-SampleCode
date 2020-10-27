package com.supermap.plotdemo.plotdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.supermap.plotdemo.R;

/**
 * Created by Administrator on 2015/7/20.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mLayoutInflater;
    private List<? extends Map<String, String>> mData;
    private List<Map<String, Object>> iMageData;
    private Options mBitMapOptions = null;
    
    public ImageAdapter(Context context, List<? extends Map<String, String>> list){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mData = list;
        mBitMapOptions = new Options();
        mBitMapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mBitMapOptions.inPurgeable = true;
        mBitMapOptions.inInputShareable = true;
       
        initImageData();
        
    }

    private void initImageData(){
    	 iMageData = new ArrayList<Map<String,Object>>();
    	for(int i=0;i<mData.size();i++){
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("name", mData.get(i).get("name"));
    		String path = mData.get(i).get("path");
    		
    		 File imageFile = new File(path);
             if(!imageFile.exists())
             	continue;
            imageFile = null;
            Bitmap bitmap = BitmapFactory.decodeFile(path,mBitMapOptions);
     		map.put("bitmap", bitmap);
    		iMageData.add(map);
    	}
    }
    public int getCount(){
        return mData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        TextView textView;
        if(convertView != null){
        	recycleView(convertView);
        	convertView = null;
        }
        if(convertView == null ){ 
        	convertView = mLayoutInflater.inflate(R.layout.image_item, null);
        	
        } 
        imageView = (ImageView) convertView.findViewById(R.id.image);
    	textView = (TextView) convertView.findViewById(R.id.text);
        imageView.setImageBitmap((Bitmap)iMageData.get(position).get("bitmap"));
        textView.setText((String)iMageData.get(position).get("name"));
       
        return  convertView;
    }

    private void recycleView(View view) {
		// TODO Auto-generated method stub
		if (view.getDrawingCache() != null){
			view.getDrawingCache().recycle();
			Log.e("", "recycled");
		}
	}


}
