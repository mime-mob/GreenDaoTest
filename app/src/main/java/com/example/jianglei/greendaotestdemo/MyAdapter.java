package com.example.jianglei.greendaotestdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jianglei.greendaotestdemo.db.bean.User;

import java.util.List;

/**
 * Created by jianglei on 2016/6/30.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<User> users;
    private LayoutInflater inflater;

    public MyAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.text_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(users.get(position).getId() + " : " + users.get(position).getName() + "  " + users.get(position).getAge() + "岁");
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
