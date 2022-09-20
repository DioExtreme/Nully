package com.dioextreme.nully.module.tally.setup.menu;

import com.dioextreme.nully.discord.interaction.menu.Menu;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;

public class TallyMenuListener extends MenuListener
{
    private final TallyMenuFactory menuFactory = new TallyMenuFactory();
    public TallyMenuListener(long starterMemberId)
    {
        super(starterMemberId);
    }

    @Override
    public void setCurrentMenu(int interactionId)
    {
        boolean interactionIsValid = getCurrentMenu().getInteractions().contains(interactionId);

        if (!interactionIsValid)
        {
            throw new IllegalArgumentException("Interaction is not allowed in the current menu: " + interactionId);
        }

        if (interactionId == TallyMenuInteraction.EXIT)
        {
            exitRequested = true;
            return;
        }

        // The same menu object handles the interaction
        if (interactionId >= 1000)
        {
            return;
        }

        Menu newMenu = menuFactory.getMenu(interactionId, this);
        setCurrentMenu(newMenu);
    }
}