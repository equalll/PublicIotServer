package com.example.falling.iotserver.sokcet;

import android.os.Bundle;
import android.os.Message;

import com.example.falling.iotserver.data.receive.ReceiveIOTData;
import com.example.falling.iotserver.ui.Fragment_iot_con;
import com.example.falling.iotserver.ui.Fragment_iot_dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by falling on 2015/12/19.
 */
public class dealSocket extends Thread {
    private static final int UPDATE = 0;
    ReceiveIOTData d ;
    byte[] bytes = new byte[56];
    Socket s = null;
    InputStream is;
    DataInputStream in;
    DataOutputStream out;
    OutputStream os;

    public dealSocket(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        deal();
    }

    private void deal() {

        try {
            while (SocketServer.isContinue) {
                is = s.getInputStream();
                os = s.getOutputStream();
                out = new DataOutputStream(os);
                in = new DataInputStream(is);
                in.read(bytes);
                d = new ReceiveIOTData(bytes);
                int flag = d.starDealDate();
                if (flag == 1) {
                    System.out.println("数据无效!");
                } else if (flag == 2) {
                    System.out.println("起始标记错误!");
                } else {
                    //显示数据
                    disIot();
                }

                //发送控制数据
                out.write(Fragment_iot_con.Instance().LoadData());
                out.flush();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                is.close();
                in.close();
                s.close();
                os.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    private void disIot() {
        Message msg = new Message();
        msg.what = UPDATE;
        Bundle b = new Bundle();
        b.putString("id",String.valueOf(d.getID()&0xFF));
        b.putString("heartbeat",String.valueOf(d.getHearbeat()&0xFF));

        b.putString("RGB",String.valueOf(d.getSensor_data_frame2().getV1()+" "+d.getSensor_data_frame2().getV2()+" "+d.getSensor_data_frame2().getV3()));
        b.putString("IR",String.valueOf(d.getSensor_data_frame3().getV1()));
        b.putString("temp",String.valueOf(d.getSensor_data_frame1().getV1()));
        b.putString("hum",String.valueOf(d.getSensor_data_frame1().getV2()));

        msg.setData(b);

        Fragment_iot_dis.handler.sendMessage(msg);
    }
}
