package com.example.projection;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

public abstract class projection {

	
	  private BlockingQueue<String> _q;
	  private ProjectionType Type;
	  private Vector<Calendar> calanders;
	  
	  public enum ProjectionType {
		    Cyclic, Monitor, Question, Recommendation
		 
		}

	public projection(ProjectionType type)
	{
		Type=type;
		calanders=new Vector<Calendar>();
		Calendar c=Calendar.getInstance();
		calanders.add(c);
		
	}
	
	
	public  Calendar getTimer()
	{
		return calanders.get(0);
		
	}
	
	
	public  void SetTime(int year,int month,int day,int hour,int minute,int sec)
	{
		calanders.get(0).set(year, month, day, hour, minute, sec);
		
	}
	
	
	
	public abstract void setTimer();
	
	public  void SendMsg(String msg)
	{
		Runnable msgSender=generateMsgSenderRunnable(msg);
		
		Thread sender=new Thread(msgSender);
		
		sender.start();
	}
	
	
	
	
	
	
	private   Runnable generateMsgSenderRunnable(final String msg)
	{
		Runnable msgSender = new Runnable() {
		       public void run() { 
		    	   
				try {
					Thread.sleep(500);
					_q.put(msg);
				} catch (InterruptedException e) {
				System.out.println("error while sending a msg to the queue");
				}
		      };};
		      
		      
		    return msgSender;
	}

	
	
	
	
	
	
	
}
