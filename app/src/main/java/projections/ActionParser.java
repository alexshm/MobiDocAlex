package projections;

/**
 * Created by Moshe on 2/2/2015.
 */
public class ActionParser {

    private  Action _a;
     public  ActionParser(Action a) {
            _a=a;
     }

        public void parse()
        {
        try {
            String name = "projections.Action";
            String methodName = "doAction";

            // get String Class
            Class cl = Class.forName(name);

            // get the constructor with one parameter
            java.lang.reflect.Constructor constructor =
                    cl.getConstructor
                            (new Class[] {Action.class});

            // create an instance
            Object invoker =
                    constructor.newInstance
                            (new Object[]{"REAL'S HOWTO"});

            // the method has no argument
            Class  arguments[] = new Class[] { };

            // get the method
            java.lang.reflect.Method objMethod =
                    cl.getMethod(methodName, arguments);

            // convert "REAL'S HOWTO" to "real's howto"
            Object result =
                    objMethod.invoke
                            (invoker, (Object[])arguments);

            System.out.println(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

