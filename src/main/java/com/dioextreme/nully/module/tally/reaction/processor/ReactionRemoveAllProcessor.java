package com.dioextreme.nully.module.tally.reaction.processor;

import com.dioextreme.nully.discord.entity.MessageOrigin;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.Runner;
import com.dioextreme.nully.module.tally.log.DiscordLogger;
import com.dioextreme.nully.module.tally.log.LogContentCreator;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

public class ReactionRemoveAllProcessor
{
    public void process(@Nonnull MessageReactionRemoveAllEvent event)
    {
        TallyDAO dao = new TallyDAO();

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getChannel().getIdLong();
        long messageId = event.getMessageIdLong();
        MessageOrigin messageOrigin = new MessageOrigin(guildId, channelId, messageId);

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
            List<Runner> runners = dao.getRunChanges(messageOrigin);

            if (runners.isEmpty())
            {
                return;
            }

            dao.removeRun(messageOrigin);

            String changes = LogContentCreator.getReactionRemovalChanges(runners);
            DiscordLogger.logSuccessfulReactionAllRemoval(event, logChannelId, changes);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
