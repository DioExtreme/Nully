package com.dioextreme.nully.module.tally.reaction.parser;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class MessageParser
{
    /*
     * The format intended to be parsed is:
     * <@memberId> <numberOfCharacters>
     * repeated at most 8 times.
     *
     * Some users tend to alter this format ever so slightly.
     * This makes regexes very hard to use.
     *
     * For example: they may put the amount of "guests" at the end
     * of the message, or they may put the total runners somewhere in
     * the message.
     *
     * This parser handles most cases. To minimize error, the parser
     * reads the message backwards. This is most needed to handle cases
     * where the amount of guests is included.
     *
     * The parser also handles the case where <@memberId> might appear
     * as <@!memberId> in certain cases (usually when a nickname is involved).
     */
    public Map<Long, Integer> parseMultiRunners(@Nonnull String messageContent)
    {
        int userIdMinDigits = 17;
        int userIdMaxDigits = 20;

        boolean numberIsFromGuestCount = false;
        int numbersAfterGuestTag = 0;

        int valueBeforeFirstRunnerFound = -1;
        int toonCount = valueBeforeFirstRunnerFound;

        Map<Long, Integer> multiRunners = new HashMap<>();
        char[] messageContentArray = messageContent.toCharArray();

        int contentLength = messageContentArray.length;
        int contentIndex = contentLength - 1;

        while (contentIndex >= 0)
        {
            char ch = messageContentArray[contentIndex];
            --contentIndex;

            if (Character.isDigit(ch))
            {
                toonCount = Character.getNumericValue(ch);
                if (numberIsFromGuestCount)
                {
                    ++numbersAfterGuestTag;
                    if (numbersAfterGuestTag > 1)
                    {
                        numberIsFromGuestCount = false;
                        numbersAfterGuestTag = 0;
                    }
                }
            }
            else if (ch == '>')
            {
                long memberId = 0;
                int digitsRead = 0;

                // Reconstruct the memberId by reading forwards
                int memberIdEndIndex = contentLength - (contentLength - contentIndex);
                int memberIdStartIndex = memberIdEndIndex - userIdMinDigits;

                if (memberIdStartIndex < 0)
                {
                    memberIdStartIndex = 0;
                }
                else
                {
                    while (memberIdStartIndex > 0 && Character.isDigit(messageContentArray[memberIdStartIndex]))
                    {
                        memberIdStartIndex--;
                    }
                }

                for (int i = memberIdStartIndex; i <= memberIdEndIndex; i++)
                {
                    char c = messageContentArray[i];
                    if (Character.isDigit(c))
                    {
                        ++digitsRead;
                        memberId = memberId * 10 + Character.getNumericValue(c);
                    }
                }
                if (digitsRead >= userIdMinDigits && digitsRead <= userIdMaxDigits)
                {
                    // "<@" always exists so decrease accordingly
                    contentIndex -= (2 + digitsRead);

                    if (numberIsFromGuestCount)
                    {
                        numberIsFromGuestCount = false;
                    }
                    else if (toonCount > 1)
                    {
                        multiRunners.put(memberId, toonCount);
                        toonCount = 0;
                    }
                }
            }
            else if (toonCount == valueBeforeFirstRunnerFound)
            {
                if (ch == 'g' || ch == 'G')
                {
                    numberIsFromGuestCount = true;
                }
                else if (ch == '+')
                {
                    numberIsFromGuestCount = false;
                }
            }
        }
        return multiRunners;
    }
}
