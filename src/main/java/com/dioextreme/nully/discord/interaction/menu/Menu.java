package com.dioextreme.nully.discord.interaction.menu;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Menu
{
    Set<Integer> getInteractions();

    String uniqueIdOf(int id);

    boolean isInteraction(@Nonnull GenericComponentInteractionCreateEvent event, int id);

    void fromSuccess(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String successText);

    void fromSuccess(@Nonnull ModalInteractionEvent event, @Nonnull String successText);

    void fromFailure(@Nonnull GenericComponentInteractionCreateEvent event, @Nonnull String successText);

    void fromFailure(@Nonnull ModalInteractionEvent event, @Nonnull String errorText);

    void fromButtonInteraction(@Nonnull ButtonInteractionEvent event);

    void fromSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event);

    void fromModalInteraction(@Nonnull ModalInteractionEvent event);

    void show(@Nonnull SlashCommandInteractionEvent event);
}
