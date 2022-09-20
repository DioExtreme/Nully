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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MilestoneAddMenu extends MilestoneMenu
{
    public MilestoneAddMenu(MenuListener menuListener)
    {
        super(menuListener);

        // In case the Modal is cancelled,
        // the previous menu is still shown
        //
        // Add menu is being renewed because
        // we cannot reply to the same interaction twice
        addInteraction(TallyMenuInteraction.MILESTONE_ADD);
        addInteraction(TallyMenuInteraction.MILESTONE_EDIT);
        addInteraction(TallyMenuInteraction.MILESTONE_REMOVE);
        addInteraction(TallyMenuInteraction.MAIN);

        // After modal is submitted
        addInteraction(TallyMenuInteraction.MILESTONE_ADD_MODAL);

        // After confirmation
        addInteraction(TallyMenuInteraction.MILESTONE_ADD_CONFIRM_YES);
        addInteraction(TallyMenuInteraction.MILESTONE_ADD_CONFIRM_NO);

        // After success / failure
        addInteraction(TallyMenuInteraction.MILESTONE);
    }

    private void sendModal(@Nonnull ButtonInteractionEvent event)
    {
        TextInput points = TextInput
                .create("tally_milestone_add_points", "Points requirement", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(10)
                .build();

        TextInput params = TextInput
                .create("tally_milestone_add_params", "Milestone actions", TextInputStyle.PARAGRAPH)
                .setValue("One action per line:\nrole 684089717524135995\nrole 684089717524135996")
                .setMinLength(1)
                .setMaxLength(300)
                .build();

        Modal modal = Modal.create(uniqueIdOf(TallyMenuInteraction.MILESTONE_ADD_MODAL), "Add milestone")
                .addActionRows(ActionRow.of(points), ActionRow.of(params))
                .build();

        event.replyModal(modal).queue();
    }

    private void addMilestone(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        try
        {
            MilestoneDataHandler dataHandler = new MilestoneDataHandler();
            dataHandler.addMilestone(event, milestone);
            showSuccess(event, "Milestone added.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showFailure(event, "Could not add milestone.");
        }
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        if (isInteraction(event, TallyMenuInteraction.MILESTONE_ADD))
        {
            sendModal(event);
        }
        else if (isInteraction(event, TallyMenuInteraction.MILESTONE_ADD_CONFIRM_YES))
        {
            NullyExecutor.execute(() -> addMilestone(event));
        }
    }

    @Override
    public void fromModalInteraction(@Nonnull ModalInteractionEvent event)
    {
        ModalMapping pointsMapping = Objects.requireNonNull(event.getValue("tally_milestone_add_points"));
        int points = Integer.parseInt(pointsMapping.getAsString());

        ModalMapping actionParamsMapping = Objects.requireNonNull(event.getValue("tally_milestone_add_params"));
        String inputString = actionParamsMapping.getAsString();

        String[] inputSplit = inputString.split("\\s+");

        MilestoneParser milestoneParser = new MilestoneParser();
        List<MilestoneAction> actions  = milestoneParser.toList(inputSplit);
        if (actions.isEmpty())
        {
            showFailure(event, "No valid options given.");
        }
        else
        {
            milestone = new Milestone(points, actions);
            showConfirmation(event, TallyMenuInteraction.MILESTONE_ADD_CONFIRM,
                    "Add milestone with the above configuration?");
        }
    }
}
