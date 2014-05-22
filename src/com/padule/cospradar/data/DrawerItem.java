package com.padule.cospradar.data;

public class DrawerItem {
    private String title;
    private int iconResId;
    private String fragmentPackage;

    public DrawerItem(String title, int iconResId, String fragmentPackage) {
        this.title = title;
        this.iconResId = iconResId;
        this.fragmentPackage = fragmentPackage;
    }

    public String getTitle() {
        return this.title;
    }

    public int getIconResId() {
        return this.iconResId;
    }

    public String getFragmentPackage() {
        return this.fragmentPackage;
    }
}
