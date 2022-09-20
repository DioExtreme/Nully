package com.dioextreme.nully.module.tally.data;

import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.Milestone;
import com.dioextreme.nully.module.tally.entity.MilestoneAction;
import com.dioextreme.nully.module.tally.entity.RankedMember;
import com.dioextreme.nully.module.tally.entity.SetupConfig;
import com.dioextreme.nully.module.tally.setup.parser.milestone.MilestoneActionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuildDataHandler
{
    private final TallyDAO dao = new TallyDAO();
    public void addTallyGuild(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull SetupConfig setupOptions)
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        TallyDAO dao = new TallyDAO();

        try
        {
            dao.addTallyGuild(guildId, setupOptions);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public long getLogChannelId(@Nonnull GenericInteractionCreateEvent event)
    {
        TallyDAO dao = new TallyDAO();

        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        try
        {
            return dao.getLogChannel(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveTally(@Nonnull Guild guild) throws SQLException
    {
        long guildId = guild.getIdLong();

        // Retrieve current ranked points
        // from members with pending points
        List<RankedMember> membersToSave = dao.getPendingMembersWithRankedPoints(guildId);

        // Prepare milestones
        var milestoneTriggers = dao.getMilestoneTriggers(guildId);
        List<Milestone> milestones = new ArrayList<>();

        for (int trigger : milestoneTriggers)
        {
            List<MilestoneAction> actions = dao.getMilestoneActions(guildId, trigger);
            Milestone milestone = new Milestone(trigger, actions);
            milestones.add(milestone);
        }

        // Assign milestones

        for (RankedMember member : membersToSave)
        {
            long memberId = member.getMemberId();
            int pending = member.getPendingPoints();
            int oldRankedPoints = member.getRankedPoints();

            int newRankedPoints = pending + oldRankedPoints;

            // Did someone lose points?
            // Zero isn't really possible cause of the SQL query
            // More of a sanity check
            if (newRankedPoints <= oldRankedPoints)
            {
                continue;
            }

            for (Milestone milestone : milestones)
            {
                int requirement = milestone.getPointsTrigger();

                if (oldRankedPoints < requirement && newRankedPoints >= requirement)
                {
                    // We'll hardcode a bit for roles, since that's the only thing we support
                    // Will refactor whenever we add more features
                    List<MilestoneAction> actions = milestone.getActions();

                    actions.forEach(action ->
                    {
                        int actionTypeId = action.getTypeId();
                        long actionValue = action.getValue();

                        if (actionTypeId != MilestoneActionType.ROLE)
                        {
                            return;
                        }

                        Role roleToAdd = guild.getRoleById(actionValue);

                        if (roleToAdd == null)
                        {
                            return;
                        }

                        guild.addRoleToMember(UserSnowflake.fromId(memberId), roleToAdd).queue();
                    });
                }
            }
            // Add points
            member.setRankedPoints(newRankedPoints);
        }

        // Add pending points to ranked points, then remove all runs and pending members
        dao.updateGuildFromSave(guildId, membersToSave);
    }
}
