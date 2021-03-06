package com.example.erowidexperiencevault;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class StoryListActivity extends ListActivity{
	StoryAdapter mStoryAdapter;
	ArrayList<Story> mStoryList;
	ProgressBar loading;
	LinearLayout mainLayout;
	Menu optionsMenu;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_list);
		
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
		
		
		String sub_name = getIntent().getStringExtra("name");
		int sub_id = getIntent().getIntExtra("id", 0);
		
		
		loading = (ProgressBar) findViewById(R.id.progress);
		mainLayout = (LinearLayout) findViewById(R.id.linear);
		TextView titleBar = (TextView) findViewById(R.id.name);
		titleBar.setText(sub_name);
		
        mStoryList = new ArrayList<Story>();
        
        new getStories().execute("" + sub_id);
     
	}
	
	public void onListItemClick(ListView listview, View view, int position, long id){
		Story story = mStoryAdapter.getItem(position);
		
		Intent i = new Intent(this, FullStoryActivity.class);
		i.putExtra("link", story.getLink());
		startActivity(i);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    this.optionsMenu = menu;
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.top_menu, menu);
	    
        return super.onCreateOptionsMenu(menu);
	}
	
    public boolean onOptionsItemSelected(MenuItem item){       
	    switch (item.getItemId()) {
	    case R.id.menuRefresh:
	    	// Complete with your code
	    	return true;
	    	
        case android.R.id.home:
            this.finish();
            return true;
	    }
	    return super.onOptionsItemSelected(item);
    }
	
    
	private class StoryAdapter extends ArrayAdapter<Story>{
		
		public StoryAdapter(Context context, int resource, int textViewResourceId, List<Story> objects){
			super(context, resource, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			RelativeLayout layout = (RelativeLayout) super.getView(position, convertView, parent);
			TextView rating = (TextView) layout.findViewById(R.id.rating);
			TextView title = (TextView) layout.findViewById(R.id.title);
			TextView author = (TextView) layout.findViewById(R.id.author);
			TextView subs = (TextView) layout.findViewById(R.id.substances);
			TextView date = (TextView) layout.findViewById(R.id.date);
			
			rating.setText(getItem(position).getRating());
			title.setText(getItem(position).getTitle());
			author.setText("Author: " + getItem(position).getAuthor());
			subs.setText("Substances: " + getItem(position).getSubstances());
			date.setText("Date: " + getItem(position).getDate());
			
			return layout;
		}
	
	}
    
    
	public class getStories extends AsyncTask<String, Void, ArrayList<Story>>{
		
		protected ArrayList<Story> doInBackground(String... id){	
			String URL = "http://www.erowid.org/experiences/exp.cgi?S1=" + id[0] + "&S2=-1&C1=-1&Str=";
			String pageText = "";
	    	
			
	    	HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(URL);
			try{
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				
				if(null != entity){
					pageText = EntityUtils.toString(entity);
				}
			}catch(ClientProtocolException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			
			
			ArrayList<Story> stories = new ArrayList<Story>();
			
			
			int start = 0;
			int end = 0;
			
			int i = 0;
			while(true){
			
				i++;
				if(i > 50){
					break;
				}
				
				start = pageText.indexOf("<tr class=\"\">", start);
				if(start <= 0){
					break;
				}
				
				
				//checking if an image exists for the rating
				int temp_start = pageText.indexOf("d>", start);
				int temp_end = pageText.indexOf("</td>", start);
				
				String temp_rat = pageText.substring(temp_start, temp_end);
				
				String rating;
				if(temp_rat.indexOf("img") > 0){
					temp_start = pageText.indexOf("star_", temp_start) + 5;
					temp_end = pageText.indexOf(".", temp_start);
					
					rating = pageText.substring(temp_start, temp_end);
					
				}else{
					rating = "0";
				}
				
				
				start = pageText.indexOf("href=\"", start) + 6;
				end = pageText.indexOf("\"", start);
				String link = pageText.substring(start, end);
				
				start = pageText.indexOf(">", start) + 1;
				end = pageText.indexOf("<", start);
				String title = pageText.substring(start, end);
				
				start = pageText.indexOf("<td>", start) + 4;
				end = pageText.indexOf("<", start);
				String author = pageText.substring(start, end);
				
				start = pageText.indexOf("<td>", start) + 4;
				end = pageText.indexOf("<", start);
				String subs = pageText.substring(start, end);
				
				start = pageText.indexOf("\">", start) + 2;
				end = pageText.indexOf("<", start);
				String date = pageText.substring(start, end);
				
				//Example of the table structure from the Erowid Stories
				//<tr class=""><td> <img align="right" src="images/exp_star_2.gif" alt="Highly Recommended" border="0"></td><td><a href="exp.php?ID=53780">I Am Not Hardcore: Or Important Safety Tip</a></td><td>maison</td><td>Alcohol, Ketamine & 1,4-butanediol</td><td align="right">May 24 2007</td>
			
				
				start = end;
				
				//Log.e("OUTPUT", "(" + rating + ", " + link + ", " + title + ", " + author + ", " + subs + ", " + date + ")");
				
				stories.add(new Story(rating, link, title, author, subs, date));
			}
			
			mStoryList = stories;
			mStoryAdapter = new StoryAdapter(StoryListActivity.this, R.layout.story_item, R.id.title, mStoryList);
			StoryListActivity.this.setListAdapter(mStoryAdapter);
			
			return stories;
		}
		
    	protected void onPostExecute(ArrayList<Story> result){
			loading.setVisibility(View.GONE);
			mainLayout.setVisibility(View.VISIBLE);
    	}
    	
    	protected void onPreExecute(){
			loading.setVisibility(View.VISIBLE);
			mainLayout.setVisibility(View.GONE);
    	}
	}

}