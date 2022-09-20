package com.dioextreme.nully.module.tally.setup.parser.mode;

import com.dioextreme.nully.module.tally.entity.Mode;
import com.dioextreme.nully.utils.TextUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ModeParser
{

    private int getOptionId(@Nonnull String optionName)
    {
        String optionNameLowercase = optionName.toLowerCase();
        return switch (optionNameLowercase)
        {
            case "normal" -> ModeOptionType.NORMAL;
            case "author" -> ModeOptionType.AUTHOR;
            case "multi" -> ModeOptionType.MULTIRUNNER;
            default -> -1;
        };
    }

    private String getOptionName(int optionId)
    {
        return switch (optionId)
        {
            case ModeOptionType.NORMAL -> "normal";
            case ModeOptionType.AUTHOR -> "author";
            case ModeOptionType.MULTIRUNNER -> "multi";
            default -> "unknown";
        };
    }

    public Map<Integer, Integer> toMap(String[] options)
    {
        Map<Integer, Integer> optionMap = new HashMap<>();

        int arrayIndex = 0;

        while (arrayIndex < options.length)
        {
            String option = options[arrayIndex];
            String optionValueString = options[arrayIndex + 1];
            int optionValue = Integer.parseUnsignedInt(optionValueString);
            int optionId = getOptionId(option);

            arrayIndex += 2;

            if (optionId < 0)
            {
                continue;
            }

            optionMap.putIfAbsent(optionId, optionValue);
        }
        return optionMap;
    }

    public String fromMap(Map<Integer, Integer> options)
    {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, Integer> action : options.entrySet())
        {
            int optionId = action.getKey();
            int optionValue = action.getValue();

            String optionName = getOptionName(optionId);
            if (optionName.equals("unknown"))
            {
                continue;
            }

            sb.append("%s  %d\n".formatted(optionName, optionValue));
        }
        return sb.toString();
    }

    public String toDiscord(Mode mode)
    {
        StringBuilder sb = new StringBuilder();

        String modeName = mode.getName();
        Map<Integer, Integer> options = mode.getOptions();

        sb.append(TextUtils.getBold(modeName))
            .append(" with options:\n\n");

        for (Map.Entry<Integer, Integer> option : options.entrySet())
        {
            int optionType = option.getKey();
            int optionValue = option.getValue();

            if (optionType == ModeOptionType.NORMAL)
            {
                sb.append(TextUtils.getBold("Normal runner:"));
            }
            else if (optionType == ModeOptionType.AUTHOR)
            {
                sb.append(TextUtils.getBold("Message creator:"));
            }
            else if (optionType == ModeOptionType.MULTIRUNNER)
            {
                sb.append(TextUtils.getBold("Multi runner:"));
            }
            else
            {
                continue;
            }
            sb.append(" %d ".formatted(optionValue))
                .append(TextUtils.getPluralized("point", optionValue))
                .append("\n");
        }
        return sb.toString();
    }
}
