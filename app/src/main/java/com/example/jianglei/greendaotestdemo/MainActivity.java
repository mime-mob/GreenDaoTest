package com.example.jianglei.greendaotestdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.jianglei.greendaotestdemo.db.GreenDaoHelper;
import com.example.jianglei.greendaotestdemo.db.bean.User;
import com.example.jianglei.greendaotestdemo.db.dao.UserDao;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private ListView listView;
    private MyAdapter adapter;
    private List<User> users;

    private int updateAge = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.insert);
        button2 = (Button) findViewById(R.id.query);
        button3 = (Button) findViewById(R.id.update);
        button4 = (Button) findViewById(R.id.delete);
        button5 = (Button) findViewById(R.id.query_where);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);

        users = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new MyAdapter(this, users);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert:
                insertUsers();
                queryUsers();
                break;
            case R.id.query:
                queryUsers();
                break;
            case R.id.update:
                updateUser();
                queryUsers();
                break;
            case R.id.delete:
                deleteUsers();
                queryUsers();
                break;
            case R.id.query_where:
                queryWhereUser();
                break;
            default:
                break;
        }
    }

    private void insertUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User u = new User();
            u.setName("江磊" + i);
            u.setAge(i);
            users.add(u);
        }
        GreenDaoHelper.getInstance().multiInsert(users);
    }

    private void deleteUsers() {
        GreenDaoHelper.getInstance().deleteAll(User.class);
    }

    private void updateUser() {
        List<User> users = new ArrayList<>();
        User u = new User();
        u.setId(1l);
        u.setName("江磊更新1");
        u.setAge(updateAge);
        users.add(u);
        User u1 = new User();
        u1.setId(3l);
        u1.setName("江磊更新2");
        u1.setAge(updateAge);
        users.add(u1);
        GreenDaoHelper.getInstance().multiUpdate(users);
        updateAge++;
    }

    private void queryUsers() {
        users.clear();
        users = GreenDaoHelper.getInstance().queryAll(User.class);
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
    }

    private void queryWhereUser() {
        users.clear();
        users = GreenDaoHelper.getInstance().queryWithFilter(User.class, UserDao.Properties.Age.eq(2));
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GreenDaoHelper.getInstance().closeGreenDao();
    }
}
