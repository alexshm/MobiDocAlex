package example.com.mobidoc;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

@SuppressLint("ShowToast")
public class MainScreen extends Activity {
	 final BlockingQueue<String> q1 = new ArrayBlockingQueue<String>(1000);
	 private TextView t;
	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toast.makeText(getApplicationContext(), "welcome to MobiDoc", 2);
		//final EditText username=(EditText)findViewById(R.id.usernametxt);
		//final EditText pass=(EditText)findViewById(R.id.passtext);
		//final Button loginbtn=(Button)findViewById(R.id.loginButton);
		
	
		 t=(TextView)findViewById(R.id.textView2);
		 Class<?>[] params= new Class[]{BlockingQueue.class};
		
		 
		 Toast.makeText(this.getApplicationContext(),"projection is set every 30 sec", 2).show();
		 
		 // inst.init(q1);
		 // Thread qu=new Thread(inst);
		
		  //Toast.makeText(this.getApplicationContext(),"welocme", 1).show();
		 
			 
			
			
			
	        try {
	            final String libPath = Environment.getExternalStorageDirectory() + "/makejar.jar";
	            final File tmpDir = getDir("dex", 0);

	            final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
	            final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("com.example.makejar.test");
	          

	  		

	            final Object myInstance  = classToLoad.newInstance();
	             Method initmeth  = classToLoad.getMethod("init",params);
	             initmeth.setAccessible(true);
	            // final  Thread pp=new Thread((Runnable) myInstance);
	             String res=(String)initmeth.invoke(myInstance,new Object[]{ q1 }); 
	             //Toast.makeText(this.getApplicationContext(),"start the projection Test", 1).show();
	             //t.setText("before executing  : "+res);
	             Method start  = classToLoad.getMethod("start");
	           
	          showToastFromBackground("");
	           start.invoke(myInstance);
	            
	        	
	        	
	        	
	            
	            
	      	    

	      	     

	           // final Method doSomething = classToLoad.getMethod("beepForAnHour");

	           //doSomething.invoke(myInstance);
	           
	        } catch (Exception e) {
	       	 Toast.makeText(this.getApplicationContext(),"error consumer main : "+e.getLocalizedMessage(), 3).show();
	        	
	        
	        }
	      
	    }

	private void showToastFromBackground(final String message) {
		new Thread(new Runnable() {
	    //runOnUiThread

	        @Override
	        public void run() {
	        	
	        	
	        	while(true){
	        		final String s;
					try {
						s = (String) q1.take();
						//System.out.println("takkkkkkkkkkkkkkeee  : "+s);
						t.post(new Runnable() {

							@Override
							public void run() {
							//t.setText("recieve from proj: "+s);
							Toast.makeText(MainScreen.this.getApplicationContext(), "please remember to take your pill", 2).show();
							}
							
							
						});
						
					} catch (InterruptedException e) {
						Toast.makeText(MainScreen.this.getApplicationContext(), "error taking element", 2).show();
						//t.setText("error taking element");
					}
					 
	        	}
				
	        
	           
	        }
	    }).start();
	}

}