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

/*
    This class define running the Actions in sequential order

    it has a list of tasks to be executed and a list of msgs to send to the
    MsgHandler defined in the GUI componenet.

    * the process for sending a msg is :
    when the execute Method invoked, it takes each task from  the tasks list to be executed
    and  after each task it put it in the msg queue and clear the list after the msg  has
    been sent.

    * the  startService() method defined in the super class 'actionExecuter'.
    this Method binding the Service for sending the msgs to the GUI.

 */
public class SerialExecutor extends actionExecutor implements Executor {


        public SerialExecutor(Context c) {
            super(c);
            pool = Executors.newSingleThreadExecutor();
            msgsrstlt=new ArrayList<Future<Message>>();
            this.Reminderstasks=new ArrayList<Action>( );
            this.tasks = new ArrayList<Action>( );
            startService();



        }
        @Override

         /*
            add a task to the task list
        */
        public  void addAction(Action a)
        {
            if(a.getType().equals(Action.ActionType.Remainder))
                this.Reminderstasks.add(a);
             else
                this.tasks.add(a);
        }

        @Override
         /*
        execute the tasks by sequencial order and send the msgs  one at a time to the
        Gui components
        send() - reads the list of msgs to be deliverd and send them to the GUI
                    by using the send Method in the Service
         */

    protected void executeActions() {
            for (Action task : this.tasks) {
                Log.i("Sequncial exiecuter-excute", " executing the action : " + task.getActionName());
                Future rslt = pool.submit(task);
                msgsrstlt.add(rslt);
                send();
                clean();


            }
        }

    /*
        execute the Reminders to be sent
        send them in sequencial order
     */
    @Override
    protected void executeReminders() {
        for (Action task : this.Reminderstasks) {
            Log.i("Sequncial Reminders exiecuter-excute", " executing the action : " + task.getActionName());
            Future rslt = pool.submit(task);
            msgsrstlt.add(rslt);
            send();
            clean();


        }
    }


}
