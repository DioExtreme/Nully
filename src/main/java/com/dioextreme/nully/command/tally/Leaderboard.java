package com.dioextreme.nully.command.tally;

import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.GuildSlashCommand;
import com.dioextreme.nully.command.annotation.CommandOption;
import com.dioextreme.nully.discord.builder.EmbedBuilder;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.RankedMember;
import com.dioextreme.nully.module.tally.image.LeaderboardImageGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@CommandOption(type = OptionType.INTEGER, name = "page", description = "The page to show", required = false)
public class Leaderboard implements GuildSlashCommand
{
    @Override
    public void executeCommand(@Nonnull SlashCommandInteractionEvent event)
    {
        int page = event.getOption("page", 1, OptionMapping::getAsInt);

        if (page <= 0)
        {
            page = 1;
        }

        TallyDAO dao = new TallyDAO();
        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();

        int maxPages;
        try
        {
            maxPages = dao.getNumLeaderboardPages(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

        if (page > maxPages)
        {
            page = 1;
        }

        List<RankedMember> leaderboardPage;
        try
        {
            leaderboardPage = dao.getLeaderboardPage(guildId, page);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

        LeaderboardImageGenerator lbImageGenerator = new LeaderboardImageGenerator();

        // todo, add per-guild foreground color support
        BufferedImage leaderboardImage = lbImageGenerator.generate(leaderboardPage, page, null);

        // We can't pass a BufferedImage to an Embed
        // Convert it to a byte stream and pass it as an attachment
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try
        {
            ImageIO.write(leaderboardImage, "png", bytes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder(event);
        eb.setColor(Color.WHITE);
        eb.setImage("attachment://img.png");
        eb.setFooter("Page " + page + "/" + maxPages);

        event.replyEmbeds(eb.build())
                .addFiles(FileUpload.fromData(bytes.toByteArray(), "img.png"))
                .queue();
    }

    @Override
    public String getName()
    {
        return "leaderboard";
    }

    @Override
    public String getDescription()
    {
        return "Shows the leaderboard of this server";
    }

    @Override
    public GuildCommandType getType()
    {
        return GuildCommandType.TALLY;
    }
}
