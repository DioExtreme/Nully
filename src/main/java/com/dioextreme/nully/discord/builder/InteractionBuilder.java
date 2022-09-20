package com.dioextreme.nully.discord.builder;

import com.dioextreme.nully.utils.TextUtils;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InteractionBuilder
{
    private MessageCreateBuilder createBuilder;
    private MessageEditBuilder editBuilder;
    private EmbedBuilder embedBuilder;

    List<LayoutComponent> components;

    public InteractionBuilder create(@Nonnull GenericInteractionCreateEvent event)
    {
        createBuilder = new MessageCreateBuilder();
        editBuilder = new MessageEditBuilder();
        embedBuilder = new EmbedBuilder(event);
        components = new ArrayList<>();
        return this;
    }

    public InteractionBuilder withEmbedSource(@Nonnull String url)
    {
        embedBuilder.setDescription(TextUtils.getHyperlink("Message", url));
        return this;
    }

    public InteractionBuilder withEmbedColor(@Nonnull Color color)
    {
        embedBuilder.setColor(color);
        return this;
    }

    public InteractionBuilder withEmbedField(String name, String value, boolean inline)
    {
        embedBuilder.addField(name, value, inline);
        return this;
    }

    public InteractionBuilder withEmbedFooter(String text)
    {
        embedBuilder.setFooter(text);
        return this;
    }

    public InteractionBuilder withButtons(@Nonnull ItemComponent... buttons)
    {
        components.add(ActionRow.of(buttons));
        return this;
    }

    public InteractionBuilder withSelectMenu(@Nonnull ItemComponent selectMenu)
    {
        components.add(ActionRow.of(selectMenu));
        return this;
    }

    public InteractionBuilder withEmptyComponents()
    {
        editBuilder.setComponents();
        return this;
    }

    public MessageCreateData buildAsCreateData()
    {
        if (!embedBuilder.isEmpty())
        {
            createBuilder.setEmbeds(embedBuilder.build());
        }
        if (!components.isEmpty())
        {
            createBuilder.setComponents(components);
        }
        return createBuilder.build();
    }

    public MessageEditData buildAsEditData()
    {
        if (!embedBuilder.isEmpty())
        {
            editBuilder.setEmbeds(embedBuilder.build());
        }
        if (!components.isEmpty())
        {
            editBuilder.setComponents(components);
        }
        return editBuilder.build();
    }
}
