package com.dioextreme.nully.module.tally.entity;

import com.dioextreme.nully.module.tally.reaction.parser.MessageParser;
import com.dioextreme.nully.module.tally.setup.parser.mode.ModeOptionType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Run
{
    private final long authorId;
    private final String messageContent;
    private final List<Runner> runners = new ArrayList<>();
    private Mode activeMode;

    public Run(@Nonnull Message message, @Nonnull List<Member> membersMentioned)
    {
        this.authorId = message.getAuthor().getIdLong();
        this.messageContent = message.getContentRaw();

        for (Member member : membersMentioned)
        {
            Runner runner = new Runner(member.getIdLong(), member.getEffectiveName());
            runners.add(runner);
        }
    }

    public List<Runner> getRunners()
    {
        return runners;
    }

    public int getNumberOfRunners()
    {
        return runners.size();
    }

    public void setActiveMode(Mode activeMode)
    {
        this.activeMode = activeMode;
    }

    public boolean isValid()
    {
        // We do not allow runs with:
        // - Only the poster
        // - More than 8 runners (the game does not support such thing)
        int numRunners = runners.size();
        return numRunners > 1 && numRunners <= 8;
    }

    public void analyze()
    {
        if (activeMode == null)
        {
            runners.forEach(runner -> runner.setPointsAwarded(1));
            return;
        }

        Map<Long, Integer> conditionalPointsAwarded = new HashMap<>();
        int authorPoints = activeMode.getOption(ModeOptionType.AUTHOR);

        if (authorPoints != 1)
        {
            conditionalPointsAwarded.put(authorId, authorPoints);
        }
        int multiRunnerPoints = activeMode.getOption(ModeOptionType.MULTIRUNNER);

        if (multiRunnerPoints != 1)
        {
            MessageParser messageParser = new MessageParser();
            Map<Long, Integer> multiRunners = messageParser.parseMultiRunners(messageContent);
            conditionalPointsAwarded.putAll(multiRunners);
        }

        int defaultPoints = activeMode.getOption(ModeOptionType.NORMAL);

        runners.forEach(runner ->
        {
            int pointsAwarded = conditionalPointsAwarded.getOrDefault(runner.getMemberId(), defaultPoints);
            runner.setPointsAwarded(pointsAwarded);
        });
    }
}
