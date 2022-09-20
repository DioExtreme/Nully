package com.dioextreme.nully.module.tally;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.module.tally.reaction.handler.ReactionAddHandler;
import com.dioextreme.nully.module.tally.reaction.handler.ReactionRemoveAllHandler;
import com.dioextreme.nully.module.tally.reaction.handler.ReactionRemoveHandler;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class TallyMessageListener extends ListenerAdapter
{
    private final ReactionAddHandler reactionAddHandler = new ReactionAddHandler();
    private final ReactionRemoveHandler reactionRemoveHandler = new ReactionRemoveHandler();
    private final ReactionRemoveAllHandler reactionRemoveAllHandler = new ReactionRemoveAllHandler();

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
    {
        NullyExecutor.execute(() -> reactionAddHandler.execute(event));
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event)
    {
        NullyExecutor.execute(() -> reactionRemoveHandler.execute(event));
    }

    @Override
    public void onMessageReactionRemoveAll(@Nonnull MessageReactionRemoveAllEvent event)
    {
        NullyExecutor.execute(() -> reactionRemoveAllHandler.execute(event));
    }
}
