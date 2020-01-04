package com.sangmee.eyegottttt.Login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sangmee.eyegottttt.R;
import com.sangmee.eyegottttt.checkbox_listview.ListViewItem;

import java.util.ArrayList;

public class ListViewAdapterSwipt extends BaseAdapter {
    private ArrayList<ListView_Swipt> listViewItemList = new ArrayList<ListView_Swipt>() ;

    public ListViewAdapterSwipt() {

    }
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return listViewItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_swiptbar, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.list_imageView) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.list_textView) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListView_Swipt listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.gettIcon());
        titleTextView.setText(listViewItem.gettTitle());

        return convertView;
    }

    public void addItem(Drawable icon, String title) {
        ListView_Swipt item = new ListView_Swipt();

        item.settIcon(icon);
        item.settTitle(title);

        listViewItemList.add(item);
    }
}
