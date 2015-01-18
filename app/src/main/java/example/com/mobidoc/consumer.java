package example.com.mobidoc;

import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.widget.Toast;

public class consumer implements Runnable{
	private Context c;

	 public consumer(Context _c){
		 
	        c=_c;
	        Toast.makeText(c,"consumer constructor", Toast.LENGTH_SHORT).show();
	    }
	 
	 public void test( BlockingQueue _q1)
	 {
	 final  BlockingQueue q1=_q1;
	 
	 t5 inst=new t5();
	 Thread qu=new Thread(inst);
	 inst.init(q1);
     try {
         /*final String libPath = Environment.getExternalStorageDirectory() + "/makejar.jar";
         final File tmpDir = getDir("dex", 0);

         final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
         final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("com.example.makejar.test");
       
*/
         final Runnable beeper = new Runnable() {
     	       public void run() { 
     	    	 while (true) {
                     String s;
                     try {
                         s = (String) q1.take();

                         Toast.makeText(c, "element from queue : " + s + " num of elements in queqe: " + q1.size(), Toast.LENGTH_SHORT).show();

                     } catch (InterruptedException e) {
                         Toast.makeText(c, "error queue not emmpty", Toast.LENGTH_SHORT).show();
                         e.printStackTrace();


                     }
                 }}};
     	Thread w=new Thread(beeper);
     	
	      	      
	      	
     	/*
         final Object myInstance  = classToLoad.newInstance();
          Method initmeth  = classToLoad.getMethod("init",params);
          initmeth.setAccessible(true);
          final  Thread pp=new Thread((Runnable) myInstance);
          String res=(String)initmeth.invoke(myInstance,new Object[]{ q1 }); 
          t.setText("before executing  : "+res);
          Method start  = classToLoad.getMethod("start");
        
          start.invoke(myInstance);
          */
     	 w.start();
     	qu.start();
         
         
     } catch (Exception e) {
    	 Toast.makeText(c,"error consumer main : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
     	
        
     }
     }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	 }
	 


