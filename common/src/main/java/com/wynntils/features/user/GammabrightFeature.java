/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.features.user;

import com.wynntils.core.features.UserFeature;
import com.wynntils.core.features.properties.FeatureInfo;
import com.wynntils.core.features.properties.FeatureInfo.Stability;
import com.wynntils.core.features.properties.RegisterKeyBind;
import com.wynntils.core.keybinds.KeyBind;
import com.wynntils.mc.utils.McUtils;
import org.lwjgl.glfw.GLFW;

@FeatureInfo(stability = Stability.INVARIABLE)
public class GammabrightFeature extends UserFeature {
    private double lastGamma = 1f;

    @RegisterKeyBind
    private final KeyBind gammabrightKeyBind =
            new KeyBind("Gammabright", GLFW.GLFW_KEY_G, true, this::onGammabrightKeyPress);

    private void onGammabrightKeyPress() {
        double currentGamma = McUtils.options().gamma;
        if (currentGamma < 1000) {
            lastGamma = currentGamma;
            McUtils.options().gamma = 1000d;
            return;
        }

        McUtils.options().gamma = lastGamma;
    }
}
