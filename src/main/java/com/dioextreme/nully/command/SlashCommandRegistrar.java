package com.dioextreme.nully.command;

import com.dioextreme.nully.command.annotation.CommandOption;
import com.dioextreme.nully.command.annotation.CommandOptions;
import com.dioextreme.nully.command.annotation.RequiredPermission;
import com.dioextreme.nully.command.tally.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlashCommandRegistrar
{
    // Commands that appear in all guilds
    private static final Map<String, SlashCommand> globalCommands = new HashMap<>();

    // Commands that appear under specific setup conditions
    private static final Map<String, GuildSlashCommand> guildCommands = new HashMap<>();

    private static boolean commandMapsFilled = false;

    public SlashCommandRegistrar()
    {
        if (commandMapsFilled)
        {
            return;
        }
        commandMapsFilled = true;
        addGlobalCommands();
        addGuildCommands();
    }

    private void addGlobalCommands()
    {
        globalCommands.put("setuptally", new TallySetup());
    }

    private void addGuildCommands()
    {
        guildCommands.put("leaderboard", new Leaderboard());
        guildCommands.put("pending", new Pending());
        guildCommands.put("rank", new Rank());
        guildCommands.put("save", new Save());
        guildCommands.put("mode", new ChangeActiveMode());
        guildCommands.put("changepoints", new ChangePendingPoints());
    }

    @Nullable
    public SlashCommand getCommand(@Nonnull String commandName)
    {
        if (guildCommands.containsKey(commandName))
        {
            return guildCommands.get(commandName);
        }
        return globalCommands.getOrDefault(commandName, null);
    }

    private CommandData getCommandData(@Nonnull SlashCommand command)
    {
        SlashCommandData commandData = Commands.slash(command.getName(), command.getDescription());
        // Do we have options for this command?

        // CommandOption != null  -> One option
        // CommandOptions != null -> More than one option
        CommandOption commandOption = command.getClass().getAnnotation(CommandOption.class);
        if (commandOption != null)
        {
            commandData.addOption(commandOption.type(), commandOption.name(),
                    commandOption.description(), commandOption.required());
        }
        else
        {
            CommandOptions commandOptionsAnnotation = command.getClass().getAnnotation(CommandOptions.class);
            if (commandOptionsAnnotation != null)
            {
                CommandOption[] commandOptions = commandOptionsAnnotation.value();

                for (CommandOption option : commandOptions)
                {
                    commandData.addOption(option.type(), option.name(), option.description(), option.required());
                }
            }
        }
        // Do we have a default required permission for this command?
        RequiredPermission requiredPermissionAnnotation = command.getClass().getAnnotation(RequiredPermission.class);

        if (requiredPermissionAnnotation != null)
        {
            Permission requiredPermission = requiredPermissionAnnotation.value();
            commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(requiredPermission));
        }

        return commandData;
    }

    public void registerGlobalCommands(@Nonnull JDA jda)
    {
        List<CommandData> globalCommandData = new ArrayList<>();

        for (SlashCommand command : globalCommands.values())
        {
            CommandData commandData = getCommandData(command);
            globalCommandData.add(commandData);
        }
        jda.updateCommands().addCommands(globalCommandData).queue();
    }

    public void registerCommandsOfType(@Nonnull GuildCommandType type, @Nonnull Guild guild)
    {
        List<CommandData> guildCommandData = new ArrayList<>();

        for (GuildSlashCommand command : guildCommands.values())
        {
            if (command.getType() != type)
            {
                continue;
            }

            CommandData commandData = getCommandData(command);
            guildCommandData.add(commandData);
        }
        guild.updateCommands().addCommands(guildCommandData).queue();
    }
}
