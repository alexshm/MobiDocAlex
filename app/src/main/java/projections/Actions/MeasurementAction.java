package projections.Actions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.Callable;

import projections.Utils;

public class MeasurementAction extends Action {

    public MeasurementAction(String measureName,  String conceptId,Context c)
    {
        super(ActionType.Measurement,measureName, conceptId, c);
        setType(ActionType.Measurement);
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

        // in case of raminder msg
        if (isReminder)
            msgType = ActionType.Remainder.ordinal() + 1;

       Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);
        bundle.putString("concept", this.getConcept());
        Log.i("mesure action- build msg","build msg for : "+actionName);
        msg.setData(bundle);
        return msg;
    }

}
