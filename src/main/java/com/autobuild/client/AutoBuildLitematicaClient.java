package com.autobuild.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AutoBuildLitematicaClient implements ClientModInitializer {
    public static KeyBinding menuKey;
    public static KeyBinding nextLayerKey;
    
    public static boolean isGuiOpen = false;
    public static boolean isAutoBuildActive = false;
    public static int currentLayer = 1;

    private int tickDelay = 0;
    private String currentMissingItem = "stone";

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autobuild.menu",
                InputUtil.Type.KEY_SYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.autobuild.general"
        ));

        nextLayerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autobuild.nextlayer",
                InputUtil.Type.KEY_SYM,
                GLFW.GLFW_KEY_N,
                "category.autobuild.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (menuKey.wasPressed()) {
                isGuiOpen = !isGuiOpen;
                client.player.sendMessage(Text.literal("§a[ForestAutoBuild] GUI: " + (isGuiOpen ? "MỞ" : "ĐÓNG")), true);
            }

            while (nextLayerKey.wasPressed()) {
                currentLayer++;
                client.player.sendMessage(Text.literal("§e[ForestAutoBuild] Chuyển sang Layer: §b" + currentLayer), true);
            }

            if (isAutoBuildActive && client.interactionManager != null) {
                tickDelay++;
                if (tickDelay >= 5) {
                    tickDelay = 0;
                    boolean hasBlock = checkInventoryForRequiredBlock();
                    if (!hasBlock) {
                        if (!currentMissingItem.isEmpty()) {
                            client.player.sendMessage(Text.literal("§c[ForestAutoBuild] Thiếu block! Đang /ah mua: " + currentMissingItem), true);
                            client.player.networkHandler.sendChatCommand("ah " + currentMissingItem);
                        }
                    } else {
                        executeLayerBuildLogic(client);
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!isGuiOpen) return;
            int x1 = 30, y1 = 30, x2 = 230, y2 = 150;
            drawContext.fill(x1, y1, x2, y2, 0xC00A3A18);
            drawContext.fill(x1, y1, x2, y1 + 20, 0xFF0F5132);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "§lForestAutoBuild Pro", x1 + 8, y1 + 6, 0xFFFFFF, true);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "Trạng thái: " + (isAutoBuildActive ? "§aĐang chạy" : "§cĐang dừng"), x1 + 10, y1 + 32, 0xFFFFFF, false);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "Layer hiện tại: §eLayer " + currentLayer, x1 + 10, y1 + 50, 0xFFFFFF, false);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "Tính năng: SafeWalk & Auto /ah", x1 + 10, y1 + 68, 0x55FF55, false);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "§7[Shift Phải]: Đóng/Mở GUI", x1 + 10, y1 + 95, 0xAAAAAA, false);
        });
    }

    private boolean checkInventoryForRequiredBlock() { return true; }
    private void executeLayerBuildLogic(net.minecraft.client.MinecraftClient client) {}
}
