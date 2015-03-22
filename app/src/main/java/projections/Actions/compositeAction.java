package projections.Actions;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import projections.ActionsExecutor.*;

import projections.Utils;


public class compositeAction  {

    public ArrayList<Action> actionsCollection;
    private Context context;
    private Vector<String> conceptsToMonitor;


   private actionExecutor ex;

    public compositeAction(Context c, Utils.ExecuteMode mode) {
        actionsCollection= new ArrayList<Action>( );
        context =new ContextWrapper(c);
        conceptsToMonitor=new Vector<String>();
        if (mode.equals(Utils.ExecuteMode.Sequential))
            ex=new SerialExecutor(c);
        else
            ex=new ParallelExecuter(c);


    }
    public Vector<String>getAllConcepts()
    {
        return conceptsToMonitor;
    }


    public void setExecuteMode(Utils.ExecuteMode executeMode)
    {
        if (executeMode.equals(Utils.ExecuteMode.Sequential))
            ex=new SerialExecutor(context);
        else
            ex=new ParallelExecuter(context);
    }

    /*
        executed the tasks according to the executed who was defined

     */
    public void invoke(boolean runReminder) {

        Runnable r = null;
        ex.setRunReminder(runReminder);
        this.ex.execute(r);

    }

    /*
        add the concept to the concepts list
     */
    private void addConceptToMonitor( String conceptId)
    {
        if(!conceptsToMonitor.contains(conceptId))
            conceptsToMonitor.add(conceptId);

    }


    /*
    *  add the action to the list of actions
    *  and also adds the action to the tasks to be  executed on the
    *  executer .
    *
    *   * also add the concept of the Action and adds it to the concepts list
    *       that will need to monitored.

     */
    public void addAction(Action action)
    {

        actionsCollection.add(action);
        this.ex.addAction(action);
        addConceptToMonitor(action.getConcept());
    }
}
