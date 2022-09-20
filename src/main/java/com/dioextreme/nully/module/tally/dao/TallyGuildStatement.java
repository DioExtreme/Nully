package com.dioextreme.nully.module.tally.dao;

public class TallyGuildStatement
{

    public static final String ADD_TALLY_GUILD =
            """
            INSERT INTO tally_guild(guild_id) VALUES (?)
            ON CONFLICT (guild_id) DO
            UPDATE SET enabled = true
            """;

    public static final String ADD_LOG_CHANNEL =
            """
            INSERT INTO tally_guild_log_channel VALUES (?, ?)
            """;

    public static final String ADD_MILESTONE =
            """
            INSERT INTO tally_guild_milestone(guild_id, ranked_points_trigger) VALUES (?,?)
            """;

    public static final String ADD_RUN_MODE =
            """
            INSERT INTO tally_guild_run_mode(guild_id, mode_name) VALUES (?,?)
            """;

    public static final String CHANGE_ACTIVE_MODE =
            """
            INSERT INTO tally_guild_run_mode_config
            SELECT guild_id, mode_id FROM tally_guild_run_mode
            WHERE guild_id = ? AND mode_name = ?
            ON CONFLICT (guild_id) DO
            UPDATE SET active_mode_id = EXCLUDED.active_mode_id
            """;

    public static final String REMOVE_MILESTONE_ACTIONS =
            """
            DELETE FROM tally_guild_milestone_action
            WHERE milestone_id =
            (
            SELECT milestone_id FROM tally_guild_milestone
            WHERE guild_id = ? AND ranked_points_trigger = ?
            )
            """;

    public static final String REMOVE_MODE_OPTIONS =
            """
            DELETE FROM tally_guild_run_mode_option
            WHERE mode_id =
            (
            SELECT mode_id FROM tally_guild_run_mode
            WHERE guild_id = ? AND mode_name = ?
            )
            """;

    public static final String ADD_MILESTONE_ACTIONS =
            """
            INSERT INTO tally_guild_milestone_action(milestone_id, action_type_id, action_value)
            SELECT tgm.milestone_id, ?, ? FROM tally_guild_milestone tgm
            WHERE tgm.guild_id = ? AND tgm.ranked_points_trigger = ?
            """;

    public static final String ADD_RUN_MODE_OPTIONS =
            """
            INSERT INTO tally_guild_run_mode_option(mode_id, option_type_id, option_value)
            SELECT tgrm.mode_id, ?, ? FROM tally_guild_run_mode tgrm
            WHERE tgrm.guild_id = ? AND tgrm.mode_name = ?
            """;

    public static final String IS_TALLY_GUILD =
            """
            SELECT enabled FROM tally_guild
            WHERE guild_id = ?
            """;

    public static final String GET_LOG_CHANNEL =
            """
            SELECT log_channel_id FROM tally_guild tg
            INNER JOIN tally_guild_log_channel tglc ON tglc.guild_id = tg.guild_id
            WHERE tg.guild_id = ?
            """;

    public static final String GET_MILESTONE_TRIGGERS =
            """
            SELECT ranked_points_trigger FROM tally_guild_milestone
            WHERE guild_id = ?
            ORDER BY ranked_points_trigger DESC
            """;

    public static final String GET_MILESTONE_ACTIONS =
            """
            SELECT action_type_id, action_value FROM tally_guild_milestone tgm
            INNER JOIN tally_guild_milestone_action tgma ON tgma.milestone_id = tgm.milestone_id
            WHERE tgm.guild_id = ? AND tgm.ranked_points_trigger = ?
            """;

    public static final String GET_MODES =
            """
            SELECT mode_name FROM tally_guild_run_mode
            WHERE guild_id = ?
            ORDER BY mode_name DESC
            """;

    public static final String GET_MODE_OPTIONS =
            """
            SELECT option_type_id, option_value FROM tally_guild_run_mode tgrm
            INNER JOIN tally_guild_run_mode_option tgrmo ON tgrmo.mode_id = tgrm.mode_id
            WHERE tgrm.guild_id = ? AND tgrm.mode_name = ?
            """;

    public static final String GET_NUMBER_LEADERBOARD_PAGES =
            """
            SELECT COUNT(*) FROM tally_guild_member tm
            INNER JOIN tally_guild_leaderboard tl on tm.tally_member_id = tl.tally_member_id
            WHERE guild_id = ?
            """;

    public static final String GET_LEADERBOARD_PAGE =
            """
            SELECT tn.member_name, tl.ranked_points FROM tally_guild_member tm
            INNER JOIN tally_guild_member_name tn on tm.tally_member_id = tn.tally_member_id
            INNER JOIN tally_guild_leaderboard tl on tm.tally_member_id = tl.tally_member_id
            WHERE tm.guild_id = ?
            ORDER BY tl.ranked_points DESC
            LIMIT 10 OFFSET ?
            """;

    public static final String GET_ALL_PENDING_MEMBERS =
            """
            SELECT tm.member_id, tn.member_name, tp.pending_points FROM tally_guild_member tm
            INNER JOIN tally_guild_member_name tn on tm.tally_member_id = tn.tally_member_id
            INNER JOIN tally_guild_pending tp on tm.tally_member_id = tp.tally_member_id
            WHERE tm.guild_id = ?
            ORDER BY tp.pending_points DESC
            """;

    public static final String GET_PENDING_MEMBERS_WITH_RANKED_POINTS =
            """
            SELECT tgm.member_id, tgp.pending_points, tgl.ranked_points FROM tally_guild_member tgm
            INNER JOIN tally_guild_pending tgp ON tgp.tally_member_id = tgm.tally_member_id
            LEFT JOIN tally_guild_leaderboard tgl ON tgl.tally_member_id = tgp.tally_member_id
            WHERE tgm.guild_id = ? AND tgp.pending_points <> 0
            """;
    public static final String UPDATE_LEADERBOARD_FROM_SAVE =
            """
            INSERT INTO tally_guild_leaderboard
            SELECT tally_member_id, ? FROM tally_guild_member
            WHERE guild_id = ? AND member_id = ?
            ON CONFLICT (tally_member_id) DO
            UPDATE SET ranked_points = EXCLUDED.ranked_points
            """;

    public static final String REMOVE_MILESTONE =
            """
            DELETE FROM tally_guild_milestone
            WHERE guild_id = ? AND ranked_points_trigger = ?
            """;

    public static final String REMOVE_MODE =
            """
            DELETE FROM tally_guild_run_mode
            WHERE guild_id = ? AND mode_name = ?
            """;

    public static final String REMOVE_ALL_PENDING_MEMBERS =
            """
            DELETE FROM tally_guild_pending
            WHERE tally_member_id IN
            (
            SELECT tally_member_id FROM tally_guild_member
            WHERE guild_id = ?
            )
            """;

}
