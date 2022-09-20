package com.dioextreme.nully.module.tally.entity;

import java.util.Map;

public class Mode
{
    private final String name;
    private final Map<Integer, Integer> options;

    public Mode(String name, Map<Integer, Integer> options)
    {
        this.name = name;
        this.options = options;
    }

    public String getName()
    {
        return name;
    }

    public int getOption(int optionTypeId)
    {
        return options.getOrDefault(optionTypeId, 1);
    }

    public Map<Integer, Integer> getOptions()
    {
        return options;
    }
}
