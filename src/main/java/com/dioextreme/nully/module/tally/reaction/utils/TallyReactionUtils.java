package com.dioextreme.nully.module.tally.reaction.utils;

import com.dioextreme.nully.module.tally.dao.TallyDAO;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Objects;

public class TallyReactionUtils
{
    private final TallyDAO dao = new TallyDAO();

    private boolean isTallyGuild(long guildId)
    {
        try
        {
            return dao.isTallyGuild(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isTallyRunChannel(long guildId, long channelId)
    {
        try
        {
            return dao.isTallyRunChannel(guildId, channelId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isPowerUser(@Nonnull Member member, @Nonnull GuildChannel channel)
    {
        return member.getPermissions(channel).contains(Permission.MESSAGE_MANAGE);
    }

    private boolean isReactionEmoji(@Nonnull Emoji emoji)
    {
        return emoji.getName().equals("✅");
    }

    public boolean isValidReaction(@Nonnull GenericMessageReactionEvent event, @Nullable Member reactMember)
    {
        if (reactMember == null)
        {
            reactMember = Objects.requireNonNull(event.getMember());
        }

        User reactUser = reactMember.getUser();

        if (reactUser.isBot())
        {
            return false;
        }

        if (!event.isFromType(ChannelType.TEXT))
        {
            return false;
        }

        Emoji reactionEmoji = event.getEmoji();

        if (!isReactionEmoji(reactionEmoji))
        {
            return false;
        }

        long guildId = event.getGuild().getIdLong();
        GuildChannel channel = event.getGuildChannel();
        long channelId = channel.getIdLong();

        if (!isTallyGuild(guildId))
        {
            return false;
        }

        if (!isTallyRunChannel(guildId, channelId))
        {
            return false;
        }

        if (!isPowerUser(reactMember, channel))
        {
            return false;
        }
        return true;
    }
}
