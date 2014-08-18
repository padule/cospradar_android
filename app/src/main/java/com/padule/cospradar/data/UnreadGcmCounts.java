package com.padule.cospradar.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.padule.cospradar.base.Data;
import com.padule.cospradar.event.UnreadChatBoardCountChangedEvent;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.NotificationUtils;
import com.padule.cospradar.util.PrefUtils;

import de.greenrobot.event.EventBus;

public class UnreadGcmCounts extends Data {

    private static final long serialVersionUID = 1L;
    private static final String KEY_SPLIT_STR = "_";
    private static final String KEY_PREFIX = "unreadcounts" + KEY_SPLIT_STR;
    private static final String PREF_KEY_UNREAD_GCM_COUNTS = "unread_gcm_counts";

    private Map<String, Integer> counts;

    private UnreadGcmCounts() {
        this.counts = new HashMap<String, Integer>();
    }

    public static UnreadGcmCounts getInstance() {
        UnreadGcmCounts counts = (UnreadGcmCounts)
                UnreadGcmCounts.deSerializeFromString(PrefUtils.get(PREF_KEY_UNREAD_GCM_COUNTS, null));
        if (counts == null) {
            counts = new UnreadGcmCounts();
            PrefUtils.put(PREF_KEY_UNREAD_GCM_COUNTS, counts.serializeToString());
        }

        return counts;
    }

    public void putCount(int notificationId, int modelId, int userId) {
        if ((userId == 0 || AppUtils.isLoginUser(userId)) 
                && NotificationUtils.relateOnGcm(notificationId)) {
            String key = generateKey(notificationId, modelId);
            Integer count = counts.get(key) != null ? counts.get(key) : 1;
            counts.put(key, count);
            PrefUtils.put(PREF_KEY_UNREAD_GCM_COUNTS, serializeToString());
            EventBus.getDefault().post(new UnreadChatBoardCountChangedEvent());
        }
    }

    private String generateKey(int notificationId, int modelId) {
        return KEY_PREFIX + notificationId + KEY_SPLIT_STR + modelId;
    }

    private int getNotificationIdFromKey(String key) {
        return getIdFromKey(key, 1);
    }

    private int getModelIdFromKey(String key) {
        return getIdFromKey(key, 2);
    }

    private int getIdFromKey(String key, int index) {
        try {
            return Integer.valueOf(key.split(KEY_SPLIT_STR, 0)[index]);
        } catch(Exception e) {
            return -1;
        }
    }

    public Map<Integer, Integer> getChatBoardMap() {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (Entry<String, Integer> e : counts.entrySet()) {
            int notificationId = getNotificationIdFromKey(e.getKey());
            if (NotificationUtils.relateOnChatBoard(notificationId)) {
                int modelId = getModelIdFromKey(e.getKey());
                result.put(modelId, modelId);
            }
        }

        return result;
    }

    public void clearFromChatBoard(int modelId) {
        int[] idRelateOnChatBoard = NotificationUtils.getIdRelateOnChatBoard();
        for (int notificationId : idRelateOnChatBoard) {
            clear(notificationId, modelId);
        }
    }

    public void clearInvalidChatBoard(List<Integer> validCharactorIds) {
        Map<Integer, Integer> map = getChatBoardMap();

        for (int charactorId : validCharactorIds) {
            map.remove(charactorId);
        }

        for (Entry<Integer, Integer> e : map.entrySet()) {
            clearFromChatBoard(e.getKey());
        }
    }

    private void clear(int notificationId, int modelId) {
        String key = generateKey(notificationId, modelId);
        counts.remove(key);
        PrefUtils.put(PREF_KEY_UNREAD_GCM_COUNTS, serializeToString());
        EventBus.getDefault().post(new UnreadChatBoardCountChangedEvent());
    }

}
