package com.example.projection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


import android.app.Activity;
import android.widget.TextView;
import static java.util.concurrent.TimeUnit.*;
public class test   implements Runnable{
	private Activity _activity;
	private TextView txt;
	private int index;
	
	private BlockingQueue<String> _q;
	
	private final ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);
	
	public test() {
		
		 
		
	}
	
	
	
	public String init(BlockingQueue<String> q)
	{
		
		_q=q;
		index=0;
		
		return "good";
	}
	public void start()
	{
		final ScheduledFuture<?> beeperHandle =
			      scheduler.scheduleAtFixedRate(beeper,8 , 30, SECONDS);
	}
	
	

	final Runnable beeper = new Runnable() {
	       public void run() { 
	    	   
			try {
				
					
					Thread.sleep(500);
					_q.put("beep:"+index);
					
				index++;
				
		    	   
			} catch (InterruptedException e) {
				
				
				
			}
			
	    	  
	      };};
	    
	
	  
	     
	 final  Runnable t= new Runnable() {
	       public void run() { //beeperHandle.cancel(true);
	       txt.setText("wait   ");}
	 };
	   
	public void beepForAnHour() {
	     
		scheduler.schedule(t, 60 * 60, SECONDS);
	}
	
	@Override
	public void run() {
		
		
	}
	
		
	
}
	
	     
	     
	    
	     
	     

	


	
	


	
    
    
   
    
    


