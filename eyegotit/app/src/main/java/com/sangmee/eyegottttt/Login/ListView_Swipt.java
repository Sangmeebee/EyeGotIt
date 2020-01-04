package com.sangmee.eyegottttt.Login;

import android.graphics.drawable.Drawable;

public class ListView_Swipt {
    Drawable iconDrawable;
    String titleStr;

    public void settIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void settTitle(String title) {
        titleStr = title ;
    }

    public Drawable gettIcon() {
        return this.iconDrawable ;
    }
    public String gettTitle() {
        return this.titleStr ;
    }

}
