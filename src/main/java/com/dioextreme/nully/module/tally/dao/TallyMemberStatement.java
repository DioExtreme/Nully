package com.dioextreme.nully.module.tally.dao;

public class TallyMemberStatement
{
    public static final String ADD_MEMBER_IF_NOT_EXIST =
            """
            INSERT INTO tally_guild_member(guild_id, member_id) VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """;
    
    public static final String ADD_OR_UPDATE_MEMBER_NAME =
            """
            INSERT INTO tally_guild_member_name
            SELECT tally_member_id, ? FROM tally_guild_member
            WHERE guild_id = ? AND member_id = ?
            ON CONFLICT (tally_member_id) DO
            UPDATE SET member_name = excluded.member_name
            """;

    public static final String ADD_TO_MEMBER_PENDING_POINTS =
            """
            INSERT INTO tally_guild_pending
            SELECT tally_member_id, ? FROM tally_guild_member
            WHERE guild_id = ? AND member_id = ?
            ON CONFLICT (tally_member_id) DO
            UPDATE SET pending_points = tally_guild_pending.pending_points + excluded.pending_points
            """;

    public static final String GET_MEMBER_PENDING_POINTS =
            """
            SELECT pending_points FROM tally_guild_member tm
            INNER JOIN tally_guild_pending tp ON tm.tally_member_id = tp.tally_member_id
            WHERE tm.guild_id = ? AND tm.member_id = ?
            """;

    public static final String GET_PENDING_MEMBER =
            """
            SELECT tm.member_id, member_name, pending_points FROM tally_guild_member tm
            INNER JOIN tally_guild_member_name tgmn ON tgmn.tally_member_id = tm.tally_member_id
            INNER JOIN tally_guild_pending tp ON tp.tally_member_id = tgmn.tally_member_id
            WHERE tm.guild_id = ? AND tm.member_id = ?
            """;

    public static final String GET_MEMBER_LEADERBOARD_POINTS =
            """
            SELECT ranked_points FROM tally_guild_member tm
            INNER JOIN tally_guild_leaderboard tl ON tm.tally_member_id = tl.tally_member_id
            WHERE tm.guild_id = ? AND tm.member_id = ?
            """;

    public static final String GET_MEMBER_LEADERBOARD_RANK =
            """
            SELECT rank FROM
            (
            SELECT member_id, RANK () OVER (ORDER BY tl.ranked_points DESC) rank FROM tally_guild_member tm
            INNER JOIN tally_guild_leaderboard tl on tm.tally_member_id = tl.tally_member_id
            WHERE tm.guild_id = ?
            ) tl
            WHERE member_id = ?
            """;

    public static final String GET_MEMBER_NEXT_MILESTONE_TRIGGER =
            """
            SELECT ranked_points_trigger FROM tally_guild_member tgm
            INNER JOIN tally_guild_leaderboard tgl ON tgl.tally_member_id = tgm.tally_member_id
            INNER JOIN tally_guild_milestone tgml ON tgml.guild_id = tgm.guild_id
            WHERE tgm.guild_id = ? AND tgm.member_id = ? AND ranked_points_trigger > tgl.ranked_points
            ORDER BY ranked_points
            LIMIT 1
            """;
}
