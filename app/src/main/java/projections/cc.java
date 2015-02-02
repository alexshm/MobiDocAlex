package projections;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class cc {

	
	public cc()
	{
		
	}
	 private final Lock lock = new ReentrantLock();

	    private final Condition notFull = lock.newCondition();
	    private final Condition notEmpty = lock.newCondition();




	//private projections.MonitorProjecction u=new projections.MonitorProjecction();
	//private projections.projection s=new projections.MonitorProjecction();
	

	public void f(){
		
		
	}
	
}
