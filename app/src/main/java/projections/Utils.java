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
    public static enum ProjectionType {
        Cyclic, Monitor
    }

    public enum ProjectionTimeUnit {
        Second, Minute, Hour, Day, Week,Month ,None
    }

    public static projection.ProjectionTimeUnit getTimeUnit(String type) {
        switch(type) {
            case "sec":
                return projection.ProjectionTimeUnit.Second;
            case "minute":
                return projection.ProjectionTimeUnit.Minute;
            case "hour":
                return projection.ProjectionTimeUnit.Hour;
            case "day":
                return projection.ProjectionTimeUnit.Day;

            case "week":
                return projection.ProjectionTimeUnit.Week;
            case "month":
                return projection.ProjectionTimeUnit.Month;
        }
        return null;
    }

    public static ProjectionType convertToProjectionType(String type) {
        switch(type) {
            case "cyc":
                return ProjectionType.Cyclic;
            case "monitor":
                return ProjectionType.Monitor;
        }
        return null;
    }

    public static ExecuteMode convertToExecuteMode(String type) {
        switch(type) {
            case "seq":
                return ExecuteMode.Sequential;
            case "parll":
                return ExecuteMode.Parallel;
        }
        return null;
    }

    public static ActionType getActionType(String type) {
        switch(type) {
            case "mesure":
                return ActionType.Measurement;
            case "notificaion":
                return ActionType.Notification;
        }
        return null;
    }

}
