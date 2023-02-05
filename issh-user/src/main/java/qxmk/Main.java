package qxmk;

import qxmk.mk.YamlUtil;
import qxmk.mk.talk.listen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.Integer.parseInt;
import static qxmk.ejz.toejz;
import static qxmk.mk.input.input;
import static qxmk.mk.key.*;

public class Main {

    public static void main(String[] args) throws IOException {
        //读取配置信息
        String ADDRESS = YamlUtil.getValueByKey("config.yaml", "user", "address");
        String PORT = YamlUtil.getValueByKey("config.yaml", "user", "port");
        String USERNAME = YamlUtil.getValueByKey("config.yaml", "user", "username");
        //尝试连接
        try {
            Socket socket = new Socket(ADDRESS, parseInt(PORT));
            System.out.println("------开始自检------\n------尝试连接到" + ADDRESS + "------");
            //初始化输入输出
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //接收服务端用户名
            byte[] buf = new byte[50];
            int r = dataInputStream.read(buf);
            System.out.println("连接到：" + new String(buf, 0, r));
            //发送客户端用户名
            dataOutputStream.write(USERNAME.getBytes());
            //设定加密密匙
            ckey();
            System.out.print("输入密匙 >>>");
            wkey(input());
            new Thread(new listen(dataInputStream, socket)).start();
            while (true) {
                try {
                    String sa = input();
                    dataOutputStream.write(toejz(USERNAME + "qxmk" + sa, rkey()).getBytes());
                    //dataOutputStream.write((USERNAME + "qxmk" + sa).getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}