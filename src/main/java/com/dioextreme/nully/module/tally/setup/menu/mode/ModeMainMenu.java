package com.dioextreme.nully.module.tally.setup.menu.mode;

import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;

public class ModeMainMenu extends ModeMenu
{
    public ModeMainMenu(MenuListener menuListener)
    {
        super(menuListener);

        addInteraction(TallyMenuInteraction.MODE_ADD);
        addInteraction(TallyMenuInteraction.MODE_EDIT);
        addInteraction(TallyMenuInteraction.MODE_REMOVE);
        addInteraction(TallyMenuInteraction.MAIN);
    }

    private MessageEditData createEditData()
    {
        return interactionBuilder.withButtons(
                Button.primary(uniqueIdOf(TallyMenuInteraction.MODE_ADD), "Add mode"),
                Button.primary(uniqueIdOf(TallyMenuInteraction.MODE_EDIT), "Edit mode"),
                Button.primary(uniqueIdOf(TallyMenuInteraction.MODE_REMOVE), "Delete mode"),
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
