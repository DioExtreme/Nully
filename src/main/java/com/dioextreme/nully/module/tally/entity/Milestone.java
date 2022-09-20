package com.dioextreme.nully.module.tally.entity;

import java.util.ArrayList;
import java.util.List;

public class Milestone
{
    private final int pointsTrigger;
    private List<MilestoneAction> actions;

    public Milestone(int pointsTrigger)
    {
        this.pointsTrigger = pointsTrigger;
        this.actions = new ArrayList<>();
    }

    public Milestone(int pointsTrigger, List<MilestoneAction> actions)
    {
        this.pointsTrigger = pointsTrigger;
        this.actions = actions;
    }

    public int getPointsTrigger()
    {
        return pointsTrigger;
    }

    public List<MilestoneAction> getActions()
    {
        return actions;
    }

    public void setActions(List<MilestoneAction> actions)
    {
        this.actions = actions;
    }
}
