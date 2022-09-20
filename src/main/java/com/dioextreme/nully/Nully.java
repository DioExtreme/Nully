package com.dioextreme.nully;

import com.dioextreme.nully.command.SlashCommandListener;
import com.dioextreme.nully.env.Env;
import com.dioextreme.nully.module.tally.TallyMessageListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Nully
{
    public static void main(String[] args)
    {
        if (!Env.hasEnvVariables())
        {
            return;
        }

        String botToken = Env.getEnvOrDockerSecret(Env.tokenEnvVariable, "nully_bot_token");

        // Use 2 shards at most for now
        String shardIdEnvVariable = System.getenv(Env.shardIdEnvVariable);
        int shardIdToUse = Integer.parseInt(shardIdEnvVariable);

        if (shardIdToUse != 0 && shardIdToUse != 1)
        {
            System.err.println("Shard ID should be either 0 or 1");
            return;
        }

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createLight(botToken)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS)
                .setShardsTotal(2)
                .setShards(shardIdToUse);

        SlashCommandListener slashCommandListener = new SlashCommandListener();
        TallyMessageListener tallyMessageListener = new TallyMessageListener();

        builder.addEventListeners(slashCommandListener, tallyMessageListener);
        builder.build();
    }
}
