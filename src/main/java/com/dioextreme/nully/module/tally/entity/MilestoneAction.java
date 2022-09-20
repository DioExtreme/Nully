package com.dioextreme.nully.module.tally.entity;

import java.util.Objects;

public class MilestoneAction
{
    private int typeId;
    private long value;

    public MilestoneAction(int typeId, long value)
    {
        this.typeId = typeId;
        this.value = value;
    }

    public int getTypeId()
    {
        return typeId;
    }

    public void setTypeId(int typeId)
    {
        this.typeId = typeId;
    }

    public long getValue()
    {
        return value;
    }

    public void setValue(long value)
    {
        this.value = value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        MilestoneAction that = (MilestoneAction) o;
        return typeId == that.typeId && value == that.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(typeId, value);
    }
}
