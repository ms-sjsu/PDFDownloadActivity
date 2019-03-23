package net.learn2develop.Services;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	IntentFilter intentFilter;
	private MyService serviceBinder;
	Intent i;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //---intent to filter for file downloaded intent---
        intentFilter = new IntentFilter();
        intentFilter.addAction("FILE_DOWNLOADED_ACTION");

        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        
        Button btnStart = (Button) findViewById(R.id.btnStartService);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	Intent intent = new Intent(getBaseContext(), MyService.class);

            	EditText pdf1=(EditText)findViewById(R.id.pdf1_location);
				String pdf1_Location=pdf1.getText().toString();
				if(pdf1_Location==null || pdf1_Location.isEmpty()){
					pdf1_Location = "https://www.dummyurl.com/dummy.pdf";
				}

				EditText pdf2=(EditText)findViewById(R.id.pdf2_location);
				String pdf2_Location=pdf2.getText().toString();
				if(pdf2_Location==null || pdf2_Location.isEmpty()){
					pdf2_Location = "https://www.dummyurl.com/dummy.pdf";
				}

				EditText pdf3=(EditText)findViewById(R.id.pdf3_location);
				String pdf3_Location=pdf3.getText().toString();
				if(pdf3_Location==null || pdf3_Location.isEmpty()){
					pdf3_Location = "https://www.dummyurl.com/dummy.pdf";
				}

				EditText pdf4=(EditText)findViewById(R.id.pdf4_location);
				String pdf4_Location=pdf4.getText().toString();
				if(pdf4_Location==null || pdf4_Location.isEmpty()){
					pdf4_Location = "https://www.dummyurl.com/dummy.pdf";
				}

				EditText pdf5=(EditText)findViewById(R.id.pdf5_location);
				String pdf5_Location=pdf5.getText().toString();
				if(pdf5_Location==null || pdf5_Location.isEmpty()){
					pdf5_Location = "https://www.dummyurl.com/dummy.pdf";
				}

				try {
					URL[] urls = new URL[] {
							new URL(pdf1_Location),
							new URL(pdf2_Location),
							new URL(pdf3_Location),
							new URL(pdf4_Location),
							new URL(pdf5_Location)};
					intent.putExtra("URLs", urls);
					
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}            	
            	startService(intent);
            }
        });
        
        Button btnStop = (Button) findViewById(R.id.btnStopService);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                
            	stopService(new Intent(getBaseContext(), MyService.class));            	
            }
        });
    }    
    
    private ServiceConnection connection = new ServiceConnection() {
    	public void onServiceConnected(ComponentName className, IBinder service) {
    		//---called when the connection is made---
    		serviceBinder = ((MyService.MyBinder)service).getService(); 
    		
    		 try {
					URL[] urls = new URL[] {
							new URL("http://www.amazon.com/somefiles.pdf"), 
							new URL("http://www.wrox.com/somefiles.pdf"),
							new URL("http://www.google.com/somefiles.pdf"),
							new URL("http://www.learn2develop.net/somefiles.pdf")};
					serviceBinder.urls = urls;
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
				startService(i);
    	}
    	public void onServiceDisconnected(ComponentName className) {
    	    //---called when the service disconnects---
    		serviceBinder = null;    		
    	}
    };
    
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {  
	        Toast.makeText(getBaseContext(), "File downloaded!", 
	        		Toast.LENGTH_LONG).show();	  
	    }
	};    
}