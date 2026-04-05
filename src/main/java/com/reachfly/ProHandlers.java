package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class ProHandlers {

    private static int antiAfkTimer = 0;
    private static int chatSpamTimer = 0;
    private static int announcerTimer = 0;
    private static String lastAction = "";

    public static void tick(MinecraftClient client) {
        if (!ModConfig.proUnlocked) return;
        if (client.player == null) return;

        tickAntiAfk(client);
        tickChatSpam(client);
        tickAnnouncer(client);
    }

    private static void tickAntiAfk(MinecraftClient client) {
        if (!ModConfig.antiAfkEnabled) { antiAfkTimer = 0; return; }
        antiAfkTimer++;
        if (antiAfkTimer >= ModConfig.antiAfkInterval) {
            antiAfkTimer = 0;
            ClientPlayerEntity p = client.player;
            if (p != null) {
                float yaw = p.getYaw() + (float)(Math.random() * 10 - 5);
                p.setYaw(yaw);
                if (p.isOnGround()) {
                    p.jump();
                }
            }
        }
    }

    private static void tickChatSpam(MinecraftClient client) {
        if (!ModConfig.chatSpamEnabled) { chatSpamTimer = 0; return; }
        chatSpamTimer++;
        if (chatSpamTimer >= ModConfig.chatSpamDelay) {
            chatSpamTimer = 0;
            if (client.player != null && ModConfig.chatSpamMessage != null && !ModConfig.chatSpamMessage.isEmpty()) {
                client.player.networkHandler.sendChatMessage(ModConfig.chatSpamMessage);
            }
        }
    }

    private static void tickAnnouncer(MinecraftClient client) {
        if (!ModConfig.announcerEnabled) return;
        announcerTimer++;
        if (announcerTimer >= 100) {
            announcerTimer = 0;
            ClientPlayerEntity p = client.player;
            if (p == null) return;
            String action = "";
            if (p.isSprinting()) action = "sprinting";
            else if (p.isSneaking()) action = "sneaking";
            else if (p.isSwimming()) action = "swimming";
            if (!action.isEmpty() && !action.equals(lastAction)) {
                lastAction = action;
                p.networkHandler.sendChatMessage("[OSP] Currently " + action);
            }
        }
    }
}
