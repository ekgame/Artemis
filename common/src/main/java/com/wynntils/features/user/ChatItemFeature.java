/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.features.user;

import com.google.common.collect.ImmutableList;
import com.wynntils.core.features.UserFeature;
import com.wynntils.core.managers.Model;
import com.wynntils.mc.event.ChatPacketReceivedEvent;
import com.wynntils.mc.event.KeyInputEvent;
import com.wynntils.mc.mixin.accessors.ChatScreenAccessor;
import com.wynntils.mc.utils.McUtils;
import com.wynntils.wynn.model.ChatItemModel;
import com.wynntils.wynn.utils.WynnUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ChatItemFeature extends UserFeature {
    private final Map<String, String> chatItems = new HashMap<>();

    @Override
    protected void onInit(
            ImmutableList.Builder<Condition> conditions, ImmutableList.Builder<Class<? extends Model>> dependencies) {
        conditions.add(new WebLoadedCondition());
    }

    @SubscribeEvent
    public void onKeyTyped(KeyInputEvent e) {
        if (!WynnUtils.onWorld()) return;
        if (!(McUtils.mc().screen instanceof ChatScreen chatScreen)) return;

        EditBox chatInput = ((ChatScreenAccessor) chatScreen).getChatInput();

        if (!chatItems.isEmpty() && (e.getKey() == GLFW.GLFW_KEY_ENTER || e.getKey() == GLFW.GLFW_KEY_KP_ENTER)) {
            // replace the placeholder strings with the actual encoded strings
            for (Map.Entry<String, String> item : chatItems.entrySet()) {
                chatInput.setValue(chatInput.getValue().replace("<" + item.getKey() + ">", item.getValue()));
            }
            chatItems.clear();
            return;
        }

        // replace encoded strings with placeholders for less confusion
        Matcher m = ChatItemModel.chatItemMatcher(chatInput.getValue());
        while (m.find()) {
            String encodedItem = m.group();
            StringBuilder name = new StringBuilder(m.group("Name"));
            while (chatItems.containsKey(name.toString())) { // avoid overwriting entries
                name.append("_");
            }

            chatInput.setValue(chatInput.getValue().replace(encodedItem, "<" + name + ">"));
            chatItems.put(name.toString(), encodedItem);
        }
    }

    @SubscribeEvent
    public void onChatReceived(ChatPacketReceivedEvent e) {
        if (!WynnUtils.onWorld()) return;

        Component message = e.getMessage();
        if (!ChatItemModel.chatItemMatcher(message.getString()).find()) return; // no chat items to replace

        e.setMessage(ChatItemModel.insertItemComponents(message));
    }
}
