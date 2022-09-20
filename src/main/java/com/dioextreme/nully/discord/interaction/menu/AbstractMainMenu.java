package com.dioextreme.nully.discord.interaction.menu;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.awt.*;

public abstract class AbstractMainMenu extends AbstractMenu
{

    public AbstractMainMenu(MenuListener menuListener)
    {
        super(menuListener);
    }

    protected abstract MessageEditData createEditData();

    @Override
    public void fromSuccess(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String successText)
    {
        interactionBuilder.create(event)
                .withEmbedColor(Color.RED)
                .withEmbedField("Success", successText, false);
        event.editMessage(createEditData()).queue();
    }

    @Override
    public void fromFailure(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String errorText)
    {
        interactionBuilder.create(event)
                .withEmbedColor(Color.RED)
                .withEmbedField("Error", errorText, false);
        event.editMessage(createEditData()).queue();
    }

    @Override
    public void fromFailure(@Nonnull ModalInteractionEvent event, @Nonnull String errorText)
    {
        interactionBuilder.create(event)
                .withEmbedColor(Color.RED)
                .withEmbedField("Error", errorText, false);
        event.editMessage(createEditData()).queue();
    }
}
