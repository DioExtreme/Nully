package com.dioextreme.nully.module.tally.setup.menu.mode;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.data.ModeDataHandler;
import com.dioextreme.nully.module.tally.entity.Mode;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

public class ModeRemoveMenu extends ModeMenu
{
    public ModeRemoveMenu(MenuListener menuListener)
    {
        super(menuListener);

        // After milestone trigger selection menu
        addInteraction(TallyMenuInteraction.MODE_REMOVE_SELECT);
        addInteraction(TallyMenuInteraction.MODE);

        // After confirmation
        addInteraction(TallyMenuInteraction.MODE_REMOVE_CONFIRM_YES);
        addInteraction(TallyMenuInteraction.MODE_REMOVE_CONFIRM_NO);
    }

    private void showSelectMenu(@Nonnull GenericComponentInteractionCreateEvent event, List<String> modes)
    {
        showModeSelectMenu(event, TallyMenuInteraction.MODE_REMOVE_SELECT, modes);
    }

    private void showModes(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        try
        {
            ModeDataHandler dataHandler = new ModeDataHandler();
            List<String> modes = dataHandler.getModes(event);

            if (modes.isEmpty())
            {
                menuListener.setCurrentMenu(TallyMenuInteraction.MODE);
                showNoModes(event);
            }
            else
            {
                showSelectMenu(event, modes);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void showRemoveConfirmation(@Nonnull SelectMenuInteractionEvent event)
    {
        MessageEditData interactionData = interactionBuilder.create(event)
                .withEmbedField("Tally Setup", "Are you sure?", false)
                .withButtons(
                        Button.success(uniqueIdOf(TallyMenuInteraction.MODE_REMOVE_CONFIRM_YES), "Yes"),
                        Button.danger(uniqueIdOf(TallyMenuInteraction.MODE_REMOVE_CONFIRM_NO), "No")
                ).buildAsEditData();
        event.editMessage(interactionData).queue();
    }

    private void removeMode(@Nonnull GenericInteractionCreateEvent event)
    {
        try
        {
            ModeDataHandler dataHandler = new ModeDataHandler();
            dataHandler.removeMode(event, mode);
            //TODO onsuccess
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            //TODO onfail
        }
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        if (isInteraction(event, TallyMenuInteraction.MODE_REMOVE))
        {
            NullyExecutor.execute(() -> showModes(event));
        }
        else if (isInteraction(event, TallyMenuInteraction.MODE_REMOVE_CONFIRM_YES))
        {
            NullyExecutor.execute(() -> removeMode(event));
        }
    }

    @Override
    public void fromSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event)
    {
        String modeName = event.getSelectedOptions().get(0).getValue();
        mode = new Mode(modeName, null);
        showRemoveConfirmation(event);
    }
}
