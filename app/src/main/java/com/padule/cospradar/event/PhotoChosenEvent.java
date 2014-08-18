package com.padule.cospradar.event;

import android.net.Uri;

public class PhotoChosenEvent {

    public Uri uri;

    public PhotoChosenEvent(Uri uri) {
        this.uri = uri;
    }

}
