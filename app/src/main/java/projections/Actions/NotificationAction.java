package projections.Actions;


import android.content.Context;

public class NotificationAction extends Action {

    Actor _actor;

    public NotificationAction(String notificationTxt, String concept, Actor actor,Context c) {
        super(ActionType.Notification, notificationTxt, concept, c);
        _actor=actor;
    }


    @Override
    public void doAction() {

    }


}
