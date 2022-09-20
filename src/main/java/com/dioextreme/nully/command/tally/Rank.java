package com.dioextreme.nully.command.tally;

import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.GuildSlashCommand;
import com.dioextreme.nully.command.annotation.CommandOption;
import com.dioextreme.nully.discord.builder.EmbedBuilder;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.utils.TextUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import javax.annotation.Nonnull;
import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

@CommandOption(type = OptionType.USER, name = "member", description = "The member to lookup", required = false)
public class Rank implements GuildSlashCommand
{
    @Override
    public void executeCommand(@Nonnull SlashCommandInteractionEvent event)
    {
        Member member = event.getOption("member", event.getMember(), OptionMapping::getAsMember);

        boolean referToSelf = false;

        if (member == event.getMember())
        {
            referToSelf = true;
        }

        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        long memberId = member.getIdLong();

        TallyDAO dao = new TallyDAO();

        int leaderboardPoints;

        try
        {
            leaderboardPoints = dao.getMemberLeaderboardPoints(guildId, memberId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder(member);

        if (leaderboardPoints == 0)
        {
            eb.setColor(Color.RED);
            if (referToSelf)
                eb.setDescription("You are not on the leaderboard.");
            else
                eb.setDescription("This member is not on the leaderboard.");

            event.replyEmbeds(eb.build()).queue();
            return;
        }

        int pendingPoints;
        int nextMilestoneTrigger;
        int rank;

        try
        {
            pendingPoints = dao.getMemberPendingPoints(guildId, memberId);
            nextMilestoneTrigger = dao.getMemberNextMilestoneTrigger(guildId, memberId);
            rank = dao.getMemberLeaderboardRank(guildId, memberId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

        int pointsToNextMilestone = nextMilestoneTrigger - leaderboardPoints;

        eb.setDescription("Ranked **#" + rank + "** on the leaderboard.");

        StringBuilder rankedInfoSb = new StringBuilder();

        rankedInfoSb.append("Points: ")
                .append(TextUtils.getBold(leaderboardPoints))
                .append("\n");

        if (pendingPoints != 0)
        {
            rankedInfoSb.append("Points pending approval: ")
                    .append(TextUtils.getBold(pendingPoints))
                    .append("\n");

            eb.setFooter("Points pending approval are not reflected on the leaderboard.");
        }
        rankedInfoSb.append("\n");

        // If we got 0 or negative, this means all milestones have been reached
        if (pointsToNextMilestone <= 0)
        {
            rankedInfoSb.append("All ranked milestones reached.");
        }
        else
        {
            rankedInfoSb.append("Next ranked milestone in ")
                    .append(TextUtils.getBold(pointsToNextMilestone))
                    .append(" ")
                    .append(TextUtils.getPluralized("point", pointsToNextMilestone))
                    .append(".");
        }

        eb.setColor(new Color(52, 152, 219));
        eb.addField("Info", rankedInfoSb.toString(), false);

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public String getName()
    {
        return "rank";
    }

    @Override
    public String getDescription()
    {
        return "Shows ranked info for a member";
    }

    @Override
    public GuildCommandType getType()
    {
        return GuildCommandType.TALLY;
    }
}
