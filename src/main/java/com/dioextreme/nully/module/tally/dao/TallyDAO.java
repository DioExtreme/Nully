package com.dioextreme.nully.module.tally.dao;

import com.dioextreme.nully.database.DataSource;
import com.dioextreme.nully.discord.entity.MessageOrigin;
import com.dioextreme.nully.module.tally.entity.*;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TallyDAO
{
    public void addTallyGuild(long guildId, SetupConfig setupOptions) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(TallyGuildStatement.ADD_TALLY_GUILD);
                ps1.setLong(1, guildId);
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(TallyGuildStatement.ADD_LOG_CHANNEL);
                ps2.setLong(1, guildId);
                long logChannelId = Long.parseUnsignedLong(setupOptions.getLogChannel().getValue());
                ps2.setLong(2, logChannelId);
                ps2.executeUpdate();

                PreparedStatement ps3 = conn.prepareStatement(TallyRunStatement.ADD_TALLY_RUN_CHANNEL);
                ps3.setLong(1, guildId);
                long runChannelId = Long.parseUnsignedLong(setupOptions.getRunChannel().getValue());
                ps3.setLong(2, runChannelId);
                ps3.executeUpdate();

                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    private void queryThenAddMilestoneActions(long guildId, Milestone milestone, String sql) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(sql);
                ps1.setLong(1, guildId);
                ps1.setInt(2, milestone.getPointsTrigger());
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(TallyGuildStatement.ADD_MILESTONE_ACTIONS);

                for (MilestoneAction action : milestone.getActions())
                {
                    int actionType = action.getTypeId();
                    long actionValue = action.getValue();

                    ps2.setInt(1, actionType);
                    ps2.setLong(2, actionValue);
                    ps2.setLong(3, guildId);
                    ps2.setLong(4, milestone.getPointsTrigger());

                    ps2.addBatch();
                }

                ps2.executeBatch();
                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    public void addMilestone(long guildId, Milestone milestone) throws SQLException
    {
        queryThenAddMilestoneActions(guildId, milestone, TallyGuildStatement.ADD_MILESTONE);
    }

    public void editMilestone(long guildId, Milestone milestone) throws SQLException
    {
        queryThenAddMilestoneActions(guildId, milestone, TallyGuildStatement.REMOVE_MILESTONE_ACTIONS);
    }

    public void removeMilestone(long guildId, int rankedTrigger) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.REMOVE_MILESTONE))
        {
            ps.setLong(1, guildId);
            ps.setInt(2, rankedTrigger);
            ps.executeUpdate();
        }
    }

    private void queryThenAddModeOptions(long guildId, Mode mode, String sql) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(sql);
                ps1.setLong(1, guildId);
                ps1.setString(2, mode.getName());
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(TallyGuildStatement.ADD_RUN_MODE_OPTIONS);

                for (var option : mode.getOptions().entrySet())
                {
                    int optionType = option.getKey();
                    int optionValue = option.getValue();

                    ps2.setInt(1, optionType);
                    ps2.setInt(2, optionValue);
                    ps2.setLong(3, guildId);
                    ps2.setString(4, mode.getName());

                    ps2.addBatch();
                }

                ps2.executeBatch();
                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    public void addRunMode(long guildId, Mode mode) throws SQLException
    {
        queryThenAddModeOptions(guildId, mode, TallyGuildStatement.ADD_RUN_MODE);
    }
    public void editRunMode(long guildId, Mode mode) throws SQLException
    {
        queryThenAddModeOptions(guildId, mode, TallyGuildStatement.REMOVE_MODE_OPTIONS);
    }

    public boolean isRunMode(long guildId, String modeName) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyRunStatement.IS_TALLY_RUN_MODE))
        {
            ps.setLong(1, guildId);
            ps.setString(2, modeName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public void removeRunMode(long guildId, String modeName) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.REMOVE_MODE))
        {
            ps.setLong(1, guildId);
            ps.setString(2, modeName);
            ps.executeUpdate();
        }
    }

    public void changeActiveMode(long guildId, String modeName) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.CHANGE_ACTIVE_MODE))
        {
            ps.setLong(1, guildId);
            ps.setString(2, modeName);
            ps.executeUpdate();
        }
    }


    public List<RankedMember> getPendingMembersWithRankedPoints(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_PENDING_MEMBERS_WITH_RANKED_POINTS))
        {
            ps.setLong(1, guildId);
            ResultSet rs = ps.executeQuery();

            List<RankedMember> members = new ArrayList<>();

            while (rs.next())
            {
                long memberId = rs.getLong(1);
                int pendingPoints = rs.getInt(2);
                int rankedPoints = rs.getInt(3);
                RankedMember member = new RankedMember(memberId, pendingPoints, rankedPoints);
                members.add(member);
            }
            return members;
        }
    }

    public void updateGuildFromSave(long guildId, List<RankedMember> members) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(TallyGuildStatement.UPDATE_LEADERBOARD_FROM_SAVE);
                for (RankedMember member : members)
                {
                    long memberId = member.getMemberId();
                    int newRankedPoints = member.getRankedPoints();
                    ps1.setInt(1, newRankedPoints);
                    ps1.setLong(2, guildId);
                    ps1.setLong(3, memberId);
                    ps1.addBatch();
                }
                ps1.executeBatch();

                PreparedStatement ps2 = conn.prepareStatement(TallyRunStatement.REMOVE_ALL_TALLY_RUNS);
                ps2.setLong(1, guildId);
                ps2.executeUpdate();

                PreparedStatement ps3 = conn.prepareStatement(TallyGuildStatement.REMOVE_ALL_PENDING_MEMBERS);
                ps3.setLong(1, guildId);
                ps3.executeUpdate();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    public long getLogChannel(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_LOG_CHANNEL))
        {
            ps.setLong(1, guildId);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return rs.getLong(1);
            }
            return -1;
        }
    }

    public boolean isTallyGuild(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.IS_TALLY_GUILD))
        {
            ps.setLong(1, guildId);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return rs.getBoolean(1);
            }
            return false;
        }
    }

    public boolean isTallyRunChannel(long guildId, long channelId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyRunStatement.IS_TALLY_RUN_CHANNEL))
        {
            ps.setLong(1, guildId);
            ps.setLong(2, channelId);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }

    public List<Integer> getMilestoneTriggers(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_MILESTONE_TRIGGERS))
        {
            ps.setLong(1, guildId);
            ResultSet rs = ps.executeQuery();

            List<Integer> triggers = new ArrayList<>();
            while (rs.next())
            {
                triggers.add(rs.getInt(1));
            }
            return triggers;
        }
    }

    public List<MilestoneAction> getMilestoneActions(long guildId, int pointsTrigger) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_MILESTONE_ACTIONS))
        {
            ps.setLong(1, guildId);
            ps.setInt(2, pointsTrigger);
            ResultSet rs = ps.executeQuery();

            List<MilestoneAction> actions = new ArrayList<>();
            while (rs.next())
            {
                int actionTypeId = rs.getInt(1);
                long actionValue = rs.getLong(2);
                MilestoneAction action = new MilestoneAction(actionTypeId, actionValue);
                actions.add(action);
            }
            return actions;
        }
    }

    public List<String> getRunModes(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_MODES))
        {
            ps.setLong(1, guildId);
            ResultSet rs = ps.executeQuery();

            List<String> modes = new ArrayList<>();
            while (rs.next())
            {
                modes.add(rs.getString(1));
            }
            return modes;
        }
    }

    public Map<Integer, Integer> getRunModeOptions(long guildId, String modeName) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_MODE_OPTIONS))
        {
            ps.setLong(1, guildId);
            ps.setString(2, modeName);
            ResultSet rs = ps.executeQuery();

            Map<Integer, Integer> options = new HashMap<>();
            while (rs.next())
            {
                int optionType = rs.getInt(1);
                int optionValue = rs.getInt(2);
                options.putIfAbsent(optionType, optionValue);
            }
            return options;
        }
    }

    public Mode getActiveMode(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyRunStatement.GET_ACTIVE_RUN_MODE))
        {
            ps.setLong(1, guildId);

            ResultSet rs = ps.executeQuery();

            String modeName;
            Map<Integer, Integer> options = new HashMap<>();

            if (!rs.next())
            {
                return null;
            }

            modeName = rs.getString(1);
            int option = rs.getInt(2);
            int option_value = rs.getInt(3);
            options.put(option, option_value);

            while (rs.next())
            {
                option = rs.getInt(2);
                option_value = rs.getInt(3);
                options.put(option, option_value);
            }

            return new Mode(modeName, options);
        }
    }

    public void updateMember(long guildId, @Nonnull Member member) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(TallyMemberStatement.ADD_MEMBER_IF_NOT_EXIST);
                PreparedStatement ps2 = conn.prepareStatement(TallyMemberStatement.ADD_OR_UPDATE_MEMBER_NAME);

                long memberId = member.getIdLong();
                String memberName = member.getEffectiveName();

                ps1.setLong(1, guildId);
                ps1.setLong(2, memberId);
                ps1.addBatch();

                ps2.setString(1, memberName);
                ps2.setLong(2, guildId);
                ps2.setLong(3, memberId);
                ps2.addBatch();

                ps1.executeBatch();
                ps2.executeBatch();

                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateMembers(long guildId, @Nonnull List<Runner> runners) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(TallyMemberStatement.ADD_MEMBER_IF_NOT_EXIST);
                PreparedStatement ps2 = conn.prepareStatement(TallyMemberStatement.ADD_OR_UPDATE_MEMBER_NAME);
                for (Runner runner : runners)
                {
                    long memberId = runner.getMemberId();
                    String memberName = runner.getName();

                    ps1.setLong(1, guildId);
                    ps1.setLong(2, memberId);
                    ps1.addBatch();

                    ps2.setString(1, memberName);
                    ps2.setLong(2, guildId);
                    ps2.setLong(3, memberId);
                    ps2.addBatch();
                }
                ps1.executeBatch();
                ps2.executeBatch();
                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    public void addRun(@Nonnull MessageOrigin messageOrigin, @Nonnull List<Runner> runners) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(TallyRunStatement.ADD_TALLY_RUN);
                ps1.setLong(1, messageOrigin.getMessageId());
                ps1.setLong(2, messageOrigin.getGuildId());
                ps1.setLong(3, messageOrigin.getChannelId());
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(TallyRunStatement.ADD_TALLY_RUN_RUNNERS);
                PreparedStatement ps3 = conn.prepareStatement(TallyRunStatement.ADD_TALLY_RUN_RUNNER_REWARDS);
                PreparedStatement ps4 = conn.prepareStatement(TallyRunStatement.ADD_TALLY_RUN_RUNNER_PENDING_POINTS);

                for (Runner runner : runners)
                {
                    long memberId = runner.getMemberId();
                    int pointsAwarded = runner.getPointsAwarded();

                    ps2.setLong(1, messageOrigin.getGuildId());
                    ps2.setLong(2, runner.getMemberId());
                    ps2.setLong(3, messageOrigin.getChannelId());
                    ps2.setLong(4, messageOrigin.getMessageId());
                    ps2.addBatch();

                    ps3.setInt(1, pointsAwarded);
                    ps3.setLong(2, messageOrigin.getGuildId());
                    ps3.setLong(3, memberId);
                    ps3.setLong(4, messageOrigin.getChannelId());
                    ps3.setLong(5, messageOrigin.getMessageId());
                    ps3.addBatch();

                    ps4.setLong(1, pointsAwarded);
                    ps4.setLong(2, messageOrigin.getGuildId());
                    ps4.setLong(3, memberId);
                    ps4.addBatch();
                }
                ps2.executeBatch();
                ps3.executeBatch();
                ps4.executeBatch();

                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    public void removeRun(MessageOrigin messageOrigin) throws SQLException
    {
        try (Connection conn = DataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                PreparedStatement ps1 = conn.prepareStatement(TallyRunStatement.REMOVE_TALLY_RUN_RUNNER_PENDING_POINTS);
                ps1.setLong(1, messageOrigin.getGuildId());
                ps1.setLong(2, messageOrigin.getChannelId());
                ps1.setLong(3, messageOrigin.getMessageId());
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(TallyRunStatement.REMOVE_TALLY_RUN);
                ps2.setLong(1, messageOrigin.getGuildId());
                ps2.setLong(2, messageOrigin.getChannelId());
                ps2.setLong(3, messageOrigin.getMessageId());
                ps2.executeUpdate();

                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Runner> getRunChanges(MessageOrigin messageOrigin) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyRunStatement.GET_TALLY_RUN_CHANGES))
        {
            ps.setLong(1, messageOrigin.getGuildId());
            ps.setLong(2, messageOrigin.getChannelId());
            ps.setLong(3, messageOrigin.getMessageId());

            ResultSet rs = ps.executeQuery();

            List<Runner> runners = new ArrayList<>();

            while (rs.next())
            {
                String memberName = rs.getString(1);
                int points = rs.getInt(2);

                Runner runner = new Runner(memberName, points);
                runners.add(runner);
            }

            return runners;
        }
    }

    public void addToMemberPendingPoints(long guildId, long memberId, int points) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyMemberStatement.ADD_TO_MEMBER_PENDING_POINTS))
        {
            ps.setInt(1, points);
            ps.setLong(2, guildId);
            ps.setLong(3, memberId);
            ps.executeUpdate();
        }
    }

    public boolean isRunReacted(MessageOrigin messageOrigin) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyRunStatement.IS_TALLY_RUN_REACTED))
        {
            ps.setLong(1, messageOrigin.getGuildId());
            ps.setLong(2, messageOrigin.getChannelId());
            ps.setLong(3, messageOrigin.getMessageId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public PendingMember getPendingMember(long guildId, long memberId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyMemberStatement.GET_PENDING_MEMBER))
        {
            ps.setLong(1, guildId);
            ps.setLong(2, memberId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                String name = rs.getString(2);
                int points = rs.getInt(3);

                return new PendingMember(memberId, name, points);
            }
            return null;
        }
    }

    private void executeReactionQuery(MessageOrigin messageOrigin, long reactMemberId, String sql) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, messageOrigin.getGuildId());
            ps.setLong(2, reactMemberId);
            ps.setLong(3, messageOrigin.getChannelId());
            ps.setLong(4, messageOrigin.getMessageId());
            ps.executeUpdate();
        }
    }

     public void addRunReaction(MessageOrigin messageOrigin, long reactMemberId) throws SQLException
     {
         executeReactionQuery(messageOrigin, reactMemberId, TallyRunStatement.ADD_TALLY_RUN_REACTION);

     }

    public void removeRunReaction(MessageOrigin messageOrigin, long reactMemberId) throws SQLException
    {
        executeReactionQuery(messageOrigin, reactMemberId, TallyRunStatement.REMOVE_TALLY_RUN_REACTION);
    }

    private int executePointsQuery(long guildId, long memberId, String sql) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, guildId);
            ps.setLong(2, memberId);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public int getMemberPendingPoints(long guildId, long memberId) throws SQLException
    {
        return executePointsQuery(guildId, memberId, TallyMemberStatement.GET_MEMBER_PENDING_POINTS);
    }

    public int getMemberLeaderboardPoints(long guildId, long memberId) throws SQLException
    {
        return executePointsQuery(guildId, memberId, TallyMemberStatement.GET_MEMBER_LEADERBOARD_POINTS);
    }

    public int getMemberLeaderboardRank(long guildId, long memberId) throws SQLException
    {
        return executePointsQuery(guildId, memberId, TallyMemberStatement.GET_MEMBER_LEADERBOARD_RANK);
    }

    public int getMemberNextMilestoneTrigger(long guildId, long memberId) throws SQLException
    {
        return executePointsQuery(guildId, memberId, TallyMemberStatement.GET_MEMBER_NEXT_MILESTONE_TRIGGER);
    }

    public List<PendingMember> getPendingMembers(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_ALL_PENDING_MEMBERS))
        {
            ps.setLong(1, guildId);
            ResultSet rs = ps.executeQuery();

            List<PendingMember> pendingMembers = new ArrayList<>();

            while (rs.next())
            {
                long memberId = rs.getLong(1);
                String memberName = rs.getString(2);
                int points = rs.getInt(3);

                PendingMember member = new PendingMember(memberId, memberName, points);
                pendingMembers.add(member);
            }
            return pendingMembers;
        }
    }

    public int getNumLeaderboardPages(long guildId) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_NUMBER_LEADERBOARD_PAGES))
        {
            ps.setLong(1, guildId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                int numEntries = rs.getInt(1);
                int maxPages = numEntries / 10 + 1;
                if (numEntries != 0 && numEntries % 10 == 0)
                {
                    maxPages -= 1;
                }
                return maxPages;
            }
            return 0;
        }
    }

    public List<RankedMember> getLeaderboardPage(long guildId, int page) throws SQLException
    {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(TallyGuildStatement.GET_LEADERBOARD_PAGE))
        {
            ps.setLong(1, guildId);
            // Paginate 10 entries
            ps.setInt(2, (page - 1) * 10);

            ResultSet rs = ps.executeQuery();

            List<RankedMember> leaderboardPage = new ArrayList<>();
            while (rs.next())
            {
                String memberName = rs.getString(1);
                int points = rs.getInt(2);

                RankedMember entry = new RankedMember(memberName, points);
                leaderboardPage.add(entry);
            }
            return leaderboardPage;
        }
    }
}
