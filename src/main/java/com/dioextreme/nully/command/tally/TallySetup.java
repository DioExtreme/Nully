package com.dioextreme.nully.command.tally;

import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.GuildSlashCommand;
import com.dioextreme.nully.command.annotation.RequiredPermission;
import com.dioextreme.nully.discord.interaction.menu.Menu;
import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuListener;
import com.dioextreme.nully.module.tally.setup.menu.main.TallyInitMenu;
import com.dioextreme.nully.module.tally.setup.menu.main.TallyMainMenu;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.Objects;

@RequiredPermission(Permission.MANAGE_SERVER)
public class TallySetup implements GuildSlashCommand
{
    @Override
    public void executeCommand(@Nonnull SlashCommandInteractionEvent event)
    {
        TallyDAO dao = new TallyDAO();

        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        Member member = Objects.requireNonNull(event.getMember());
        long memberId = member.getIdLong();

        boolean tallyGuild = false;

        try
        {
            tallyGuild = dao.isTallyGuild(guildId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        TallyMenuListener menuListener = new TallyMenuListener(memberId);
        Menu startMenu;

        if (!tallyGuild)
        {
            startMenu = new TallyInitMenu(menuListener);
        }
        else
        {
            startMenu = new TallyMainMenu(menuListener);
        }

        menuListener.start(event, startMenu);
    }

    @Override
    public String getName()
    {
        return "setuptally";
    }

    @Override
    public String getDescription()
    {
        return "Setup menu for the Tally module.";
    }

    @Override
    public GuildCommandType getType()
    {
        return GuildCommandType.TALLY;
    }
}
