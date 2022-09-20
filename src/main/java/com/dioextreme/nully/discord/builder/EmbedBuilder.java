package com.dioextreme.nully.discord.builder;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.awt.*;
import java.util.Objects;

public class EmbedBuilder extends net.dv8tion.jda.api.EmbedBuilder
{
    public EmbedBuilder() {}

    public EmbedBuilder(Member member)
    {
        setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
    }

    public EmbedBuilder(GenericInteractionCreateEvent event)
    {
        setColor(new Color(144, 238, 144));
        if (event.isFromGuild())
        {
            Member guildMember = Objects.requireNonNull(event.getMember());
            setAuthor(guildMember.getEffectiveName(), null, guildMember.getEffectiveAvatarUrl());
        }
        else
        {
            User user = Objects.requireNonNull(event.getUser());
            setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
        }
    }
}
