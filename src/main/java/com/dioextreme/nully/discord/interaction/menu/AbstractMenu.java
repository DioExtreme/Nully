package com.dioextreme.nully.discord.interaction.menu;

import com.dioextreme.nully.discord.builder.InteractionBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractMenu implements Menu
{
    protected final InteractionBuilder interactionBuilder = new InteractionBuilder();
    protected MenuListener menuListener;
    private final Set<Integer> interactions = new HashSet<>();

    public AbstractMenu(MenuListener menuListener)
    {
        this.menuListener = menuListener;
    }

    protected void addInteraction(int id)
    {
        interactions.add(id);
    }

    @Override
    public Set<Integer> getInteractions()
    {
        return interactions;
    }

    @Override
    public boolean isInteraction(@Nonnull GenericComponentInteractionCreateEvent event, int id)
    {
        return event.getComponentId().equals(uniqueIdOf(id));
    }

    @Override
    public String uniqueIdOf(int id)
    {
        return "%s:%d".formatted(menuListener, id);
    }

    @Override
    public void fromSuccess(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String successText)
    {
        throw new UnsupportedOperationException("This menu does not support returning from successful tasks");
    }

    @Override
    public void fromSuccess(@Nonnull ModalInteractionEvent event, @Nonnull String successText)
    {
        throw new UnsupportedOperationException("This menu does not support returning from successful tasks");
    }

    @Override
    public void fromFailure(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String errorText)
    {
        throw new UnsupportedOperationException("This menu does not support returning from failed tasks");
    }

    @Override
    public void fromFailure(@Nonnull ModalInteractionEvent event, @Nonnull String errorText)
    {
        throw new UnsupportedOperationException("This menu does not support returning from failed tasks");
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        throw new UnsupportedOperationException("This menu cannot process button interactions");
    }

    @Override
    public void fromSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event)
    {
        throw new UnsupportedOperationException("This menu cannot process select menu interactions");
    }

    @Override
    public void fromModalInteraction(@Nonnull ModalInteractionEvent event)
    {
        throw new UnsupportedOperationException("This menu cannot process modal interactions");
    }

    @Override
    public void show(@Nonnull SlashCommandInteractionEvent event)
    {
        throw new UnsupportedOperationException("This menu cannot be used from a command");
    }
}
