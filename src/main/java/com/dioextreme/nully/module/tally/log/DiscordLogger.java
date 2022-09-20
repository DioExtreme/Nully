package com.dioextreme.nully.module.tally.log;

import com.dioextreme.nully.discord.builder.EmbedBuilder;
import com.dioextreme.nully.utils.TextUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;

public class DiscordLogger
{
    private static void sendErrorEmbed(@Nonnull GenericMessageReactionEvent event, long logChannelId,
                                       @Nonnull String errorText)
    {
        EmbedBuilder eb = new EmbedBuilder();
        Member reactMember = Objects.requireNonNull(event.getMember());

        eb.setAuthor("Run rejected", null, reactMember.getEffectiveAvatarUrl());
        eb.setColor(Color.RED);
        eb.setDescription(TextUtils.getHyperlink("Message", event.getJumpUrl()));
        eb.setFooter("Message rejected: " + event.getMessageId());
        eb.addField("Error", errorText, false);

        TextChannel channel = event.getGuild().getTextChannelById(logChannelId);

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setContent(reactMember.getAsMention());
        builder.setEmbeds(eb.build());

        channel.sendMessage(builder.build()).queue();
    }

    public static void logFatalError(@Nonnull GenericMessageReactionEvent event, long logChannelId,
                                     @Nonnull String errorText)
    {
        String errorStr = TextUtils.getCodeFormattedText(errorText) + "**Contact my owner.**";
        sendErrorEmbed(event, logChannelId, errorStr);
    }

    public static void logRunnersOutOfBounds(@Nonnull GenericMessageReactionEvent event, long logChannelId,
                                             int uniqueRunnersMentioned)
    {
        sendErrorEmbed(event, logChannelId, "Expected 2 to 8 runners, got: " + uniqueRunnersMentioned);
    }

    public static void logSuccessfulReactionAdd(@Nonnull GenericMessageReactionEvent event, long logChannelId,
                                                @Nonnull String changes)
    {
        EmbedBuilder eb = new EmbedBuilder();
        Member reactMember = Objects.requireNonNull(event.getMember());

        eb.setAuthor("Run approved", null, reactMember.getEffectiveAvatarUrl());
        eb.setColor(new Color(114, 137, 218));
        eb.setDescription(TextUtils.getHyperlink("Message", event.getJumpUrl()));
        eb.setFooter("Message approved: " + event.getMessageId());

        eb.addField("Changes", changes, false);
        TextChannel channel = Objects.requireNonNull(event.getGuild().getTextChannelById(logChannelId));
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void logSuccessfulReactionRemoval(@Nonnull GenericMessageEvent event, long logChannelId,
                                                    @Nonnull Member reactMember, @Nonnull String changes)
    {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Run reverted", null, reactMember.getEffectiveAvatarUrl());
        eb.setColor(Color.ORANGE);
        eb.setDescription(TextUtils.getHyperlink("Message", event.getJumpUrl()));
        eb.setFooter("Message reverted: " + event.getMessageId());

        eb.addField("Changes", changes, false);
        TextChannel channel = Objects.requireNonNull(event.getGuild().getTextChannelById(logChannelId));
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void logSuccessfulReactionAllRemoval(@Nonnull GenericMessageEvent event, long logChannelId,
                                                       @Nonnull String changes)
    {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Run reverted");
        eb.setColor(Color.ORANGE);
        eb.setDescription(TextUtils.getHyperlink("Message", event.getJumpUrl()));
        eb.setFooter("Message reverted: " + event.getMessageId());

        eb.addField("Changes", changes, false);
        TextChannel channel = Objects.requireNonNull(event.getGuild().getTextChannelById(logChannelId));
        channel.sendMessageEmbeds(eb.build()).queue();
    }
}
