package me.abem.njit;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFooterData;
import discord4j.discordjson.json.EmbedImageData;
import discord4j.discordjson.json.EmbedThumbnailData;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Bot {
    private static final Map<String, Command> commands = new HashMap<>();
    private static GatewayDiscordClient client;

    static {
        commands.put("announce", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then());
    }

    public static void main(String[] args) throws IOException {
        client = DiscordClientBuilder.create("**TOKEN**")
                .build()
                .login()
                .block();
        assert client != null;
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });

        InstaScraper.start();

        System.out.println("done");
        client.onDisconnect().block();
    }

    public static void sendMessageToChannel(String id, Account a, Media m) {
        String user = a.getFullName().isBlank() ? a.getUsername() : a.getFullName();
        Objects.requireNonNull(client.getChannelById(Snowflake.of(id)).block()).getRestChannel().createMessage(EmbedData.builder()
                .image(EmbedImageData.builder().url(m.getDisplayUrl()).build())
                .description(m.getCaption())
                .title(user + " (@" + a.getUsername() + ")")
                .footer(EmbedFooterData.builder().text(m.getCreated().toString()).build())
                .thumbnail(EmbedThumbnailData.builder().url(a.getProfilePicUrl()).build())
                .build())
                .block();
    }

}
