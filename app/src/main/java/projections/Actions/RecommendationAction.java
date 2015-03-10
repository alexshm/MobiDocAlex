package projections.Actions;


import android.content.Context;

public class RecommendationAction extends Action{



    public RecommendationAction(String recommendationTxt, String concept, Actor actor,Context c) {
        super(Action.ActionType.Recommendation, recommendationTxt, concept, c);
        _actor=actor;
    }


    @Override
    public void doAction() {

    }



}
