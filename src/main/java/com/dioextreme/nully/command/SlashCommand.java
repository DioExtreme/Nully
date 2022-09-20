package com.dioextreme.nully.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;

public interface SlashCommand
{
    void executeCommand(@Nonnull SlashCommandInteractionEvent event);
    String getName();
    String getDescription();
}
