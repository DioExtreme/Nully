package com.dioextreme.nully.module.tally.data;

import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.Milestone;
import com.dioextreme.nully.module.tally.entity.MilestoneAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MilestoneDataHandler
{
    private final TallyDAO dao = new TallyDAO();
    public void addMilestone(@Nonnull GenericInteractionCreateEvent event, Milestone milestone) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        dao.addMilestone(guildId, milestone);
    }

    public void editMilestone(@Nonnull GenericInteractionCreateEvent event, Milestone milestone) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        dao.editMilestone(guildId, milestone);
    }

    public void removeMilestone(@Nonnull GenericInteractionCreateEvent event, Milestone milestone) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        int points = milestone.getPointsTrigger();
        dao.removeMilestone(guildId, points);
    }

    public List<Integer> getMilestoneTriggers(@Nonnull GenericInteractionCreateEvent event) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        return dao.getMilestoneTriggers(guildId);
    }

    public List<MilestoneAction> getMilestoneActions(@Nonnull GenericInteractionCreateEvent event, Milestone milestone) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        int points = milestone.getPointsTrigger();

        return dao.getMilestoneActions(guildId, points);
    }
}
