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

/**============================================================================================
 * holds many actions to be preformed
 *  the actions can be executed in  parallel / sequential order .
 *  this can be done in the actionExecutor class that determine the
 *  execute order for the actions.
 *
 *  * conceptsToMonitor - a collection of concepts . this collection holds all the spacial
 *                      concepts that we need to register for.
 *                      the projection class used in that collection . this is how it knows to
 *                      which concepts to register.
 *                      we can add a   concept to the collection when :
 *                      a) every time we add an  action to the composite action we check if we need
 *                      to add its concept to this  collection.
 *                      b) by the method 'addConceptToMonitor'
 *
 =================================================================================================*/
public class compositeAction  {

    private String name;
    public ArrayList<Action> actionsCollection;
    private Context context;
    private Vector<String> conceptsToMonitor;


   private actionExecutor ex;
    public compositeAction(Context c) {
        actionsCollection= new ArrayList<Action>( );
        context =new ContextWrapper(c);
        conceptsToMonitor=new Vector<String>();
       ex=null;


    }
    // set and add concepts for the spacieal actions (recommendation/qeustion)
    // add only these two kind of actions
    //
    public void addConceptForOnRecieve(String concept) {conceptsToMonitor.add(concept); }


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
        executed the tasks according to the execute order that  was defined
        this method get as parameter a boolean 'runReminder'
        the boolean determines if we need to ivoke the reminder
        also in this execution or not.
        runReminder==true => the executor invoke only the reminders
         runReminder==true => the executor invoke  the action and NOT the reminders
     */

    public void invoke(boolean runReminder) {

        Runnable r = null;
        ex.setRunReminder(runReminder);
       this.ex.execute(r);



    }

    /*
        add the concept to the concepts collection who need to be registered and monitored
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

        //need to register to concepts only if the action is Measurement
        if(action.getType().equals(Action.ActionType.Measurement))
             addConceptToMonitor(action.getConcept());

    }
}
