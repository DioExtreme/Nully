package com.dioextreme.nully.module.tally.setup.menu.milestone;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.data.MilestoneDataHandler;
import com.dioextreme.nully.module.tally.entity.Milestone;
import com.dioextreme.nully.module.tally.entity.MilestoneAction;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import com.dioextreme.nully.module.tally.setup.parser.milestone.MilestoneParser;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MilestoneEditMenu extends MilestoneMenu
{
    public MilestoneEditMenu(MenuListener menuListener)
    {
        super(menuListener);

        // After milestone trigger selection menu
        addInteraction(TallyMenuInteraction.MILESTONE_EDIT_TRIGGER_SELECT);
        addInteraction(TallyMenuInteraction.MILESTONE);

        // After modal submit
        addInteraction(TallyMenuInteraction.MILESTONE_EDIT_MODAL);

        // After confirmation
        addInteraction(TallyMenuInteraction.MILESTONE_EDIT_CONFIRM_YES);
        addInteraction(TallyMenuInteraction.MILESTONE_EDIT_CONFIRM_NO);
    }

    private void showSelectMenu(@Nonnull GenericComponentInteractionCreateEvent event, List<Integer> triggers)
    {
        showMilestoneSelectMenu(event, TallyMenuInteraction.MILESTONE_EDIT_TRIGGER_SELECT, triggers);
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

    private void showEditModal(@Nonnull SelectMenuInteractionEvent event, List<MilestoneAction> actions)
    {
        MilestoneParser parser = new MilestoneParser();
        String actionString = parser.fromList(actions);

        TextInput params = TextInput
                .create("tally_milestone_edit_params", "Milestone actions", TextInputStyle.PARAGRAPH)
                .setValue(actionString)
                .setMinLength(1)
                .setMaxLength(300)
                .build();

        Modal modal = Modal.create(uniqueIdOf(TallyMenuInteraction.MILESTONE_EDIT_MODAL), "Edit milestone")
                .addActionRows(ActionRow.of(params))
                .build();

        event.replyModal(modal).queue();
    }

    private void showMilestoneActions(@Nonnull SelectMenuInteractionEvent event)
    {
        MilestoneDataHandler dataHandler = new MilestoneDataHandler();

        String pointsTriggerString = event.getSelectedOptions().get(0).getValue();
        int pointsTrigger = Integer.parseInt(pointsTriggerString);

        milestone = new Milestone(pointsTrigger);

        try
        {
            List<MilestoneAction> actions = dataHandler.getMilestoneActions(event, milestone);
            showEditModal(event, actions);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showFailure(event, "Could not get milestone actions.");
        }
    }

    private void editMilestone(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        try
        {
            MilestoneDataHandler dataHandler = new MilestoneDataHandler();
            dataHandler.editMilestone(event, milestone);
            showSuccess(event, "Milestone edited.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showFailure(event, "Could not edit milestone.");
        }
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        if (isInteraction(event, TallyMenuInteraction.MILESTONE_EDIT))
        {
            NullyExecutor.execute(() -> showPointTriggers(event));
        }
        else if (isInteraction(event, TallyMenuInteraction.MILESTONE_EDIT_CONFIRM_YES))
        {
            NullyExecutor.execute(() -> editMilestone(event));
        }
    }

    @Override
    public void fromSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event)
    {
        NullyExecutor.execute(() -> showMilestoneActions(event));
    }

    @Override
    public void fromModalInteraction(@Nonnull ModalInteractionEvent event)
    {
        ModalMapping actionParamsMapping = Objects.requireNonNull(event.getValue("tally_milestone_edit_params"));
        String actionString = actionParamsMapping.getAsString();

        String[] actions = actionString.split("\\s+");

        MilestoneParser milestoneParser = new MilestoneParser();
        List<MilestoneAction> actionsMap = milestoneParser.toList(actions);
        if (actionsMap.isEmpty())
        {
            showFailure(event, "No valid actions provided.");
        }
        else
        {
            milestone.setActions(actionsMap);
            showConfirmation(event, TallyMenuInteraction.MILESTONE_EDIT_CONFIRM,
                    "Edit milestone with the above configuration?");
        }
    }
}
