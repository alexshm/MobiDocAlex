package example.com.mobidoc;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class t5 implements Runnable {

	private int index;
	private BlockingQueue<String> _q;
	public t5() {
		System.out.println("constructor");
		index=0;
		
	}
	public String init(BlockingQueue<String> q)
	{
		
		_q=q;
		index=0;
		
		return "good";
	}
	public void start()
	{ 
		//Thread p=new Thread(beeper);
		//p.start();
	}
	
	private final ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);

	final Runnable beeper = new Runnable() {
	       public void run() { 
	    	   
			try {
				while(true)
		    	   {
					
					Thread.sleep(1000);
					_q.put("beep_"+index);
					//System.out.println("sssssssssssssssssssssssssssssssssssssssssssssssssss");
				index++;
				
		    	   }
			} catch (InterruptedException e) {
				
				
				
			}
			
	    	  
	      };};
	    
	
	  
	     
	 final  Runnable t= new Runnable() {
	       public void run() {// beeperHandle.cancel(true);
	       System.out.println("wait ");}
	 };
	   
	
		 
	@Override
	public void run() {
		
		final ScheduledFuture<?> beeperHandle =
			      scheduler.scheduleAtFixedRate(beeper, 2, 3, SECONDS);
	}
	}

	
		