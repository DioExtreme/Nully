package com.dioextreme.nully.module.tally.log;

import com.dioextreme.nully.module.tally.entity.Runner;
import com.dioextreme.nully.utils.TextUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class LogContentCreator
{
    public static String getReactionAddChanges(@Nonnull List<Runner> runners)
    {
        StringBuilder sb = new StringBuilder();

        runners.forEach(runner ->
        {
            String memberName = runner.getName();
            int pointsAwarded = runner.getPointsAwarded();
            sb.append(memberName)
                    .append(" -> Added ")
                    .append(pointsAwarded)
                    .append(" ")
                    .append(TextUtils.getPluralized("point", pointsAwarded))
                    .append("\n");
        });

        return sb.toString();
    }

    public static String getReactionRemovalChanges(@Nonnull List<Runner> runners)
    {
        StringBuilder sb = new StringBuilder();

        runners.forEach((runner) ->
        {
            String memberName = runner.getName();
            int pointsAwarded = runner.getPointsAwarded();

            sb.append(memberName)
                    .append(" -> Removed ")
                    .append(pointsAwarded)
                    .append(" ")
                    .append(TextUtils.getPluralized("point", pointsAwarded))
                    .append("\n");
        });

        return sb.toString();
    }
}
