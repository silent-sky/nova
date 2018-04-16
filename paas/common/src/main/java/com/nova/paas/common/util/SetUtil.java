package com.nova.paas.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Set;

public class SetUtil {

    public static void removeNull(Set set) {
        if (set != null) {
            set.remove(null);
        }
    }

    public static void removeBlankElement(Set<String> set) {
        if (set == null) {
            return;
        }
        Iterator<String> iter = set.iterator();
        while (iter.hasNext()) {
            if (StringUtils.isBlank(iter.next())) {
                iter.remove();
            }
        }
    }
}
