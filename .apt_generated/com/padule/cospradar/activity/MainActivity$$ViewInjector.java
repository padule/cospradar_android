// Generated code from Butter Knife. Do not modify!
package com.padule.cospradar.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.padule.cospradar.activity.MainActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131099710);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131099710' for field 'mDrawerList' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mDrawerList = (android.widget.ListView) view;
    view = finder.findById(source, 2131099708);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131099708' for field 'mDrawerLayout' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mDrawerLayout = (android.support.v4.widget.DrawerLayout) view;
  }

  public static void reset(com.padule.cospradar.activity.MainActivity target) {
    target.mDrawerList = null;
    target.mDrawerLayout = null;
  }
}
