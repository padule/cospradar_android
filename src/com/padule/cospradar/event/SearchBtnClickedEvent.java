package com.padule.cospradar.event;

public class SearchBtnClickedEvent {

    public final String searchText;
    public final boolean isRealtime;

    public SearchBtnClickedEvent(String searchText, boolean isRealtime) {
        this.searchText = searchText;
        this.isRealtime = isRealtime;
    }

}
