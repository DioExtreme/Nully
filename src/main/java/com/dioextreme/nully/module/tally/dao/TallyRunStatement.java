package com.dioextreme.nully.module.tally.dao;

public class TallyRunStatement
{
        public static final String ADD_TALLY_RUN_CHANNEL =
                """
                INSERT INTO tally_guild_run_channel(guild_id, channel_id) VALUES (?, ?)
                """;

        public static final String ADD_TALLY_RUN =
                """
                INSERT INTO tally_run(run_channel_id, message_id)
                SELECT run_channel_id, ? FROM tally_guild_run_channel
                WHERE guild_id = ? AND channel_id = ?
                """;

        public static final String ADD_TALLY_RUN_RUNNERS =
                """
                INSERT INTO tally_run_runner(run_id, tally_member_id)
                SELECT tr.run_id, tm.tally_member_id FROM tally_guild_member tm
                INNER JOIN tally_guild_run_channel trc ON tm.guild_id = trc.guild_id
                INNER JOIN tally_run tr ON tr.run_channel_id = trc.run_channel_id
                WHERE tm.guild_id = ? AND tm.member_id = ? AND trc.channel_id = ? AND tr.message_id = ?
                """;

        public static final String ADD_TALLY_RUN_RUNNER_REWARDS =
                """
                INSERT INTO tally_run_runner_reward
                SELECT trr.runner_id, ? FROM tally_guild_member tm
                INNER JOIN tally_guild_run_channel trc ON tm.guild_id = trc.guild_id
                INNER JOIN tally_run tr ON tr.run_channel_id = trc.run_channel_id
                INNER JOIN tally_run_runner trr ON trr.run_id = tr.run_id AND tm.tally_member_id = trr.tally_member_id
                WHERE tm.guild_id = ? AND tm.member_id = ? AND trc.channel_id = ?
                AND tr.message_id = ?
                """;

        public static final String ADD_TALLY_RUN_RUNNER_PENDING_POINTS =
                """
                INSERT INTO tally_guild_pending
                SELECT tm.tally_member_id, ? FROM tally_guild_member tm
                WHERE tm.guild_id = ? AND tm.member_id = ?
                ON CONFLICT (tally_member_id) DO
                UPDATE SET pending_points = tally_guild_pending.pending_points + excluded.pending_points
                """;

        public static final String ADD_TALLY_RUN_REACTION =
                """
                INSERT INTO tally_run_reaction
                SELECT tr.run_id, tm.tally_member_id FROM tally_guild_member tm
                INNER JOIN tally_guild_run_channel trc ON trc.guild_id = tm.guild_id
                INNER JOIN tally_run tr ON tr.run_channel_id = trc.run_channel_id
                WHERE tm.guild_id = ? AND tm.member_id = ? AND trc.channel_id = ? AND tr.message_id = ?
                ON CONFLICT DO NOTHING
                """;

        public static final String IS_TALLY_RUN_CHANNEL =
                """
                SELECT 1 FROM tally_guild_run_channel
                WHERE guild_id = ? AND channel_id = ?
                """;

        public static final String IS_TALLY_RUN_REACTED =
                """
                SELECT 1 FROM tally_guild_run_channel trc
                INNER JOIN tally_run tr ON tr.run_channel_id = trc.run_channel_id
                INNER JOIN tally_run_reaction trr on tr.run_id = trr.run_id
                WHERE trc.guild_id = ? AND trc.channel_id = ? AND tr.message_id = ?
                """;

        public static final String IS_TALLY_RUN_MODE =
                """
                SELECT 1 FROM tally_guild_run_mode
                WHERE guild_id = ? AND mode_name = ?
                """;

        public static final String GET_ACTIVE_RUN_MODE =
                """
                SELECT trm.mode_name, trmo.option_type_id, trmo.option_value FROM tally_guild tg
                INNER JOIN tally_guild_run_mode_config tgrm ON tg.guild_id = tgrm.guild_id
                INNER JOIN tally_guild_run_mode trm ON tgrm.active_mode_id = trm.mode_id
                INNER JOIN tally_guild_run_mode_option trmo ON trm.mode_id = trmo.mode_id
                WHERE tg.guild_id = ?
                """;

        public static final String GET_TALLY_RUN_CHANGES =
                """
                SELECT tgmn.member_name, awarded_points FROM tally_guild_member tm
                INNER JOIN tally_guild_member_name tgmn on tm.tally_member_id = tgmn.tally_member_id
                INNER JOIN tally_guild_run_channel trc ON tm.guild_id = trc.guild_id
                INNER JOIN tally_run tr ON tr.run_channel_id = trc.run_channel_id
                INNER JOIN tally_run_runner trr ON trr.run_id = tr.run_id AND tgmn.tally_member_id = trr.tally_member_id
                INNER JOIN tally_run_runner_reward trrr on trr.runner_id = trrr.runner_id
                WHERE tm.guild_id = ? AND trc.channel_id = ? AND tr.message_id = ?
                """;

        public static final String REMOVE_TALLY_RUN =
                """     
                DELETE FROM tally_run
                WHERE run_channel_id IN
                (
                SELECT run_channel_id FROM tally_guild_run_channel
                WHERE guild_id = ? AND channel_id = ?
                )
                AND message_id = ?
                """;

        public static final String REMOVE_ALL_TALLY_RUNS =
                """
                DELETE FROM tally_run
                WHERE run_channel_id IN
                (
                SELECT run_channel_id FROM tally_guild_run_channel
                WHERE guild_id = ?
                )
                """;

        public static final String REMOVE_TALLY_RUN_RUNNER_PENDING_POINTS =
                """
                UPDATE tally_guild_pending tp
                SET pending_points = a.new_pending_points
                FROM
                (
                SELECT tm.tally_member_id, tp.pending_points - trrr.awarded_points AS new_pending_points FROM tally_guild_member tm
                INNER JOIN tally_guild_pending tp ON tp.tally_member_id = tm.tally_member_id
                INNER JOIN tally_guild_run_channel trc ON trc.guild_id = tm.guild_id
                INNER JOIN tally_run tr ON tr.run_channel_id = trc.run_channel_id
                INNER JOIN tally_run_runner trr ON trr.run_id = tr.run_id AND trr.tally_member_id = tp.tally_member_id
                INNER JOIN tally_run_runner_reward trrr on trr.runner_id = trrr.runner_id
                WHERE tm.guild_id = ? AND trc.channel_id = ? AND tr.message_id = ?
                ) a
                WHERE tp.tally_member_id = a.tally_member_id
                """;

        public static final String REMOVE_TALLY_RUN_REACTION =
                """
                DELETE FROM tally_run_reaction
                WHERE run_id IN
                (
                SELECT tr.run_id FROM tally_guild_member tm
                INNER JOIN tally_guild_run_channel trc ON trc.guild_id = tm.guild_id
                INNER JOIN tally_run tr ON tr.run_channel_id = trc.run_channel_id
                WHERE tm.guild_id = ? AND tm.member_id = ? AND trc.channel_id = ? AND tr.message_id = ?
                )
                """;
}
