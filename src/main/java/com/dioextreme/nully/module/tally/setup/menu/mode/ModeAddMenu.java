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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class ModeAddMenu extends ModeMenu
{

    public ModeAddMenu(MenuListener menuListener)
    {
        super(menuListener);

        // In case the Modal is cancelled,
        // the previous menu is still shown
        //
        // Add menu is being renewed because
        // we cannot reply to the same interaction twice
        addInteraction(TallyMenuInteraction.MODE_ADD);
        addInteraction(TallyMenuInteraction.MODE_EDIT);
        addInteraction(TallyMenuInteraction.MODE_REMOVE);
        addInteraction(TallyMenuInteraction.MAIN);

        // After modal is submitted
            addInteraction(TallyMenuInteraction.MODE_ADD_MODAL);

        // After confirmation
        addInteraction(TallyMenuInteraction.MODE_ADD_CONFIRM_YES);
        addInteraction(TallyMenuInteraction.MODE_ADD_CONFIRM_NO);
    }

    protected void sendModal(@Nonnull GenericComponentInteractionCreateEvent event)
    {
        TextInput points = TextInput
                .create("tally_mode_add_name", "Name", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(10)
                .build();

        TextInput params = TextInput
                .create("tally_mode_add_options", "Options", TextInputStyle.PARAGRAPH)
                .setValue("Valid options:\nmulti 2\nauthor 2\nnormal 1")
                .setMinLength(1)
                .setMaxLength(300)
                .build();

        Modal modal = Modal.create("tally_add_mode_modal", "Add run mode")
                .addActionRows(ActionRow.of(points), ActionRow.of(params))
                .build();

        event.replyModal(modal).queue();
    }

    private void addMode(@Nonnull GenericInteractionCreateEvent event)
    {
        try
        {
            ModeDataHandler dataHandler = new ModeDataHandler();
            dataHandler.addMode(event, mode);
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
        if (isInteraction(event, TallyMenuInteraction.MODE_ADD))
        {
            sendModal(event);
        }
        else if (isInteraction(event, TallyMenuInteraction.MODE_ADD_CONFIRM_YES))
        {
            NullyExecutor.execute(() -> addMode(event));
        }
    }

    @Override
    public void fromModalInteraction(@Nonnull ModalInteractionEvent event)
    {
        ModalMapping pointsMapping = Objects.requireNonNull(event.getValue("tally_mode_add_name"));
        String modeName = pointsMapping.getAsString();

        ModalMapping optionParamsMapping = Objects.requireNonNull(event.getValue("tally_mode_add_options"));
        String optionString = optionParamsMapping.getAsString();

        String[] options = optionString.split("\\s+");

        ModeParser modeParser = new ModeParser();
        Map<Integer, Integer> optionMap = modeParser.toMap(options);
        if (optionMap.isEmpty())
        {
            // TODO: No idea, will implement later
        }
        else
        {
            mode = new Mode(modeName, optionMap);
            showConfirmation(event, TallyMenuInteraction.MODE_ADD_CONFIRM,
                    "Add run mode with the above configuration?");
        }
    }
}
