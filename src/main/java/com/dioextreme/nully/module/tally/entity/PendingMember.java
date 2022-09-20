package com.dioextreme.nully.module.tally.entity;

public class PendingMember extends Member
{
    private final int pendingPoints;

    public PendingMember(long memberId, String name, int pendingPoints)
    {
        super(memberId, name);
        this.pendingPoints = pendingPoints;
    }

    public int getPendingPoints()
    {
        return pendingPoints;
    }
}
