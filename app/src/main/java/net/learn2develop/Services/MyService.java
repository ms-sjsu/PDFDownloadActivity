package net.learn2develop.Services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {	
	int counter = 0;
	public URL[] urls;
	private static final int  MEGABYTE = 1024 * 1024;
	
	static final int UPDATE_INTERVAL = 1000;
	private Timer timer = new Timer();

	private final IBinder binder = new MyBinder();
	
	public class MyBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		//return null;
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly    
		// stopped, so return sticky.
        Toast.makeText(this, "Download Started", Toast.LENGTH_LONG).show();

        Object[] objUrls = (Object[]) intent.getExtras().get("URLs");   
        URL[] urls = new URL[objUrls.length];
        for (int i=0; i<objUrls.length; i++) {
        	urls[i] = (URL) objUrls[i];
        }        
    	new DoBackgroundTask().execute(urls);	
        
		return START_STICKY;
	}	

	private void doSomethingRepeatedly() {
		timer.scheduleAtFixedRate( new TimerTask() {
			public void run() {
                Log.d("MyService", String.valueOf(++counter));
                try {
					Thread.sleep(4000);
	                Log.d("MyService", counter + " Finished");

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0, UPDATE_INTERVAL);
	}
		
	@Override
    public void onDestroy() {
        super.onDestroy();     
        if (timer != null){
        	timer.cancel();
        }
        Toast.makeText(this, "Download Stopped", Toast.LENGTH_LONG).show();
    }
	
	private int DownloadFile(URL url, File directory) {
		int file_size = 0;
		try {

			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.connect();

			file_size = urlConnection.getContentLength();
			InputStream inputStream = urlConnection.getInputStream();
			FileOutputStream fileOutputStream = new FileOutputStream(directory);

			byte[] buffer = new byte[MEGABYTE];
			int bufferLength = 0;
			while((bufferLength = inputStream.read(buffer))>0 ){
				fileOutputStream.write(buffer, 0, bufferLength);
			}
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file_size;
	}	

	private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {        
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {

				String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
				File folder = new File(extStorageDirectory, "PDFDownloadActivity");
				folder.mkdir();

				File pdfFile = new File(folder, "PDF_" + (i + 1) + ".pdf");

				try{
					pdfFile.createNewFile();
				}catch (IOException e){
					e.printStackTrace();
				}

                totalBytesDownloaded += DownloadFile(urls[i], pdfFile);
                //---calculate precentage downloaded and 
                // report its progress---
                publishProgress((int) (((i+1) / (float) count) * 100));                
            }
            return totalBytesDownloaded;
        }

        protected void onProgressUpdate(Integer... progress) {        	            
        	Log.d("Downloading files", 
        			String.valueOf(progress[0]) + "% downloaded");
        	Toast.makeText(getBaseContext(), 
        			String.valueOf(progress[0]) + "% downloaded", 
        			Toast.LENGTH_LONG).show();
        }

        protected void onPostExecute(Long result) {
        	Toast.makeText(getBaseContext(), 
        			"Downloaded " + result + " bytes", 
        			Toast.LENGTH_LONG).show();
        	stopSelf();
        }        
	}
}