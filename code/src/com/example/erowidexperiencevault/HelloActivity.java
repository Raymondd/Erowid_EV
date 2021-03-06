package com.example.erowidexperiencevault;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HelloActivity extends ListActivity{
	SubAdapter mSubAdapter;
	ArrayList<Substance> mSubList;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hello);
		
		ProgressBar loading = (ProgressBar) findViewById(R.id.progress);
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.linear);
		EditText search = (EditText) findViewById(R.id.search_term);
		
        mSubList = new ArrayList<Substance>();
        
        try {
			mSubList = (new SubstanceNames().execute()).get();
			loading.setVisibility(View.GONE);
			mainLayout.setVisibility(View.VISIBLE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
        
		mSubAdapter = new SubAdapter(this, R.layout.sub_item, R.id.sub_name, mSubList);
		setListAdapter(mSubAdapter);
		
		search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {};
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {};
			
			@Override
			public void afterTextChanged(Editable s){
				if(s.length() > 0){
					ArrayList<Substance> newSubList = new ArrayList<Substance>();
					for(int i = 0; i < mSubList.size(); i++){
						if((mSubList.get(i).getName().toLowerCase()).contains((s.toString().toLowerCase()))){
							newSubList.add(mSubList.get(i));
						}
					}
					mSubAdapter = new SubAdapter(HelloActivity.this, R.layout.sub_item, R.id.sub_name, newSubList);
					HelloActivity.this.setListAdapter(mSubAdapter);
				}else{
					mSubAdapter = new SubAdapter(HelloActivity.this, R.layout.sub_item, R.id.sub_name, mSubList);
					HelloActivity.this.setListAdapter(mSubAdapter);
					
				}
			};
		});
		
	}
	
	private class SubAdapter extends ArrayAdapter<Substance>{
		
		public SubAdapter(Context context, int resource, int textViewResourceId, List<Substance> objects){
			super(context, resource, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			RelativeLayout layout = (RelativeLayout) super.getView(position, convertView, parent);
			//View thumbnail = layout.findViewById(R.id.colorThumbnail);
			//thumbnail.setBackgroundColor(getItem(position).mColor);
			TextView name = (TextView) layout.findViewById(R.id.sub_name);
			name.setText(getItem(position).getName());
			return layout;
		}
	
	}
	
	public void onListItemClick(ListView listview, View view, int position, long id){
		/*Color color = mColorAdapter.getItem(position);
		int hexColor = color.mColor;
		
		Intent i = new Intent();
		i.putExtra("color", hexColor);
		setResult(Activity.RESULT_OK, i); //(resultCode, data)
		finish();*/
	}

}

