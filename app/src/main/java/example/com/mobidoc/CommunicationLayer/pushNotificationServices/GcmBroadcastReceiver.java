/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.com.mobidoc.CommunicationLayer.pushNotificationServices;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.util.Xml;

import example.com.mobidoc.MsgRecieverService;


/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();


        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            String Receivedmsg = extras.getString("message");
            boolean isProjectionMsg = Receivedmsg.contains("Projection");
            ComponentName comp = null;
            // checking if received new projections or getting recommendation/qeustion/notification msg
            // from  the server
            if (isProjectionMsg) {
                // Explicitly specify that GcmIntentService will handle the intent.
                comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
                startWakefulService(context, (intent.setComponent(comp)));
                setResultCode(Activity.RESULT_OK);
            } else
                // in case we receive  recommendation/qeustion/notification msg from  the server
                // we wiil send it to the message handler
                comp = new ComponentName(context.getPackageName(), MsgRecieverService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
        }
    }

    // Start the service, keeping the device awake while it is launching.


}




