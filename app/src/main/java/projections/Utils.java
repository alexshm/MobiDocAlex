package projections;



public  final class Utils {
    public static enum AggregationAction {
        Sum, Avg, Count
    }

    public static enum AggregationOperators {
        Equal, GreaterThen, LessThen, GreatEqual, LessEqual
    }

    public static enum ActionType {
        Question, Recommendation, Notification, Measurement, General, Remainder, Trigger
    }

    public static enum Actor {
        Patient, physician
    }
    public static enum ExecuteMode {
        Parallel, Sequential
    }

}
