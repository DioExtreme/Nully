package com.dioextreme.nully.discord.entity;

public class MessageOrigin
{
    private final long guildId;
    private final long channelId;
    private final long messageId;

    public MessageOrigin(long guildId, long channelId, long messageId)
    {
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
    }

    public long getGuildId()
    {
        return guildId;
    }

    public long getChannelId()
    {
        return channelId;
    }

    public long getMessageId()
    {
        return messageId;
    }
}
