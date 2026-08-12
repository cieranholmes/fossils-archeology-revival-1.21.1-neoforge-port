package com.github.fossils.util;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.neoforged.fml.ModList;

import java.util.HashSet;
import java.util.Set;

public class Version {
    private static final Set<ReleaseType> RELEASE_TYPES = ReleaseType.parseVersion(getVersion());

    public static String getVersion() {
        return ModList.get().getModContainerById(FossilsAndArcheologyRevival.MODID)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("");
    }

    public static boolean isAlpha() {
        return RELEASE_TYPES.contains(ReleaseType.ALPHA);
    }

    public static boolean debugEnabled() {
        return RELEASE_TYPES.contains(ReleaseType.DEVELOP);
    }

    public enum ReleaseType {
        ALPHA, DEVELOP, RELEASE_CANDIDATE;

        public static Set<ReleaseType> parseVersion(String version) {
            Set<ReleaseType> set = new HashSet<>();
            if (version.contains("-develop")) {
                set.add(DEVELOP);
            }
            if (version.contains("-rc")) {
                set.add(RELEASE_CANDIDATE);
            }
            if (version.contains("-alpha")) {
                set.add(ALPHA);
            }
            return set;
        }
    }
}
