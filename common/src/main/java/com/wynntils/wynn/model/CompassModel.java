/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.wynn.model;

import com.wynntils.core.managers.Model;
import com.wynntils.mc.event.SetSpawnEvent;
import com.wynntils.mc.objects.Location;
import com.wynntils.mc.utils.McUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// TODO: Draw compass beam
public final class CompassModel extends Model {
    private static Location compassLocation = null;

    public static void init() {}

    public static Optional<Location> getCompassLocation() {
        if (compassLocation != null) compassLocation.y = McUtils.player().getY();

        return Optional.ofNullable(compassLocation);
    }

    public static void setCompassLocation(Location compassLocation) {
        CompassModel.compassLocation = compassLocation;

        if (McUtils.mc().level != null) McUtils.mc().level.setDefaultSpawnPos(compassLocation.toBlockPos(), 0);
    }

    public static void reset() {
        compassLocation = null;

        if (McUtils.mc().level != null) McUtils.mc().level.setDefaultSpawnPos(null, 0);
    }

    @SubscribeEvent
    public static void onSetSpawn(SetSpawnEvent e) {
        BlockPos spawnPos = e.getSpawnPos();

        if (McUtils.player() == null) {
            // Reset compass
            CompassModel.reset();

            if (McUtils.mc().level != null) McUtils.mc().level.setDefaultSpawnPos(spawnPos, 0);

            return;
        }

        // Cancel the event to force the compass to not change
        if (CompassModel.getCompassLocation().isPresent()) {
            e.setCanceled(true);
        }
    }
}
