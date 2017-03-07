package com.example.admin.networkimageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.admin.networkimageloader.utils.ImageLoader;
import com.example.admin.networkimageloader.utils.Images;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ListImgsFragment extends Fragment
{
	private GridView mGridView;
	private String[] mUrlStrs = Images.imageThumbUrls;
	private List<String> mlist=new ArrayList<>();
	private ImageLoader mImageLoader;
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
		mImageLoader.clearDiskCache(getActivity());
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int size = mGruopMap.size();
				Iterator iter = mGruopMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					List<String> val = (List<String>) entry.getValue();
					mlist.addAll(val);
					Log.d("手机图片key", key);
					for (String str : val) {
						Log.d("手机图片val", str);
					}

				}
				mlist.clear();
				mlist.addAll(Arrays.asList(mUrlStrs));
				ListImgItemAdaper adapter= (ListImgItemAdaper) mGridView.getAdapter();adapter.notifyDataSetChanged();
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = getActivity().getContentResolver();

				//只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

				if (mCursor == null) {
					return;
				}

				while (mCursor.moveToNext()) {
					//获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					//获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();


					//根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}

				//通知Handler扫描图片完成
				mHandler.sendEmptyMessage(123);
				mCursor.close();
			}
		}).start();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_list_imgs, container,
				false);
		mGridView = (GridView) view.findViewById(R.id.id_gridview);
		setUpAdapter();
		return view;
	}

	private void setUpAdapter()
	{
		if (getActivity() == null || mGridView == null)
			return;

		if (mUrlStrs != null)
		{
			mGridView.setAdapter(new ListImgItemAdaper(getActivity(), 0,
					mlist));
		} else
		{
			mGridView.setAdapter(null);
		}

	}

	private class ListImgItemAdaper extends ArrayAdapter<String>
	{

		public ListImgItemAdaper(Context context, int resource, List<String> datas)
		{
			super(getActivity(), 0, datas);
			Log.e("TAG", "ListImgItemAdaper");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.item_fragment_list_imgs, parent, false);
			}
			ImageView imageview = (ImageView) convertView
					.findViewById(R.id.id_img);
			imageview.setImageResource(R.drawable.pictures_no);
			mImageLoader.loadImage(getItem(position), imageview,true);
			return convertView;
		}

	}

}