package com.dioextreme.nully.command.annotation;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CommandOptions.class)
public @interface CommandOption
{
    OptionType type();
    String name();
    String description();
    boolean required();
}

