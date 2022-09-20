package com.dioextreme.nully.module.tally.entity;

public class RankedMember extends Member
{
    private int pendingPoints;
    private int rankedPoints;

    public RankedMember(int rankedPoints)
    {
        this.rankedPoints = rankedPoints;
    }

    public RankedMember(int rankedPoints, int pendingPoints)
    {
        this.rankedPoints = rankedPoints;
        setPendingPoints(pendingPoints);
    }

    public RankedMember(long memberId, int pendingPoints, int rankedPoints)
    {
        setMemberId(memberId);
        setPendingPoints(pendingPoints);
        this.rankedPoints = rankedPoints;
    }

    public RankedMember(String name, int rankedPoints)
    {
        setName(name);
        this.rankedPoints = rankedPoints;
    }

    public int getRankedPoints()
    {
        return rankedPoints;
    }

    public void setRankedPoints(int rankedPoints)
    {
        this.rankedPoints = rankedPoints;
    }

    public int getPendingPoints()
    {
        return pendingPoints;
    }

    public void setPendingPoints(int pendingPoints)
    {
        this.pendingPoints = pendingPoints;
    }
}
