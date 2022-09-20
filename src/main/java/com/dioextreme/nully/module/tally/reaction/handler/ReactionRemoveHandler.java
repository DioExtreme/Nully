package com.dioextreme.nully.module.tally.reaction.handler;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.module.tally.reaction.processor.ReactionRemoveProcessor;
import com.dioextreme.nully.module.tally.reaction.utils.TallyReactionUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

import javax.annotation.Nonnull;

public class ReactionRemoveHandler
{
    private final ReactionRemoveProcessor removeProcessor = new ReactionRemoveProcessor();
    private void onReactMemberRetrieved(@Nonnull MessageReactionRemoveEvent event, @Nonnull Member reactMember)
    {
        NullyExecutor.execute(() ->
        {
            TallyReactionUtils reactionUtils = new TallyReactionUtils();
            if (!reactionUtils.isValidReaction(event, reactMember))
            {
                return;
            }

            removeProcessor.execute(event, reactMember);
        });
    }

    public void execute(@Nonnull MessageReactionRemoveEvent event)
    {
        // Turns out we don't get member info on removals
        event.retrieveMember().queue(reactMember -> onReactMemberRetrieved(event, reactMember));
    }
}
