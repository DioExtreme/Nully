package com.dioextreme.nully.module.tally.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class Member
{
    @Nullable
    private Long memberId;
    private String name;

    public Member(){}

    public Member(@Nonnull String name)
    {
        this.name = name;
    }

    public Member(long memberId, @Nonnull String name)
    {
        this.memberId = memberId;
        this.name = name;
    }

    @Nullable
    public Long getMemberId()
    {
        return memberId;
    }


    public void setMemberId(long memberId)
    {
        this.memberId = memberId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(@Nonnull String name)
    {
        this.name = name;
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
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(memberId);
    }
}
