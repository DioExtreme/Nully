package com.dioextreme.nully.module.tally.setup.menu;

import com.dioextreme.nully.discord.interaction.menu.Menu;
import com.dioextreme.nully.discord.interaction.menu.MenuFactory;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.setup.menu.main.TallyMainMenu;
import com.dioextreme.nully.module.tally.setup.menu.milestone.MilestoneAddMenu;
import com.dioextreme.nully.module.tally.setup.menu.milestone.MilestoneEditMenu;
import com.dioextreme.nully.module.tally.setup.menu.milestone.MilestoneMainMenu;
import com.dioextreme.nully.module.tally.setup.menu.milestone.MilestoneRemoveMenu;
import com.dioextreme.nully.module.tally.setup.menu.mode.ModeAddMenu;
import com.dioextreme.nully.module.tally.setup.menu.mode.ModeEditMenu;
import com.dioextreme.nully.module.tally.setup.menu.mode.ModeMainMenu;
import com.dioextreme.nully.module.tally.setup.menu.mode.ModeRemoveMenu;

import javax.annotation.Nonnull;

import static com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction.*;

public class TallyMenuFactory implements MenuFactory
{
    @Override
    public Menu getMenu(int interactionId, @Nonnull MenuListener menuListener)
    {
        return switch (interactionId)
        {
            case MAIN -> new TallyMainMenu(menuListener);
            case MODE,
                    MODE_ADD_CONFIRM_NO,
                    MODE_EDIT_CONFIRM_NO,
                    MODE_REMOVE_CONFIRM_NO -> new ModeMainMenu(menuListener);
            case MODE_ADD -> new ModeAddMenu(menuListener);
            case MODE_EDIT -> new ModeEditMenu(menuListener);
            case MODE_REMOVE -> new ModeRemoveMenu(menuListener);
            case MILESTONE,
                    MILESTONE_ADD_CONFIRM_NO,
                    MILESTONE_EDIT_CONFIRM_NO,
                    MILESTONE_REMOVE_CONFIRM_NO -> new MilestoneMainMenu(menuListener);
            case MILESTONE_ADD -> new MilestoneAddMenu(menuListener);
            case MILESTONE_EDIT -> new MilestoneEditMenu(menuListener);
            case MILESTONE_REMOVE -> new MilestoneRemoveMenu(menuListener);
            default -> throw new IllegalArgumentException("Got unknown menu id -> " + interactionId);
        };
    }
}
