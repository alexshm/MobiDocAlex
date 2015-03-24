package projections.ActionsExecutor;

import java.util.List;

/**
 * Created by Moshe on 3/23/2015.
 */
public interface Continuation {
    Object call(Integer first, List rest);
}

