package com.padule.cospradar.util;

import android.content.Context;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AdmobUtils {

    private static final String UNIT_ID_CHAT_BOARD_LIST_FOOTER = "ca-app-pub-8640637673501328/6877757491";
    private static final String UNIT_ID_PROFILE_FOOTER = "ca-app-pub-8640637673501328/8354490692";
    private static final String UNIT_ID_CHARACTOR_CHOOSER_FOOTER = "ca-app-pub-8640637673501328/4991908290";
    private static final String UNIT_ID_SETTING_FOOTER = "ca-app-pub-8640637673501328/5652886297";

    public static void loadBanner(AdView adView, RelativeLayout view) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adView.setLayoutParams(lp);

        view.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private static AdView createAdView(String unitId, Context context) {
        AdView adView = new AdView(context);
        adView.setAdUnitId(unitId);
        adView.setAdSize(AdSize.BANNER);
        return adView;
    }

    public static AdView createAdViewInChatBoardListFooter(Context context) {
        return createAdView(UNIT_ID_CHAT_BOARD_LIST_FOOTER, context);
    }

    public static AdView createAdViewInProfileFooter(Context context) {
        return createAdView(UNIT_ID_PROFILE_FOOTER, context);
    }

    public static AdView createAdViewInCharactorChooserFooter(Context context) {
        return createAdView(UNIT_ID_CHARACTOR_CHOOSER_FOOTER, context);
    }

    public static AdView createAdViewInSettingFooter(Context context) {
        return createAdView(UNIT_ID_SETTING_FOOTER, context);
    }

}
