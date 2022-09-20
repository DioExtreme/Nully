package com.dioextreme.nully.module.tally.reaction.handler;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.module.tally.entity.Run;
import com.dioextreme.nully.module.tally.reaction.processor.ReactionAddProcessor;
import com.dioextreme.nully.module.tally.reaction.utils.TallyReactionUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReactionAddHandler
{
    private final ReactionAddProcessor reactionAddProcessor = new ReactionAddProcessor();
    private void onUsersConvertedToMembers(@Nonnull MessageReactionAddEvent event, @Nonnull Message message,
                                           @Nonnull List<Member> membersMentioned)
    {
        Run run = new Run(message, membersMentioned);
        NullyExecutor.execute(() -> reactionAddProcessor.execute(event, run));
    }

    private void onMessageRetrieved(@Nonnull MessageReactionAddEvent event, @Nonnull Message message)
    {
        // We only get User data from the message
        // Get Member data through the guild
        List<User> usersMentioned = message.getMentions().getUsers();
        Set<User> uniqueUsersMentioned = new HashSet<>(usersMentioned);
        uniqueUsersMentioned.add(message.getAuthor());

        event.getGuild().retrieveMembers(uniqueUsersMentioned)
                .onSuccess(memberList -> onUsersConvertedToMembers(event, message, memberList));
    }

    public void execute(@Nonnull MessageReactionAddEvent event)
    {
        TallyReactionUtils reactionUtils = new TallyReactionUtils();

        if (!reactionUtils.isValidReaction(event, event.getMember()))
        {
            return;
        }

        TextChannel channel = event.getChannel().asTextChannel();
        channel.retrieveMessageById(event.getMessageIdLong())
                .queue(m -> onMessageRetrieved(event, m), null);
    }
}
