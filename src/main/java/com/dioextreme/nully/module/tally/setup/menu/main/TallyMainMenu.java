package com.dioextreme.nully.module.tally.setup.menu.main;

import com.dioextreme.nully.discord.interaction.menu.AbstractMenu;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;

public class TallyMainMenu extends AbstractMenu
{
    public TallyMainMenu(MenuListener menuListener)
    {
        super(menuListener);

        // Mode
        addInteraction(TallyMenuInteraction.MODE);

        // Milestone main menu
        addInteraction(TallyMenuInteraction.MILESTONE);

        // Destroy MenuListener
        addInteraction(TallyMenuInteraction.EXIT);
    }

    private void createMessage(@Nonnull GenericInteractionCreateEvent event)
    {
        interactionBuilder.create(event)
                .withEmbedField("Tally Setup", "What would you like to change?", false)
                .withButtons(
                    Button.primary(uniqueIdOf(TallyMenuInteraction.MODE), "Run modes"),
                    Button.primary(uniqueIdOf(TallyMenuInteraction.MILESTONE), "Ranked milestones"),
                    Button.danger(uniqueIdOf(TallyMenuInteraction.EXIT), "Exit")
                );
    }

    @Override
    public void show(@Nonnull SlashCommandInteractionEvent event)
    {
        createMessage(event);
        MessageCreateData messageData = interactionBuilder.buildAsCreateData();
        event.reply(messageData).queue();
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        createMessage(event);
        MessageEditData messageData = interactionBuilder.buildAsEditData();
        event.editMessage(messageData).queue();
    }
}
