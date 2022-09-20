package com.dioextreme.nully.module.tally.setup.menu.mode;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.data.ModeDataHandler;
import com.dioextreme.nully.module.tally.entity.Mode;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import com.dioextreme.nully.module.tally.setup.parser.mode.ModeParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModeEditMenu extends ModeMenu
{
    private String modeName;

    public ModeEditMenu(MenuListener menuListener)
    {
        super(menuListener);

        // After mode selection menu
        addInteraction(TallyMenuInteraction.MODE_EDIT_SELECT);
        addInteraction(TallyMenuInteraction.MODE);

        // After modal submit
        addInteraction(TallyMenuInteraction.MODE_EDIT_MODAL);

        // After confirmation
        addInteraction(TallyMenuInteraction.MODE_EDIT_CONFIRM_YES);
        addInteraction(TallyMenuInteraction.MODE_EDIT_CONFIRM_NO);
    }

    private void showSelectMenu(@Nonnull GenericComponentInteractionCreateEvent event, List<String> modes)
    {
        showModeSelectMenu(event, TallyMenuInteraction.MODE_EDIT_SELECT, modes);
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

    private void showEditModal(@Nonnull SelectMenuInteractionEvent event, Map<Integer, Integer> options)
    {
        ModeParser parser = new ModeParser();
        String optionString = parser.fromMap(options);

        TextInput params = TextInput
                .create("tally_mode_edit_options", "Mode actions", TextInputStyle.PARAGRAPH)
                .setValue(optionString)
                .setMinLength(1)
                .setMaxLength(300)
                .build();

        Modal modal = Modal.create("tally_edit_mode_modal", "Edit mode")
                .addActionRows(ActionRow.of(params))
                .build();

        event.replyModal(modal).queue();
    }

    private void showModeOptions(@Nonnull SelectMenuInteractionEvent event)
    {
        ModeDataHandler dataHandler = new ModeDataHandler();
        modeName = event.getSelectedOptions().get(0).getValue();

        try
        {
            Mode mode = new Mode(modeName, null);
            Map<Integer, Integer> modeOptions = dataHandler.getModeOptions(event, mode);
            showEditModal(event, modeOptions);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void editMode(@Nonnull GenericInteractionCreateEvent event)
    {
        try
        {
            ModeDataHandler dataHandler = new ModeDataHandler();
            dataHandler.editMode(event, mode);
            // TODO onsuccess
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            //todo onfail
        }
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        if (isInteraction(event, TallyMenuInteraction.MODE_EDIT))
        {
            NullyExecutor.execute(() -> showModes(event));
        }
        else if (isInteraction(event, TallyMenuInteraction.MODE_EDIT_CONFIRM_YES))
        {
            NullyExecutor.execute(() -> editMode(event));
        }
    }

    @Override
    public void fromSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event)
    {
        NullyExecutor.execute(() -> showModeOptions(event));
    }

    @Override
    public void fromModalInteraction(@Nonnull ModalInteractionEvent event)
    {
        ModalMapping optionParamsMapping = Objects.requireNonNull(event.getValue("tally_mode_edit_options"));
        String optionString = optionParamsMapping.getAsString();

        String[] options = optionString.split("\\s+");

        ModeParser modeParser = new ModeParser();
        Map<Integer, Integer> optionMap = modeParser.toMap(options);
        if (optionMap.isEmpty())
        {
            // TODO No idea, will implement later
        }
        else
        {
            mode = new Mode(modeName, optionMap);
            showConfirmation(event, TallyMenuInteraction.MODE_EDIT_CONFIRM,
                    "Edit run mode with the above configuration?");
        }
    }
}
