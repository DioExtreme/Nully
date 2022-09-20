package com.dioextreme.nully.module.tally.setup.menu.milestone;

import com.dioextreme.nully.discord.interaction.menu.AbstractMainMenu;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;

public class MilestoneMainMenu extends AbstractMainMenu
{

    public MilestoneMainMenu(MenuListener menuListener)
    {
        super(menuListener);

        addInteraction(TallyMenuInteraction.MILESTONE_ADD);
        addInteraction(TallyMenuInteraction.MILESTONE_EDIT);
        addInteraction(TallyMenuInteraction.MILESTONE_REMOVE);
        addInteraction(TallyMenuInteraction.MAIN);
    }

    @Override
    protected MessageEditData createEditData()
    {
        return interactionBuilder.withButtons(
                Button.primary(uniqueIdOf(TallyMenuInteraction.MILESTONE_ADD), "Add milestone"),
                Button.primary(uniqueIdOf(TallyMenuInteraction.MILESTONE_EDIT), "Edit milestone"),
                Button.primary(uniqueIdOf(TallyMenuInteraction.MILESTONE_REMOVE), "Delete milestone"),
                Button.secondary(uniqueIdOf(TallyMenuInteraction.MAIN), "Back")
        ).buildAsEditData();
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        interactionBuilder.create(event)
                .withEmbedField("Tally Setup", "Pick an option", false);
        event.editMessage(createEditData()).queue();
    }
}
