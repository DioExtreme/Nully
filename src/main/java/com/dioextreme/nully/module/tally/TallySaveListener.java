package com.dioextreme.nully.module.tally;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.discord.builder.InteractionBuilder;
import com.dioextreme.nully.module.tally.data.GuildDataHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

public class TallySaveListener extends ListenerAdapter
{
    private final long guildId;
    private final long channelId;
    private final long memberId;

    private final long timeOnObjectCreation;

    public TallySaveListener(long guildId, long channelId, long memberId)
    {
        this.guildId = guildId;
        this.channelId = channelId;
        this.memberId = memberId;
        this.timeOnObjectCreation = System.currentTimeMillis();
    }

    private void replySuccess(@Nonnull ButtonInteractionEvent event, long logChannelId)
    {

        InteractionBuilder interactionBuilder = new InteractionBuilder();

        interactionBuilder.create(event)
                .withEmbedColor(Color.GREEN)
                .withEmbedField("Save complete", "Ranked points saved and milestone rewards are being distributed. " +
                        "", false)
                .withEmbedFooter("If there's a high milestone workload, this might take minutes.")
                .withEmptyComponents();

        MessageEditData userReply = interactionBuilder.buildAsEditData();
        event.getHook().editOriginal(userReply).queue();

        String url = event.getMessage().getJumpUrl();
        MessageCreateData logReply = interactionBuilder
                .withEmbedSource(url)
                .buildAsCreateData();

        JDA jda = event.getJDA();
        TextChannel logChannel = Objects.requireNonNull(jda.getTextChannelById(logChannelId));
        logChannel.sendMessage(logReply).queue();
    }

    private void replyFailure(@Nonnull ButtonInteractionEvent event)
    {
        InteractionBuilder interactionBuilder = new InteractionBuilder();

        MessageEditData messageData = interactionBuilder.create(event)
                .withEmbedColor(Color.RED)
                .withEmbedField("Save failed", "An error occurred while saving data. " +
                        "No data has been lost." +
                        "", false)
                .withEmbedFooter("If there's a high milestone workload, this might take minutes.")
                .withEmptyComponents()
                .buildAsEditData();

        event.getHook().editOriginal(messageData).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        long timeOnEvent = System.currentTimeMillis();

        if (timeOnEvent - timeOnObjectCreation > 30000L)
        {
            event.getJDA().removeEventListener(this);
            event.deferEdit().queue();
            event.getMessage().delete().queue();
            return;
        }

        if (!event.getComponentId().equals("tally_save_yes"))
        {
            return;
        }

        Guild guild = Objects.requireNonNull(event.getGuild());
        TextChannel channel = Objects.requireNonNull(event.getChannel().asTextChannel());
        Member member = Objects.requireNonNull(event.getMember());

        long guildId = guild.getIdLong();
        long channelId = channel.getIdLong();
        long memberId = member.getIdLong();

        if (this.guildId != guildId || this.channelId != channelId || this.memberId != memberId)
        {
            return;
        }

        event.getJDA().removeEventListener(this);
        event.deferEdit().queue();

        NullyExecutor.execute(() ->
        {
            try
            {
                GuildDataHandler guildDataHandler = new GuildDataHandler();
                guildDataHandler.saveTally(guild);
                long logChannelId = guildDataHandler.getLogChannelId(event);
                replySuccess(event, logChannelId);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                replyFailure(event);
            }
        });
    }
}
