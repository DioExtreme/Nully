package com.dioextreme.nully.module.tally.setup.menu.milestone;

import com.dioextreme.nully.discord.interaction.menu.AbstractMenu;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.entity.Milestone;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import com.dioextreme.nully.module.tally.setup.parser.milestone.MilestoneParser;
import com.dioextreme.nully.utils.TextUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public abstract class MilestoneMenu extends AbstractMenu
{
    protected Milestone milestone;

    public MilestoneMenu(MenuListener menuListener)
    {
        super(menuListener);
    }

    protected void showConfirmation(@Nonnull ModalInteractionEvent event, int id, @Nonnull String question)
    {
        int buttonYesId;
        int buttonNoId;

        if (id == TallyMenuInteraction.MILESTONE_ADD_CONFIRM)
        {
            buttonYesId = TallyMenuInteraction.MILESTONE_ADD_CONFIRM_YES;
            buttonNoId = TallyMenuInteraction.MILESTONE_ADD_CONFIRM_NO;
        }
        else if (id == TallyMenuInteraction.MILESTONE_EDIT_CONFIRM)
        {
            buttonYesId = TallyMenuInteraction.MILESTONE_EDIT_CONFIRM_YES;
            buttonNoId = TallyMenuInteraction.MILESTONE_EDIT_CONFIRM_NO;
        }
        else
        {
            throw new IllegalArgumentException("Unknown milestone confirmation type " + id);
        }

        Guild guild = Objects.requireNonNull(event.getGuild());

        MilestoneParser parser = new MilestoneParser();
        String milestoneString = parser.toDiscord(milestone, guild);
        milestoneString += "\n" + question;

        MessageEditData messageData = interactionBuilder.create(event)
                .withEmbedField("Tally Setup", milestoneString, false)
                .withButtons(
                        Button.success(uniqueIdOf(buttonYesId), "Yes"),
                        Button.danger(uniqueIdOf(buttonNoId), "No")
                ).buildAsEditData();

        event.editMessage(messageData).queue();
    }

    protected void showMilestoneSelectMenu(@Nonnull GenericComponentInteractionCreateEvent event, int id,
                                                  List<Integer> triggers)
    {
        SelectMenu.Builder selectMenuBuilder = SelectMenu.create(uniqueIdOf(id))
                .setPlaceholder("Select a milestone trigger")
                .setRequiredRange(1,1);

        for (int points : triggers)
        {
            selectMenuBuilder.addOption(points + TextUtils.getPluralized(" Point", points),
                    String.valueOf(points));
        }

        SelectMenu triggerMenu = selectMenuBuilder.build();

        MessageEditData menuData = interactionBuilder
                .create(event)
                .withEmbedField("Tally Setup", "Triggers", false)
                .withSelectMenu(triggerMenu)
                .withButtons(Button.danger(uniqueIdOf(TallyMenuInteraction.MILESTONE), "Back"))
                .buildAsEditData();

        event.editMessage(menuData).queue();
    }

    protected void showNoMilestones(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        MessageEditData messageData = interactionBuilder.create(event)
                .withEmbedColor(Color.YELLOW)
                .withEmbedField("Tally Setup", "The server has no milestones.", false)
                .buildAsEditData();
        event.editMessage(messageData).queue();
    }

    protected void showSuccess(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String successText)
    {
        menuListener.setCurrentMenu(TallyMenuInteraction.MILESTONE);
        menuListener.getCurrentMenu().fromSuccess(event, successText);
    }

    protected void showFailure(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String errorText)
    {
        menuListener.setCurrentMenu(TallyMenuInteraction.MILESTONE);
        menuListener.getCurrentMenu().fromFailure(event, errorText);
    }

    protected void showFailure(@Nonnull ModalInteractionEvent event, @Nonnull String errorText)
    {
        menuListener.setCurrentMenu(TallyMenuInteraction.MILESTONE);
        menuListener.getCurrentMenu().fromFailure(event, errorText);
    }
}
