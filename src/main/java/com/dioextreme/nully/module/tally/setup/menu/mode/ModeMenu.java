package com.dioextreme.nully.module.tally.setup.menu.mode;

import com.dioextreme.nully.discord.interaction.menu.AbstractMenu;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.entity.Mode;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import com.dioextreme.nully.module.tally.setup.parser.mode.ModeParser;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public abstract class ModeMenu extends AbstractMenu
{
    protected Mode mode;

    public ModeMenu(MenuListener menuListener)
    {
        super(menuListener);
    }

    protected void showConfirmation(@Nonnull ModalInteractionEvent event, int id, String question)
    {
        int buttonYesId;
        int buttonNoId;

        if (id == TallyMenuInteraction.MODE_ADD_CONFIRM)
        {
            buttonYesId = TallyMenuInteraction.MODE_ADD_CONFIRM_YES;
            buttonNoId = TallyMenuInteraction.MODE_ADD_CONFIRM_NO;
        }
        else if (id == TallyMenuInteraction.MODE_EDIT_CONFIRM)
        {
            buttonYesId = TallyMenuInteraction.MODE_EDIT_CONFIRM_YES;
            buttonNoId = TallyMenuInteraction.MODE_EDIT_CONFIRM_NO;
        }
        else
        {
            throw new IllegalArgumentException("Unknown mode confirmation type " + id);
        }

        ModeParser modeParser = new ModeParser();

        String fieldText = "%s\n%s".formatted(modeParser.toDiscord(mode), question);

        MessageEditData messageData = interactionBuilder.create(event)
                .withEmbedField("Tally Setup", fieldText, false)
                .withButtons(
                        Button.success(uniqueIdOf(buttonYesId), "Yes"),
                        Button.danger(uniqueIdOf(buttonNoId), "No")
                ).buildAsEditData();

        event.editMessage(messageData).queue();
    }

    protected void showModeSelectMenu(@Nonnull GenericComponentInteractionCreateEvent event, int id, List<String> modes)
    {
        SelectMenu.Builder selectMenuBuilder = SelectMenu.create(uniqueIdOf(id))
                .setPlaceholder("Select a run mode")
                .setRequiredRange(1,1);

        for (String modeName : modes)
        {
            selectMenuBuilder.addOption(modeName, modeName);
        }

        SelectMenu triggerMenu = selectMenuBuilder.build();

        MessageEditData menuData = interactionBuilder
                .create(event)
                .withEmbedField("Tally Setup", "Modes", false)
                .withSelectMenu(triggerMenu)
                .withButtons(Button.danger(uniqueIdOf(TallyMenuInteraction.MODE), "Back"))
                .buildAsEditData();

        event.editMessage(menuData).queue();
    }

    protected void showNoModes(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        MessageEditData messageData = interactionBuilder.create(event)
                .withEmbedColor(Color.YELLOW)
                .withEmbedField("Tally Setup", "The server has no run modes.", false)
                .buildAsEditData();
        event.editMessage(messageData).queue();
    }
}
