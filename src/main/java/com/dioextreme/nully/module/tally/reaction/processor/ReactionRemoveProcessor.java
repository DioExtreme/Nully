package com.dioextreme.nully.module.tally.reaction.processor;

import com.dioextreme.nully.discord.entity.MessageOrigin;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.Runner;
import com.dioextreme.nully.module.tally.log.DiscordLogger;
import com.dioextreme.nully.module.tally.log.LogContentCreator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

public class ReactionRemoveProcessor
{
    public void execute(@Nonnull MessageReactionRemoveEvent event, @Nonnull Member reactMember)
    {
        TallyDAO dao = new TallyDAO();

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getChannel().getIdLong();
        long messageId = event.getMessageIdLong();
        MessageOrigin messageOrigin = new MessageOrigin(guildId, channelId, messageId);

        long reactMemberId = reactMember.getIdLong();

        long logChannelId;

        try
        {
            logChannelId = dao.getLogChannel(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            dao.removeRunReaction(messageOrigin, reactMemberId);

            // Did someone else react?
            if (dao.isRunReacted(messageOrigin))
            {
                return;
            }

            List<Runner> runners = dao.getRunChanges(messageOrigin);

            if (runners.isEmpty())
            {
                return;
            }

            dao.removeRun(messageOrigin);

            String changes = LogContentCreator.getReactionRemovalChanges(runners);
            DiscordLogger.logSuccessfulReactionRemoval(event, logChannelId, reactMember, changes);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            DiscordLogger.logFatalError(event, logChannelId, e.getMessage());
        }
    }
}
