package com.dioextreme.nully.module.tally.setup.menu.main;

import com.dioextreme.nully.NullyExecutor;
import com.dioextreme.nully.command.GuildCommandType;
import com.dioextreme.nully.command.SlashCommandRegistrar;
import com.dioextreme.nully.discord.interaction.menu.AbstractMenu;
import com.dioextreme.nully.discord.interaction.menu.MenuListener;
import com.dioextreme.nully.module.tally.data.GuildDataHandler;
import com.dioextreme.nully.module.tally.entity.SetupConfig;
import com.dioextreme.nully.module.tally.setup.menu.TallyMenuInteraction;
import com.dioextreme.nully.utils.TextUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TallyInitMenu extends AbstractMenu
{
    private final SetupConfig setupConfig = new SetupConfig();

    public TallyInitMenu(MenuListener menuListener)
    {
        super(menuListener);

        addInteraction(TallyMenuInteraction.INIT);
        addInteraction(TallyMenuInteraction.RUN_CHANNEL_SELECT);
        addInteraction(TallyMenuInteraction.LOG_CHANNEL_SELECT);
        addInteraction(TallyMenuInteraction.CONFIRM_INIT);

        addInteraction(TallyMenuInteraction.EXIT);
    }

    private SelectMenu getChannelSelectMenu(@Nonnull GenericComponentInteractionCreateEvent event,
                                                  @Nonnull String id, @Nonnull String placeholder)
    {
        Guild guild = Objects.requireNonNull(event.getGuild());
        var channelCache = guild.getTextChannelCache();

        SelectMenu.Builder selectMenuBuilder = SelectMenu.create(id)
                .setPlaceholder(placeholder)
                .setRequiredRange(1,1);

        channelCache.forEach(channel -> selectMenuBuilder.addOption(channel.getName(), channel.getId()));

        return selectMenuBuilder.build();
    }

    private void showChannelSelect(@Nonnull GenericComponentInteractionCreateEvent event, int type)
    {
        String channelType;

        switch (type)
        {
            case TallyMenuInteraction.RUN_CHANNEL_SELECT -> channelType = "run";
            case TallyMenuInteraction.LOG_CHANNEL_SELECT -> channelType = "log";
            default -> throw new IllegalArgumentException("Unknown channel type " + type);
        }

        MessageEditData menuData = interactionBuilder
                .create(event)
                .withEmbedField("Tally Setup", "Select a " + TextUtils.getBold(channelType + " channel") + ".", false)
                .withSelectMenu(getChannelSelectMenu(event, uniqueIdOf(type), "Select a " + channelType + " channel"))
                .withButtons(Button.danger("tally_exit", "Exit"))
                .buildAsEditData();

        event.editMessage(menuData).queue();
    }

    private void showInitConfirm(@Nonnull SelectMenuInteractionEvent event)
    {
        Guild guild = Objects.requireNonNull(event.getGuild());

        long runChannelId = Long.parseUnsignedLong(setupConfig.getRunChannel().getValue());
        TextChannel runChannel = Objects.requireNonNull(guild.getTextChannelById(runChannelId));
        String runChannelMention = runChannel.getAsMention();

        long logChannelId = Long.parseUnsignedLong(setupConfig.getLogChannel().getValue());
        TextChannel logChannel = Objects.requireNonNull(guild.getTextChannelById(logChannelId));
        String logChannelMention = logChannel.getAsMention();

        StringBuilder sb = new StringBuilder();
        sb.append("You **selected** the following:\n\n")
                .append("**Run channel**: ").append(runChannelMention).append("\n")
                .append("**Log channel**: ").append(logChannelMention).append("\n\n")
                .append("Complete setup with the above options?");

        MessageEditData menuData = interactionBuilder
                .create(event)
                .withEmbedField("Tally Setup", sb.toString(), false)
                .withButtons(
                        Button.success(uniqueIdOf(TallyMenuInteraction.CONFIRM_INIT), "Complete Setup"),
                        Button.danger(uniqueIdOf(TallyMenuInteraction.EXIT), "Exit")
                ).buildAsEditData();

        event.editMessage(menuData).queue();
    }

    @Override
    public void show(@Nonnull SlashCommandInteractionEvent event)
    {
        MessageCreateData messageData = interactionBuilder.create(event)
                .withEmbedField("Tally Setup", "The tally module is disabled for this guild. Initialize?", false)
                .withButtons(
                        Button.primary(uniqueIdOf(TallyMenuInteraction.INIT), "Initialize"),
                        Button.danger(uniqueIdOf(TallyMenuInteraction.EXIT), "Exit")
                )
                .buildAsCreateData();

        event.reply(messageData).queue();
    }

    @Override
    public void fromButtonInteraction(@Nonnull ButtonInteractionEvent event)
    {
        if (isInteraction(event, TallyMenuInteraction.INIT))
        {
            showChannelSelect(event, TallyMenuInteraction.RUN_CHANNEL_SELECT);
        }
        else if (isInteraction(event, TallyMenuInteraction.CONFIRM_INIT))
        {
            GuildDataHandler dataHandler = new GuildDataHandler();
            NullyExecutor.execute(() ->
            {
                dataHandler.addTallyGuild(event, setupConfig);
                SlashCommandRegistrar commandRegistrar = new SlashCommandRegistrar();
                Guild guild = Objects.requireNonNull(event.getGuild());
                commandRegistrar.registerCommandsOfType(GuildCommandType.TALLY, guild);
            });
            menuListener.close(event);
        }
    }

    @Override
    public void fromSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event)
    {
        SelectOption channelSelected = event.getSelectedOptions().get(0);

        if (isInteraction(event, TallyMenuInteraction.RUN_CHANNEL_SELECT))
        {
            setupConfig.setRunChannel(channelSelected);
            showChannelSelect(event, TallyMenuInteraction.LOG_CHANNEL_SELECT);
        }
        else if (isInteraction(event, TallyMenuInteraction.LOG_CHANNEL_SELECT))
        {
            setupConfig.setLogChannel(channelSelected);
            showInitConfirm(event);
        }
    }
}