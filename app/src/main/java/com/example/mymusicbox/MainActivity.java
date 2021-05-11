package com.example.mymusicbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    //控件
    ImageButton previous,next,play,stop;
    TextView song,singer;

    ActivityReceiver activityReceiver;

    public static final String CTL_ACTION =
            "org.bq.action.CTL_ACTION";
    public static final String UPDATE_ACTION =
            "org.bq.action.UPDATE_ACTION";

    //定义一个表示状态的变量，0x11代表没有播放；0x12代表正在播放；0x13代表暂停
    int status = 0x11;
    //定义两个列表，一个放歌名，一个放歌手名字
    String[] Songs = new String[]{"大雨还在下","难渡","大风吹","我害怕鬼"};
    String[] Singers = new String[]{"1个球","等什么君","王赫野","段兴华"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //控件绑定
        previous = (ImageButton)this.findViewById(R.id.previous);
        next = (ImageButton) this.findViewById(R.id.next);
        play = (ImageButton)this.findViewById(R.id.play);
        stop = (ImageButton)this.findViewById(R.id.stop);
        song = (TextView) this.findViewById(R.id.song);
        singer = (TextView) this.findViewById(R.id.singer);

        //添加监听
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);

        activityReceiver = new ActivityReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(UPDATE_ACTION);
        // 注册BroadcastReceiver
        registerReceiver(activityReceiver, filter);

        Intent intent = new Intent(this, MusicService.class);
        // 启动后台Service
        startService(intent);

    }

    //自定义的BroadcastReceiver，负责监听从Service传回来的广播
    public class ActivityReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收广播的数据,updata表示播放器的状态，current表示正在播放的音乐
            int update = intent.getIntExtra("update",-1);
            int current = intent.getIntExtra("current",-1);

            if (current >= 0)
            {
                song.setText(Songs[current]);
                singer.setText(Singers[current]);
            }

            switch (update)
            {

                case 0x11:
                    play.setImageResource(R.drawable.play);
                    status = 0x11;
                    break;
                // 控制系统进入播放状态
                case 0x12:
                    // 播放状态下设置使用暂停图标
                    play.setImageResource(R.drawable.pause);
                    // 设置当前状态
                    status = 0x12;
                    break;
                // 控制系统进入暂停状态
                case 0x13:
                    // 暂停状态下设置使用播放图标
                    play.setImageResource(R.drawable.play);
                    // 设置当前状态
                    status = 0x13;
                    break;
            }

        }
    }

    @Override
    public void onClick(View source) {
        Intent intent = new Intent("org.bq.action.CTL_ACTION");
        switch (source.getId())
        {
            // 按下播放/暂停按钮
            case R.id.play:
                intent.putExtra("control", 1);
                break;
            // 按下停止按钮
            case R.id.stop:
                intent.putExtra("control", 2);
                break;
            case R.id.previous:
                intent.putExtra("control", 3);
                break;
            case R.id.next:
                intent.putExtra("control", 4);
                break;
        }
        // 发送广播，将被Service组件中的BroadcastReceiver接收到
        sendBroadcast(intent);

    }

}