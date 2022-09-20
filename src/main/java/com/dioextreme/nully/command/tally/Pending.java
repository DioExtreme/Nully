package com.dioextreme.nully.command.tally;

import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.GuildSlashCommand;
import com.dioextreme.nully.command.annotation.RequiredPermission;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.PendingMember;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RequiredPermission(Permission.MESSAGE_MANAGE)
public class Pending implements GuildSlashCommand
{
    private boolean isUseAllowed(@Nonnull SlashCommandInteractionEvent event)
    {
        TallyDAO dao = new TallyDAO();

        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        try
        {
            return event.getChannel().getIdLong() == dao.getLogChannel(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void executeCommand(@Nonnull SlashCommandInteractionEvent event)
    {
        if (isUseAllowed(event))
        {
            event.reply("Moderator commands cannot be used in the log channel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();

        TallyDAO dao = new TallyDAO();
        List<PendingMember> pendingMembers;

        try
        {
            pendingMembers = dao.getPendingMembers(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

        StringBuilder reply = new StringBuilder();

        // Start code formatting
        reply.append("```").append("\n");

        if (pendingMembers.isEmpty())
        {
            reply.append("No members with pending points.").append("\n");
        }
        else
        {
            for (PendingMember member : pendingMembers)
            {
                reply.append(member.getName())
                        .append(": ")
                        .append(member.getPendingPoints())
                        .append("\n");
            }
        }
        // End code formatting
        reply.append("```");

        event.reply(reply.toString()).queue();
    }

    @Override
    public String getName()
    {
        return "pending";
    }

    @Override
    public String getDescription()
    {
        return "Shows members with points pending to be saved";
    }

    @Override
    public GuildCommandType getType()
    {
        return GuildCommandType.TALLY;
    }
}
