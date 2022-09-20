package com.dioextreme.nully.discord.interaction.menu;

public interface MenuFactory
{
    Menu getMenu(int interactionId, MenuListener menuListener);
}
