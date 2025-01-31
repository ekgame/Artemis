/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.keybinds;

import com.google.common.collect.Lists;
import com.wynntils.core.managers.CoreManager;
import com.wynntils.mc.event.ClientTickEvent;
import com.wynntils.mc.event.InventoryKeyPressEvent;
import com.wynntils.mc.event.InventoryMouseClickedEvent;
import com.wynntils.mc.mixin.accessors.OptionsAccessor;
import com.wynntils.mc.utils.McUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Registers and handles keybinds */
public final class KeyBindManager extends CoreManager {
    private static final Set<KeyBind> KEY_BINDS = ConcurrentHashMap.newKeySet();
    private static final Object optionsLock = new Object();

    /** Needed for all Models */
    public static void init() {}

    @SubscribeEvent
    public static void onTick(ClientTickEvent.End e) {
        triggerKeybinds();
    }

    @SubscribeEvent
    public static void onKeyPress(InventoryKeyPressEvent e) {
        KEY_BINDS.forEach(keyBind -> {
            if (keyBind.getKeyMapping().matches(e.getKeyCode(), e.getScanCode())) {
                keyBind.onInventoryPress(e.getHoveredSlot());
            }
        });
    }

    @SubscribeEvent
    public static void onMousePress(InventoryMouseClickedEvent e) {
        KEY_BINDS.forEach(keyBind -> {
            if (keyBind.getKeyMapping().matchesMouse(e.getButton())) {
                keyBind.onInventoryPress(e.getHoveredSlot());
            }
        });
    }

    public static void registerKeybind(KeyBind toAdd) {
        if (hasName(toAdd.getName())) {
            throw new IllegalStateException(
                    "Can not add keybind " + toAdd.getName() + " since the name already exists");
        }

        Options options = McUtils.options();

        assert options != null;

        KEY_BINDS.add(toAdd);

        synchronized (optionsLock) {
            KeyMapping[] keyMappings = options.keyMappings;

            List<KeyMapping> newKeyMappings = Lists.newArrayList(keyMappings);
            newKeyMappings.add(toAdd.getKeyMapping());

            ((OptionsAccessor) options).setKeyBindMixins(newKeyMappings.toArray(new KeyMapping[0]));
        }
    }

    public static void unregisterKeybind(KeyBind toAdd) {
        Options options = McUtils.options();

        assert options != null;

        if (KEY_BINDS.remove(toAdd)) {
            synchronized (optionsLock) {
                KeyMapping[] keyMappings = options.keyMappings;

                List<KeyMapping> newKeyMappings = Lists.newArrayList(keyMappings);
                newKeyMappings.remove(toAdd.getKeyMapping());

                ((OptionsAccessor) options).setKeyBindMixins(newKeyMappings.toArray(new KeyMapping[0]));
            }
        }
    }

    private static void triggerKeybinds() {
        KEY_BINDS.forEach(keyBind -> {
            if (keyBind.isFirstPress()) {
                if (keyBind.getKeyMapping().consumeClick()) {
                    keyBind.onPress();
                }

                while (keyBind.getKeyMapping().consumeClick()) {
                    // do nothing
                }

                return;
            }

            if (keyBind.getKeyMapping().isDown()) {
                keyBind.onPress();
            }
        });
    }

    private static boolean hasName(String name) {
        return KEY_BINDS.stream().anyMatch(k -> k.getName().equals(name));
    }

    public static void initKeyMapping(String category, Map<String, Integer> categorySortOrder) {
        if (categorySortOrder.containsKey(category)) return;

        int max = 0;

        for (int val : categorySortOrder.values()) {
            if (val > max) {
                max = val;
            }
        }

        categorySortOrder.put(category, max + 1);
    }

    public static void loadKeybindConfigFile() {
        Options options = McUtils.options();

        assert options != null;

        options.load();
    }
}
