package projections.Actions;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.Callable;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.aggregate.Avg;
import ch.lambdaj.function.aggregate.Sum;
import projections.monitoringObjects.DataCollection;
import projections.monitoringObjects.valueConstraint;
import projections.var;

import static ch.lambdaj.Lambda.avg;
import static ch.lambdaj.Lambda.count;
import static ch.lambdaj.Lambda.sum;

/*
 an abstract class that represent a single action to do like :
 question/recommendation/notification/measure/ measure reminder ...
 every action is extends thread class(or callable )

 the call method is like the Run method implemented in threads however,
 it returns a value. the returns value is a msg to be sent to the Gui.

 in this class there are also some constants(enums) for monitioring action
 ===========================================================================
 * AggregationAction - ( Sum, Avg, Count) - actions to accoumlate if we have conditions
                    (i.e we want to count how many abnormal BP we had)

 * AggregationOperators - (Equal, GreaterThen, LessThen, GreatEqual, LessEqual)
 *                          =, > , < , >= , <=
     operations for the aggregation (i.e we want to trigger something only if
     the count for abnormal BP is greaterthan(>) 4)

  *
  *Actor - the antity to whom we need to sent the action
  *
 */
public  abstract class Action  implements Callable<Message> {


    protected String actionName;
    protected String actionConcept;

    protected ActionType type;
    protected String msgToSend;
    protected Context context;

    protected boolean mIsBound = false;

    protected boolean isReminder;

    protected   Actor _actor;



    public enum AggregationAction {
        Sum, Avg, Count
    }

    public enum AggregationOperators {
        Equal, GreaterThen, LessThen, GreatEqual, LessEqual
    }

    public enum ActionType {
        Question, Recommendation, Notification, Measurement, CallBack, Remainder,Trigger
    }

    public enum Actor {
        Patient, physician
    }

    public Action(ActionType _type, String name, String concept) {

        actionName = name;
        type = _type;
        actionConcept = concept;

        context = null;

        isReminder=false;
        //===========================
        //TODO:Add implementaion for :
        _actor=null;

    }


    public Action(ActionType _type, String name, String concept, Context _context) {

        actionName = name;
        type = _type;
        actionConcept = concept;

        context = _context;

        isReminder=false;
        //===========================
        //TODO:Add implementaion for :
        _actor=null;

    }


    public String getActionName() {
        return actionName;
    }

    public String getConcept() {
        return actionConcept;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType actionType) {
        type = actionType;
    }

    public void setActor(Actor a) {
        _actor=a;
    }

    public abstract void setOnReceiveConcept(String onReceiveOp,String concept);







}
