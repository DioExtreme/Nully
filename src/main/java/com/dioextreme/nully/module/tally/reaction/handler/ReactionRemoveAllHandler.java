package com.dioextreme.nully.module.tally.reaction.handler;

import com.dioextreme.nully.module.tally.reaction.processor.ReactionRemoveAllProcessor;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;

import javax.annotation.Nonnull;

public class ReactionRemoveAllHandler
{
    private final ReactionRemoveAllProcessor reactionRemoveAllProcessor = new ReactionRemoveAllProcessor();
    public void execute(@Nonnull MessageReactionRemoveAllEvent event)
    {
        reactionRemoveAllProcessor.process(event);
    }
}
