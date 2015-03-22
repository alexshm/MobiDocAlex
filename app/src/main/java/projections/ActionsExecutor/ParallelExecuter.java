package projections.ActionsExecutor;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import projections.Actions.Action;

/**
 * Created by Moshe on 3/3/2015.
 */
public class ParallelExecuter extends actionExecutor implements Executor {


    public ParallelExecuter(Context c) {
            super(c);
        pool=Executors.newFixedThreadPool(3);
        startService();

    }

    @Override
    protected void executeActions() {
        try {
            Log.i("parrallel exiecuter- execute actions", " executing the actions ");
            msgsrstlt= pool.invokeAll(tasks);
            send();
            clean();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void executeReminders() {
        try {
            Log.i("parrallel exiecuter- execute reminders", " executing the reminders ");
            msgsrstlt= pool.invokeAll(Reminderstasks);
            send();
            clean();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

