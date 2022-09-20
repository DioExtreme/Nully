package com.dioextreme.nully.command.tally;

import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.GuildSlashCommand;
import com.dioextreme.nully.command.annotation.CommandOption;
import com.dioextreme.nully.command.annotation.RequiredPermission;
import com.dioextreme.nully.discord.builder.InteractionBuilder;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.data.GuildDataHandler;
import com.dioextreme.nully.module.tally.entity.PendingMember;
import com.dioextreme.nully.utils.TextUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

@RequiredPermission(Permission.MESSAGE_MANAGE)
@CommandOption(type = OptionType.USER, name = "member", description = "A member", required = true)
@CommandOption(type = OptionType.INTEGER, name = "points", description = "The points to add or remove", required = true)
public class  ChangePendingPoints implements GuildSlashCommand
{
    private boolean isUseAllowed(@Nonnull SlashCommandInteractionEvent event, long logChannelId)
    {
        return event.getChannel().getIdLong() == logChannelId;
    }

    @Override
    public void executeCommand(@Nonnull SlashCommandInteractionEvent event)
    {
        GuildDataHandler guildDataHandler = new GuildDataHandler();
        long logChannelId = guildDataHandler.getLogChannelId(event);

        if (isUseAllowed(event, logChannelId))
        {
            event.reply("Moderator commands cannot be used in the log channel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        TallyDAO dao = new TallyDAO();

        Member targetedMember = event.getOption("member").getAsMember();
        int points = event.getOption("points").getAsInt();

        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        long memberId = targetedMember.getIdLong();
        try
        {
            dao.updateMember(guildId, targetedMember);
            dao.addToMemberPendingPoints(guildId, memberId, points);
            PendingMember member = dao.getPendingMember(guildId, memberId);

            String memberNameBold = TextUtils.getBold(member.getName());

            int newPoints = member.getPendingPoints();
            int oldPoints = newPoints - points;

            String changeString = TextUtils.getBold(oldPoints + " -> " + newPoints);

            InteractionBuilder interactionBuilder = new InteractionBuilder();
            MessageCreateData messageData = interactionBuilder.create(event)
                    .withEmbedColor(Color.CYAN)
                    .withEmbedField("Points changed", "Changed pending points for %s:\n%s"
                            .formatted(memberNameBold, changeString), false)
                    .buildAsCreateData();

            JDA jda = event.getJDA();
            TextChannel logChannel = Objects.requireNonNull(jda.getTextChannelById(logChannelId));
            logChannel.sendMessage(messageData).queue();

            event.reply(messageData).queue();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getName()
    {
        return "changepoints";
    }

    @Override
    public String getDescription()
    {
        return "Change pending points of a member";
    }

    @Override
    public GuildCommandType getType()
    {
        return GuildCommandType.TALLY;
    }
}
