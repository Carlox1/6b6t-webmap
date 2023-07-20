package com.crlx;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class CommandHandler {
    public static boolean webmap = true;
    public static void register(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(ClientCommandManager.literal("webmap").executes(ctx -> {
            webmap = !webmap;
            if (webmap) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Webmap: §aon"));
            } else {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Webmap: §4off"));
            }
            return 1;
        }));
    }
}
