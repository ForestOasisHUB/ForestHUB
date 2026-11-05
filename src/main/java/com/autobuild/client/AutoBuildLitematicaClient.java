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
    private String currentMissingItem = "stone"; // Mẫu vật phẩm test

    @Override
    public void onInitializeClient() {
        // Phím mở GUI: Shift phải (Right Shift)
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autobuild.menu",
                InputUtil.Type.KEY_SYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.autobuild.general"
        ));

        // Phím N: Tăng Layer thủ công nếu cần
        nextLayerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autobuild.nextlayer",
                InputUtil.Type.KEY_SYM,
                GLFW.GLFW_KEY_N,
                "category.autobuild.general"
        ));

        // Xử lý sự kiện nhấn phím trong mỗi tick game
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Bật/tắt GUI khi bấm Shift phải
            while (menuKey.wasPressed()) {
                isGuiOpen = !isGuiOpen;
                client.player.sendMessage(Text.literal("§a[ForestAutoBuild] GUI: " + (isGuiOpen ? "MỞ" : "ĐÓNG")), true);
            }

            // Tăng Layer khi bấm phím N
            while (nextLayerKey.wasPressed()) {
                currentLayer++;
                client.player.sendMessage(Text.literal("§e[ForestAutoBuild] Chuyển sang Layer: §b" + currentLayer), true);
            }

            // Vòng lặp tính năng tự động xây & mua đồ /ah
            if (isAutoBuildActive && client.interactionManager != null) {
                tickDelay++;
                if (tickDelay >= 5) { // Nhịp 0.25 giây / hành động
                    tickDelay = 0;

                    boolean hasBlock = checkInventoryForRequiredBlock();
                    if (!hasBlock) {
                        if (!currentMissingItem.isEmpty()) {
                            client.player.sendMessage(Text.literal("§c[ForestAutoBuild] Thiếu block! Đang /ah mua: " + currentMissingItem), true);
                            client.player.networkHandler.sendChatCommand("ah " + currentMissingItem);
                            buyCheapestItemFromAH(client);
                        }
                    } else {
                        executeLayerBuildLogic(client);
                    }
                }
            }
        });

        // Vẽ bảng GUI màu xanh lá lên màn hình khi mở
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!isGuiOpen) return;

            int screenWidth = drawContext.getScaledWindowWidth();
            int screenHeight = drawContext.getScaledWindowHeight();

            // Tọa độ khung GUI (Góc trên bên trái)
            int x1 = 30;
            int y1 = 30;
            int x2 = 230;
            int y2 = 150;

            // Vẽ khung nền màu xanh lá đậm (Mã màu ARGB: Đậm nét phong cách Forest)
            drawContext.fill(x1, y1, x2, y2, 0xC00A3A18); // Nền xanh lá cây tối pha đen mờ
            drawContext.fill(x1, y1, x2, y1 + 20, 0xFF0F5132); // Thanh tiêu đề màu xanh lá sáng hơn

            // Hiển thị tiêu đề và thông tin cấu hình trong GUI
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "§lForestAutoBuild Pro", x1 + 8, y1 + 6, 0xFFFFFF, true);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "Trạng thái: " + (isAutoBuildActive ? "§aĐang chạy" : "§cĐang dừng"), x1 + 10, y1 + 32, 0xFFFFFF, false);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "Layer hiện tại: §eLayer " + currentLayer, x1 + 10, y1 + 50, 0xFFFFFF, false);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "Tính năng: SafeWalk & Auto /ah", x1 + 10, y1 + 68, 0x55FF55, false);
            drawContext.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "§7[Shift Phải]: Đóng/Mở GUI", x1 + 10, y1 + 95, 0xAAAAAA, false);
        });
    }

    private boolean checkInventoryForRequiredBlock() {
        return true; // Giả lập luôn có block
    }

    private void buyCheapestItemFromAH(net.minecraft.client.MinecraftClient client) {
        // Logic tự động quét danh sách /ah và chọn món rẻ nhất
    }

    private void executeLayerBuildLogic(net.minecraft.client.MinecraftClient client) {
        // Logic chống té (SafeWalk) và đặt block theo tầng Litematica
        if (client.player.isOnGround()) {
            // Giữ chân chống rơi rớt mép vực
        }
    }
}
