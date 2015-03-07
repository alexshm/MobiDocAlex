package projections.ActionsExecutor;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import projections.Actions.Action;


public class SerialExecutor extends actionExecutor implements Executor {


        public SerialExecutor(Context c) {
            super(c);
            pool = Executors.newSingleThreadExecutor();
            msgsrstlt=new ArrayList<Future<Message>>();
            startService();

        }

        public  void execute( Runnable r) {

            for (Action task:tasks)
            {
                Log.i("Sequncial exiecuter-excute"," executing the action : "+task.getActionName());
                Future rslt=pool.submit(task);
                msgsrstlt.add(rslt);
                send();
                clean();


            }



        }



}
