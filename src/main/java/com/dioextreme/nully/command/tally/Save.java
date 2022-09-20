package com.dioextreme.nully.command.tally;

import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.GuildSlashCommand;
import com.dioextreme.nully.command.annotation.RequiredPermission;
import com.dioextreme.nully.discord.builder.InteractionBuilder;
import com.dioextreme.nully.module.tally.TallySaveListener;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

@RequiredPermission(Permission.MESSAGE_MANAGE)
public class Save implements GuildSlashCommand
{
    private boolean isUseAllowed(@Nonnull SlashCommandInteractionEvent event)
    {
        TallyDAO dao = new TallyDAO();

        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        try
        {
            return event.getChannel().getIdLong() == dao.getLogChannel(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private void sendConfirmation(@Nonnull SlashCommandInteractionEvent event)
    {
        InteractionBuilder interactionBuilder = new InteractionBuilder();
        MessageCreateData data = interactionBuilder.create(event)
                .withEmbedField("Save", "Are you sure? You can not cancel this operation.", false)
                .withEmbedColor(Color.WHITE)
                .withButtons(
                        Button.danger("tally_save_yes", "Yes"),
                        Button.danger("tally_save_no", "No")
                )
                .buildAsCreateData();
        event.reply(data).queue();
    }
    @Override
    public void executeCommand(@Nonnull SlashCommandInteractionEvent event)
    {
        if (isUseAllowed(event))
        {
            event.reply("Moderator commands cannot be used in the log channel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Guild guild = Objects.requireNonNull(event.getGuild());
        TextChannel channel = Objects.requireNonNull(event.getChannel().asTextChannel());
        Member member = Objects.requireNonNull(event.getMember());

        long guildId = guild.getIdLong();
        long channelId = channel.getIdLong();
        long memberId = member.getIdLong();

        TallySaveListener tallySaveListener = new TallySaveListener(guildId, channelId, memberId);

        sendConfirmation(event);
        event.getJDA().addEventListener(tallySaveListener);
    }

    @Override
    public String getName()
    {
        return "save";
    }

    @Override
    public String getDescription()
    {
        return "Apply pending points and distribute milestone rewards";
    }

    @Override
    public GuildCommandType getType()
    {
        return GuildCommandType.TALLY;
    }
}
