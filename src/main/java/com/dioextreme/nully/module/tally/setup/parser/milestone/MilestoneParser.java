package com.dioextreme.nully.module.tally.setup.parser.milestone;

import com.dioextreme.nully.module.tally.entity.Milestone;
import com.dioextreme.nully.module.tally.entity.MilestoneAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MilestoneParser
{
    public List<MilestoneAction> toList(String[] input)
    {
        List<MilestoneAction> actions = new ArrayList<>();

        int arrayIndex = 0;
        while (arrayIndex < input.length)
        {
            String actionString = input[arrayIndex];
            if (actionString.equalsIgnoreCase("role"))
            {
                String valueString = input[arrayIndex + 1];
                long roleId = Long.parseUnsignedLong(valueString);
                MilestoneAction action = new MilestoneAction(MilestoneActionType.ROLE, roleId);
                actions.add(action);
                ++arrayIndex;
            }
            else
            {
                return new ArrayList<>();
            }
            ++arrayIndex;
        }
        return actions;
    }

    public String fromList(List<MilestoneAction> actions)
    {
        StringBuilder sb = new StringBuilder();

        for (MilestoneAction action : actions)
        {
            int actionType = action.getTypeId();
            long actionValue = action.getValue();

            if (actionType == MilestoneActionType.ROLE)
            {
                sb.append("role ").append(actionValue).append("\n");
            }
        }
        return sb.toString();
    }

    public String toDiscord(Milestone milestone, Guild guild)
    {
        int points = milestone.getPointsTrigger();
        List<MilestoneAction> actions = milestone.getActions();

        StringBuilder sb = new StringBuilder();
        sb.append("At **").append(points).append("** points, do:\n\n");

        for (MilestoneAction action : actions)
        {
            int actionType = action.getTypeId();
            long actionValue = action.getValue();

            if (actionType == MilestoneActionType.ROLE)
            {
                Role role = Objects.requireNonNull(guild.getRoleById(actionValue));
                String roleMention = role.getAsMention();

                sb.append("**Add role:** ").append(roleMention).append("\n");
            }
        }
        return sb.toString();
    }
}
