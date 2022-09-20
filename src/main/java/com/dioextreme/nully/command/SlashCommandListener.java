package com.dioextreme.nully.command;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class SlashCommandListener extends ListenerAdapter
{
    private SlashCommandRegistrar commandRegistrar;

    @Override
    public void onReady(@Nonnull ReadyEvent event)
    {
        commandRegistrar = new SlashCommandRegistrar();
        commandRegistrar.registerGlobalCommands(event.getJDA());
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event)
    {
        if (event.getUser().isBot())
        {
            return;
        }

        if (!event.isFromGuild())
        {
            event.reply("Commands must be used from a server.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String commandName = event.getName();
        SlashCommand command = commandRegistrar.getCommand(commandName);

        if (command == null)
        {
            throw new IllegalArgumentException("Attempted to execute unknown command: " + commandName);
        }

        command.executeCommand(event);
    }
}
