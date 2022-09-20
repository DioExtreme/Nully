package com.dioextreme.nully.module.tally.entity;

public class Runner extends Member
{
    private int pointsAwarded;

    public Runner(long memberId, String name)
    {
       super(memberId, name);
    }

    public Runner(long memberId, int pointsAwarded)
    {
        setMemberId(memberId);
        this.pointsAwarded = pointsAwarded;
    }

    public Runner(String name, int pointsAwarded)
    {
        setName(name);
        this.pointsAwarded = pointsAwarded;
    }

    public int getPointsAwarded()
    {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded)
    {
        this.pointsAwarded = pointsAwarded;
    }
}
