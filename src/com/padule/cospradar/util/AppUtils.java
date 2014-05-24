package com.padule.cospradar.util;

import com.padule.cospradar.data.Charactor;

public class AppUtils {

    public static void setCharactor(Charactor charactor) {
        String str = charactor != null ? charactor.serializeToString() : null;
        PrefUtils.put(Charactor.class.getName(), str);
    }

    public static Charactor getCharactor() {
        Charactor charactor = (Charactor)Charactor.deSerializeFromString(PrefUtils.get(Charactor.class.getName(), null));
        if (charactor != null && !charactor.isEnabled()) {
            setCharactor(null);
            charactor = null;
        }
        return charactor;
    }

}
