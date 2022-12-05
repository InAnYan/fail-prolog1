package com.inanyan.prolog.logic.goals;

import com.inanyan.prolog.logic.Environment;
import com.inanyan.prolog.logic.Goal;
import com.inanyan.prolog.logic.LogicBase;

import java.util.List;

public class CompoundGoal extends Goal {
    private final List<Goal> goalList;

    public CompoundGoal(LogicBase base, List<Goal> goalList) {
        super(base);
        assert goalList.size() != 0;
        assert goalList.size() != 1;
        this.goalList = goalList;
    }

    @Override
    public void changeBase(LogicBase base) {
        super.changeBase(base);
        for (Goal goal : goalList) {
            goal.changeBase(base);
        }
    }

    @Override
    public boolean call(Environment env) {
        return mainJob(env, true);
    }

    @Override
    public boolean redo(Environment env) {
        return mainJob(env, false);
    }

    private boolean mainJob(Environment env, boolean mode) {
        int currentGoalIndex = 0;
        while (true) {
            if (currentGoalIndex < 0) {
                return false;
            } else if (currentGoalIndex >= goalList.size()){
                return true;
            }

            Goal currentGoal = goalList.get(currentGoalIndex);
            boolean result = mode ? currentGoal.call(env) : currentGoal.redo(env);

            if (result) {
                currentGoalIndex++;
                mode = true;
            } else {
                currentGoalIndex--;
                mode = false;
            }
        }
    }
}
