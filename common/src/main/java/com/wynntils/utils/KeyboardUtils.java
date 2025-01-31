/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.utils;

import com.wynntils.mc.utils.McUtils;
import org.lwjgl.glfw.GLFW;

public class KeyboardUtils {
    public static boolean isKeyDown(int keyCode) {
        return GLFW.glfwGetKey(McUtils.mc().getWindow().getWindow(), keyCode) == 1;
    }
}
