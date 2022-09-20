package com.dioextreme.nully.module.tally.setup.menu.milestone;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.data.MilestoneDataHandler;
import com.dioextreme.nully.module.tally.entity.Milestone;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

public class MilestoneRemoveMenu extends MilestoneMenu
{

    public MilestoneRemoveMenu(MenuListener menuListener)
    {
        super(menuListener);

        // After milestone trigger selection menu
        addInteraction(TallyMenuInteraction.MILESTONE_REMOVE_TRIGGER_SELECT);
        addInteraction(TallyMenuInteraction.MILESTONE);

        // After confirmation
        addInteraction(TallyMenuInteraction.MILESTONE_REMOVE_CONFIRM_YES);
        addInteraction(TallyMenuInteraction.MILESTONE_REMOVE_CONFIRM_NO);
    }

    private void showSelectMenu(@Nonnull GenericComponentInteractionCreateEvent event, List<Integer> triggers)
    {
        showMilestoneSelectMenu(event, TallyMenuInteraction.MILESTONE_REMOVE_TRIGGER_SELECT, triggers);
    }

    private void showPointTriggers(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        try
        {
            MilestoneDataHandler dataHandler = new MilestoneDataHandler();
            List<Integer> triggers = dataHandler.getMilestoneTriggers(event);

            if (triggers.isEmpty())
            {
                menuListener.setCurrentMenu(TallyMenuInteraction.MILESTONE);
                showNoMilestones(event);
            }
            else
            {
                showSelectMenu(event, triggers);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showFailure(event, "Could not get milestones.");
        }
    }

    private void showRemoveConfirmation(@Nonnull SelectMenuInteractionEvent event)
    {
        MessageEditData interactionData = interactionBuilder.create(event)
                .withEmbedField("Tally Setup", "Are you sure?", false)
                .withButtons(
                        Button.success(uniqueIdOf(TallyMenuInteraction.MILESTONE_REMOVE_CONFIRM_YES), "Yes"),
                        Button.danger(uniqueIdOf(TallyMenuInteraction.MILESTONE_REMOVE_CONFIRM_NO), "No")
                ).buildAsEditData();
        event.editMessage(interactionData).queue();
    }

    private void removeMilestone(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        try
        {
            MilestoneDataHandler dataHandler = new MilestoneDataHandler();
            dataHandler.removeMilestone(event, milestone);
            showSuccess(event, "Milestone removed.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showFailure(event, "Could not remove milestone.");
        }
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        if (isInteraction(event, TallyMenuInteraction.MILESTONE_REMOVE))
        {
            NullyExecutor.execute(() -> showPointTriggers(event));
        }
        else if (isInteraction(event, TallyMenuInteraction.MILESTONE_REMOVE_CONFIRM_YES))
        {
            NullyExecutor.execute(() -> removeMilestone(event));
        }
    }

    @Override
    public void fromSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event)
    {
        String pointsTriggerString = event.getSelectedOptions().get(0).getValue();
        int pointsTrigger = Integer.parseInt(pointsTriggerString);
        milestone = new Milestone(pointsTrigger, null);
        showRemoveConfirmation(event);
    }
}
