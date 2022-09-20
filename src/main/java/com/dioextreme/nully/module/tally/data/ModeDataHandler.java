package com.dioextreme.nully.module.tally.data;

import com.dioextreme.nully.module.tally.dao.TallyDAO;
import com.dioextreme.nully.module.tally.entity.Mode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModeDataHandler
{
    private final TallyDAO dao = new TallyDAO();

    public void addMode(@Nonnull GenericInteractionCreateEvent event, Mode mode) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        dao.addRunMode(guildId, mode);
    }

    public void editMode(@Nonnull GenericInteractionCreateEvent event, Mode mode) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        String modeName = mode.getName();
        Map<Integer, Integer> modeOptions = mode.getOptions();

        dao.editRunMode(guildId, mode);
    }

    public boolean isMode(long guildId, Mode mode) throws SQLException
    {
        String modeName = mode.getName();
        return dao.isRunMode(guildId, modeName);
    }

    public boolean isMode(long guildId, String modeName) throws SQLException
    {
        return dao.isRunMode(guildId, modeName);
    }

    public void removeMode(@Nonnull GenericInteractionCreateEvent event, Mode mode) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        String modeName = mode.getName();
        dao.removeRunMode(guildId, modeName);
    }

    public List<String> getModes(@Nonnull GenericInteractionCreateEvent event) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        return dao.getRunModes(guildId);
    }

    public Map<Integer, Integer> getModeOptions(@Nonnull GenericInteractionCreateEvent event, Mode mode) throws SQLException
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        long guildId = guild.getIdLong();

        String modeName = mode.getName();

        return dao.getRunModeOptions(guildId, modeName);
    }

    public Map<Integer, Integer> getModeOptions(long guildId, String modeName) throws SQLException
    {
        return dao.getRunModeOptions(guildId, modeName);
    }

    public void changeActiveMode(long guildId, String modeName) throws SQLException
    {
        dao.changeActiveMode(guildId, modeName);
    }
}
