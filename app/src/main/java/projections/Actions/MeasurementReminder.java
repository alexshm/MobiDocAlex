package projections.Actions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 *  remiander obj
 */
public class MeasurementReminder extends Action {
    public MeasurementReminder(String measureName)
    {
        super(ActionType.Remainder,measureName, "Reminder");
        setType(ActionType.Remainder);
        //TODO:SubscribeConcept(conceptId);
        _actor=Actor.Patient;

    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    /*
    creating a message obj that will be sent to the msgHandler in
    the Gui component
    */

    @Override
    public Message call()  {
        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);
        bundle.putString("concept", this.getConcept());
        Log.i("reminder action- build reminder", "build msg for : " + actionName);
        msg.setData(bundle);
        return msg;
    }

}

