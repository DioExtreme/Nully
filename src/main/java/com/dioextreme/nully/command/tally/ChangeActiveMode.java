package com.dioextreme.nully.command.tally;

import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.GuildSlashCommand;
import com.dioextreme.nully.command.annotation.CommandOption;
import com.dioextreme.nully.command.annotation.RequiredPermission;
import com.dioextreme.nully.discord.builder.InteractionBuilder;
import com.dioextreme.nully.module.tally.data.GuildDataHandler;
import com.dioextreme.nully.module.tally.data.ModeDataHandler;
import com.dioextreme.nully.module.tally.entity.Mode;
import com.dioextreme.nully.module.tally.setup.parser.mode.ModeParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

@RequiredPermission(Permission.MESSAGE_MANAGE)
@CommandOption(type = OptionType.STRING, name = "name", description =  "The mode to use", required = true)
public class ChangeActiveMode implements GuildSlashCommand
{
    private boolean isUseAllowed(@Nonnull SlashCommandInteractionEvent event, long logChannelId)
    {
        return event.getChannel().getIdLong() == logChannelId;
    }

    @Override
    public void executeCommand(@Nonnull SlashCommandInteractionEvent event)
    {
        GuildDataHandler guildDataHandler = new GuildDataHandler();
        long logChannelId = guildDataHandler.getLogChannelId(event);

        if (isUseAllowed(event, logChannelId))
        {
            event.reply("Moderator commands cannot be used in the log channel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String modeName = event.getOption("name").getAsString();

        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        InteractionBuilder creator = new InteractionBuilder();
        ModeDataHandler dataHandler = new ModeDataHandler();

        try
        {
            MessageCreateData messageData;
            if (!dataHandler.isMode(guildId, modeName))
            {
                 messageData = creator.create(event)
                        .withEmbedColor(Color.RED)
                        .withEmbedField("Error", "This mode does not exist.", false)
                        .buildAsCreateData();
            }
            else
            {
                dataHandler.changeActiveMode(guildId, modeName);
                Map<Integer, Integer> options = dataHandler.getModeOptions(guildId, modeName);

                Mode mode = new Mode(modeName, options);
                ModeParser modeParser = new ModeParser();

                String fieldText = "Changed to %s".formatted(modeParser.toDiscord(mode));

                messageData = creator.create(event)
                        .withEmbedColor(Color.GREEN)
                        .withEmbedField("Mode changed", fieldText, false)
                        .buildAsCreateData();

                JDA jda = event.getJDA();
                TextChannel logChannel = Objects.requireNonNull(jda.getTextChannelById(logChannelId));
                logChannel.sendMessage(messageData).queue();
            }
            event.reply(messageData).queue();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getName()
    {
        return "mode";
    }

    @Override
    public String getDescription()
    {
        return "Change the active run mode for the server";
    }

    @Override
    public GuildCommandType getType()
    {
        return GuildCommandType.TALLY;
    }
}
