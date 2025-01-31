/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.functions;

import com.google.common.base.CaseFormat;
import com.wynntils.core.features.Translatable;
import java.util.List;
import net.minecraft.client.resources.language.I18n;

public abstract class Function<T> implements Translatable {
    public abstract T getValue(String argument);

    public String getName() {
        String name = this.getClass().getSimpleName().replace("Function", "");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    public List<String> getAliases() {
        return List.of();
    }

    public String getTranslatedName() {
        return getTranslation("name");
    }

    public String getDescription() {
        return getTranslation("description");
    }

    private String getTranslationKeyName() {
        String name = this.getClass().getSimpleName().replace("Function", "");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
    }

    public String getTranslation(String keySuffix) {
        return I18n.get("function.wynntils." + getTranslationKeyName() + "." + keySuffix);
    }
}
