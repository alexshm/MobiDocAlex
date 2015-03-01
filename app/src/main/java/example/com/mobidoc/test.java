package example.com.mobidoc;

import android.app.Activity;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class test   extends Activity{
	private Activity _activity;
	private TextView txt;
	private int index;
	
	private BlockingQueue<String> _q;
	
	private final ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);
	
	public void test() {
         ClassPool cp = ClassPool.getDefault();
     try
     {
            cp.insertClassPath(".");

         //to add to the pool

            cp.insertClassPath(new ClassClassPath(ClassGenerator.class));

            CtClass cls=cp.get("ClassGenerator");
            CtMethod mtd=cls.getDeclaredMethod("test");
            mtd.insertBefore("count=9;");
           Method c= cls.getClass().getMethod("test",null);


        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


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
	

	
		
	
}
	
	     
	     
	    
	     
	     

	


	
	


	
    
    
   
    
    


