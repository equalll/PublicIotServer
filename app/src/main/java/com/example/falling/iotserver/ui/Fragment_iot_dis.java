package com.example.falling.iotserver.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.falling.iotserver.R;
import com.example.falling.iotserver.sokcet.SocketServer;

import java.io.IOException;

/**
 * Created by falling on 2015/11/30.
 */
//显示的fragment
public class Fragment_iot_dis extends Fragment {
    private static Fragment_iot_dis iot_dis;
    private static SocketServer server;
    public Switch swiPort;//socket开关
    private EditText edtPort;//输入端口号
    private TextView disID;
    private TextView disHeart;
    private TextView disRGB;
    private TextView disIR;
    private TextView disTemp;
    private TextView disHum;

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            // TODO 接收消息并且去更新UI线程上的控件内容
            if(msg.what==0){
                Bundle b=msg.getData();
                Instance().disID.setText(b.getString("id"));
                Instance().disHeart.setText(b.getString("heartbeat"));
                Instance().disRGB.setText(b.getString("RGB"));

                if(b.getString("IR").equals("1")){
                    Instance().disIR.setText("开");
                }
                else{
                    Instance().disIR.setText("关");
                }

                Instance().disTemp.setText(b.getString("temp"));
                Instance().disHum.setText(b.getString("hum"));
            }
            super.handleMessage(msg);
        }
    };

    public static Fragment_iot_dis Instance() {
        if (iot_dis == null)
            iot_dis = new Fragment_iot_dis();
        return iot_dis;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_iot_dis, container, false);
        disID = (TextView) rootView.findViewById(R.id.disID);
        edtPort = (EditText) rootView.findViewById(R.id.port);
        swiPort = (Switch) rootView.findViewById(R.id.swiPort);

        disHeart = (TextView) rootView.findViewById(R.id.disHeart);
        disRGB =(TextView) rootView.findViewById(R.id.disRGB);
        disIR = (TextView) rootView.findViewById(R.id.disIR);
        disTemp = (TextView) rootView.findViewById(R.id.disTemp);
        disHum = (TextView) rootView.findViewById(R.id.disHum);

        // socket的开关
        swiPort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swiPort.isChecked()) {
                    try {
                        //开启socket服务
                        int port = Integer.parseInt(edtPort.getText().toString());
                        server = new SocketServer(port);
                        server.start();
                        SocketServer.isContinue = true;
                        Toast.makeText(getContext(), "Socket 服务开启，端口号为:" + port, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        //端口号输入不是整数
                        swiPort.setChecked(false);
                        Toast.makeText(getContext(), "输入有效的端口号！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        //关闭socket服务
                        if (!SocketServer.sst.isClosed()) {
                            SocketServer.sst.close();//关闭Server socket
                            SocketServer.isContinue = false; //接入的socket不在继续执行
                            Toast.makeText(getContext(), "Socket 服务关闭", Toast.LENGTH_SHORT).show();
                            System.out.println("Server close");
                            //关闭服务后。清除显示的数据
                            disHeart.setText("");
                            disHum.setText("");
                            disID.setText("");
                            disIR.setText("");
                            disRGB.setText("");
                            disTemp.setText("");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return rootView;
    }
}
