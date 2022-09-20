package com.dioextreme.nully.module.tally.entity;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

public class SetupConfig
{
    private SelectOption runChannel;
    private SelectOption logChannel;

    public SelectOption getRunChannel()
    {
        return runChannel;
    }

    public void setRunChannel(SelectOption runChannel)
    {
        this.runChannel = runChannel;
    }

    public SelectOption getLogChannel()
    {
        return logChannel;
    }

    public void setLogChannel(SelectOption logChannel)
    {
        this.logChannel = logChannel;
    }
}
