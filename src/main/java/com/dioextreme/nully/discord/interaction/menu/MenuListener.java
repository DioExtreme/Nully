package com.dioextreme.nully.discord.interaction.menu;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class MenuListener extends ListenerAdapter
{
    private final long creationTime;
    private final long starterMemberId;

    private Menu currentMenu;
    protected boolean exitRequested = false;

    public MenuListener(long starterMemberId)
    {
        this.starterMemberId = starterMemberId;
        this.creationTime = System.currentTimeMillis();
    }

    public Menu getCurrentMenu()
    {
        return currentMenu;
    }

    public abstract void setCurrentMenu(int interactionId);

    private void setCurrentMenu(String uniqueInteractionId)
    {
        String interactionIdString = uniqueInteractionId.substring(uniqueInteractionId.lastIndexOf(':') + 1);
        int interactionId = Integer.parseInt(interactionIdString);
        setCurrentMenu(interactionId);
    }

    public void setCurrentMenu(Menu newMenu)
    {
        currentMenu = newMenu;
    }

    protected String uniqueIdOf(int interactionId)
    {
        return "%s:%d".formatted(this, interactionId);
    }

    private boolean isStarter(long memberId)
    {
        return starterMemberId == memberId;
    }

    private boolean isInteractionValid(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        Member member = Objects.requireNonNull(event.getMember());
        long memberId = member.getIdLong();
        return isStarter(memberId) && event.getComponentId().startsWith(this.toString());
    }

    private boolean isInteractionValid(@Nonnull ModalInteractionEvent event)
    {
        Member member = Objects.requireNonNull(event.getMember());
        long memberId = member.getIdLong();
        return isStarter(memberId) && event.getModalId().startsWith(this.toString());
    }

    public void start(@Nonnull SlashCommandInteractionEvent event, Menu startMenu)
    {
        setCurrentMenu(startMenu);
        event.getJDA().addEventListener(this);
        getCurrentMenu().show(event);
    }

    public void close(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        event.getJDA().removeEventListener(this);
        event.deferEdit().queue();
        event.getHook().deleteOriginal().queue();

        currentMenu = null;
    }

    private void stopListening(@Nonnull GenericInteractionCreateEvent event)
    {
        event.getJDA().removeEventListener(this);
        currentMenu = null;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        if (!isInteractionValid(event))
        {
            return;
        }

        if (event.getHook().isExpired())
        {
            stopListening(event);
            return;
        }

        String buttonId = event.getComponentId();
        setCurrentMenu(buttonId);

        if (exitRequested)
        {
            close(event);
            return;
        }
        getCurrentMenu().fromButtonInteraction(event);
    }

    @Override
    public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event)
    {
        if (!isInteractionValid(event))
        {
            return;
        }

        if (event.getHook().isExpired())
        {
            stopListening(event);
            return;
        }

        String selectMenuId = event.getComponentId();
        setCurrentMenu(selectMenuId);

        if (exitRequested)
        {
            close(event);
            return;
        }

        getCurrentMenu().fromSelectMenuInteraction(event);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event)
    {
        if (!isInteractionValid(event))
        {
            return;
        }

        if (event.getHook().isExpired())
        {
            stopListening(event);
            return;
        }

        String modalId = event.getModalId();
        setCurrentMenu(modalId);

        getCurrentMenu().fromModalInteraction(event);
    }

    @Override
    public String toString()
    {
        return "%d:%d".formatted(starterMemberId, creationTime);
    }
}
