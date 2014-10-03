package uk.me.timlittle.ask;

import java.util.Random;

import uk.me.timlittle.ask.ShakeEventManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**************************************************
 * 
 * @author TJL-Dell
 * 
 * Simple Android App that shows a random message when the phone is shaken
 */
public class MainActivity extends Activity  implements ShakeEventManager.ShakeListener {

	private List<String> ANSWER_LIST = new ArrayList<String>();
	 private int flips;
	 private int spinCounter;
	 private Handler customHandler = new Handler();
	 private int animationDelay = 50;
	 private Button btnFlip;
	 private ShakeEventManager sd;
	 private TextView textMessage;
	 private int stringCounter;
	 public String ANSWERS_PREFS = "ANSWERS";

	 private void readValues(){
		 String answerList;
		 String defaultList;
		 SharedPreferences pref;
		 		
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		//defaultList = "Yes;No;Of course!;Yuck!;Proceed with caution;Are they cute?;Don't go there!;Hmm, maybe";
		defaultList = getResources().getString(R.string.pref_default_answers);
		
		if (pref != null) {
			answerList = pref.getString(ANSWERS_PREFS, defaultList);
			
			answerList = answerList.trim();
			
			if (answerList.length() < 2){
				answerList = defaultList;
			}
			
			ANSWER_LIST = Arrays.asList(answerList.split(";"));
		}

		stringCounter = ANSWER_LIST.size();
	 }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        sd = new ShakeEventManager();
        sd.setListener(this);
        sd.init(this);
		
		btnFlip = (Button) findViewById(R.id.btnFlip);
		textMessage = (TextView)findViewById(R.id.textView1);
		
		if (btnFlip != null)
			btnFlip.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					Random r = new Random();
					

					flips = r.nextInt(20) + 2;
					textMessage.setText(ANSWER_LIST.get(spinCounter % stringCounter));
					
					spinCounter = 0;

					customHandler.postDelayed(updateImageThread, animationDelay);
				}
			});
		

		if (savedInstanceState != null){
			flips = savedInstanceState.getInt("flips");
			spinCounter = savedInstanceState.getInt("spinCounter");			
		} 
		
		readValues();
		//if we aren't there yet, carry on.
		if (flips != spinCounter){
			customHandler.postDelayed(updateImageThread, animationDelay);
		}
		
		//If the app has been interrupted start again from where we'd got to
		
	}

    @Override
    public void onShake() {
		Random r = new Random();
		
		textMessage.setText(ANSWER_LIST.get(spinCounter % stringCounter));
		
		flips = r.nextInt(20) + 2;
		spinCounter = 0;

		customHandler.postDelayed(updateImageThread, animationDelay);    	
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        
        readValues();
        
        sd.register();        
    }


    @Override
    protected void onPause() {
        super.onPause();
        sd.deregister();
    }
    
	@Override
	public void onSaveInstanceState(Bundle outState){
		
		outState.putInt("flips", flips);
		outState.putInt("spinCounter", spinCounter);
		
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			// Starts the Settings activity on top of the current activity
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

		return true;
	}

private Runnable updateImageThread = new Runnable() {

    public void run() {
    	
    	if (spinCounter < flips) {
    		
    		textMessage.setText(ANSWER_LIST.get(spinCounter % stringCounter));
			spinCounter++;
				
    		
    		customHandler.postDelayed(this, animationDelay);
    	}
    }
};
	
}
