package com.dioextreme.nully.module.tally.reaction.processor;

import com.dioextreme.nully.discord.entity.MessageOrigin;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.Mode;
import com.dioextreme.nully.module.tally.entity.Run;
import com.dioextreme.nully.module.tally.entity.Runner;
import com.dioextreme.nully.module.tally.log.DiscordLogger;
import com.dioextreme.nully.module.tally.log.LogContentCreator;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ReactionAddProcessor
{
    public void execute(@Nonnull MessageReactionAddEvent event, @Nonnull Run run)
    {
        TallyDAO dao = new TallyDAO();

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getChannel().getIdLong();
        long messageId = event.getMessageIdLong();
        MessageOrigin messageOrigin = new MessageOrigin(guildId, channelId, messageId);

        long reactMemberId = Objects.requireNonNull(event.getMember()).getIdLong();

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

        // Check if run is already reacted to
        // Just save the member who reacted if that's the case
        try
        {
            if (dao.isRunReacted(messageOrigin))
            {
                dao.addRunReaction(messageOrigin, reactMemberId);
                return;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            DiscordLogger.logFatalError(event, logChannelId, e.getMessage());
            return;
        }

        if (!run.isValid())
        {
            DiscordLogger.logRunnersOutOfBounds(event, logChannelId, run.getNumberOfRunners());
            return;
        }

        try
        {
            Mode activeMode = dao.getActiveMode(guildId);

            run.setActiveMode(activeMode);
            run.analyze();

            List<Runner> runners = run.getRunners();
            dao.updateMembers(guildId, runners);
            dao.addRun(messageOrigin, runners);
            dao.addRunReaction(messageOrigin, reactMemberId);

            String changes = LogContentCreator.getReactionAddChanges(runners);
            DiscordLogger.logSuccessfulReactionAdd(event, logChannelId, changes);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            DiscordLogger.logFatalError(event, logChannelId, e.getMessage());
        }
    }
}
