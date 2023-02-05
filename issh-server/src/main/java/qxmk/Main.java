package qxmk;

import qxmk.mk.talk.sa;
import qxmk.mk.user;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;
import static qxmk.Main.users;
import static qxmk.ejz.ejzto;
import static qxmk.ejz.toejz;
import static qxmk.mk.YamlUtil.getValueByKey;
import static qxmk.mk.input.input;
import static qxmk.mk.key.*;

public class Main {

    public static user[] users = new user[0];

    public static void main(String[] args) throws IOException {

        //设定加密密匙
        ckey();
        System.out.print("输入密匙 >>>");
        wkey(input());
        //读取配置信息
        String PORT = getValueByKey("config.yaml", "server", "port");
        String USERNAME = getValueByKey("config.yaml", "server", "username");
        //开始监听
        try (ServerSocket serverSocket = new ServerSocket(parseInt(PORT))) {
            //开启输出线程
            new Thread(new say(USERNAME)).start();
            while (true) {
                Socket socket = serverSocket.accept();
                //注册
                System.out.println("------开始自检------");
                //检测黑名单
                if (Arrays.asList(getValueByKey("config.yaml", "server", "blacklist").split("\\,")).contains(socket.getInetAddress().getHostAddress())) {
                    socket.close();
                    System.out.println("已拦截黑名单ip：" + socket.getInetAddress().getHostAddress());
                } else {
                    //发送服务端用户名
                    new DataOutputStream(socket.getOutputStream()).write(USERNAME.getBytes());
                    //接收客户端用户名
                    byte[] buf = new byte[50];
                    int r = new DataInputStream(socket.getInputStream()).read(buf);
                    String uname = new String(buf, 0, r);
                    System.out.println("客户端加入：" + uname);
                    //写入客户端信息
                    user[] ls = new user[users.length + 1];
                    System.arraycopy(users, 0, ls, 0, users.length);
                    users = ls;
                    int use = users.length - 1;
                    users[use] = new user(uname, socket);
                    //开启监听线程
                    new Thread(new lis(users, use)).start();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class say implements Runnable {

    private final String USERNAME;

    public say(String USERNAME) throws InterruptedException {
        sleep(1);
        this.USERNAME = USERNAME;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String in = input();
                //用"qxmk"分隔用户名和消息
                byte[] fs = toejz(USERNAME + "qxmk" + in, rkey()).getBytes();
                //byte[] fs = (USERNAME + "qxmk" + in).getBytes();
                //多线程发送给每个客户端
                for (qxmk.mk.user user : users) {
                    new Thread(new sa(user.getDataOutputStream(), fs)).start();
                }
                //命令行反馈
                System.out.println("{" + USERNAME + " : " + in + "}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class lis implements Runnable {
    private final user[] userss;
    private int use;

    public lis(user[] userss, int use) {
        this.userss = userss;
        this.use = use;
    }

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        while (true) {
            if (userss.length > users.length) {
                use--;
            }
            try {
                int r = users[use].getDataInputStream().read(buf);
                if (r != -1) {
                    new Thread(new li(users, buf, r)).start();
                }
            } catch (Exception e) {
                try {
                    users[use].getSocket().close();
                    break;
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class li implements Runnable {

    private final byte[] buf;
    private final int r;

    public li(user[] userss, byte[] buf, int r) {
        users = userss;
        this.buf = buf;
        this.r = r;
    }

    @Override
    public void run() {
        String nr = new String(buf, 0, r);
        //转发给每个客户端
        for (int i = 0; i < users.length; i++) {
            try {
                users[i].getDataOutputStream().write(nr.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                //客户端注销
                System.out.println("客户端" + users[i].getUSERNAME() + "/" + users[i].getIP() + "退出");
                user[] ls = new user[users.length - 1];
                for (int ii = 0; ii < users.length; ii++) {
                    if (ii < i) {
                        ls[ii] = users[ii];
                    } else {
                        ls[ii] = users[ii + 1];
                    }
                }
                users = ls;
                break;
            }
        }
        //命令行反馈
        try {
            String[] fh = ejzto(nr, rkey()).split("qxmk");
            //String[] fh = new String(buf, 0, r).split("qxmk");
            System.out.println("{" + fh[0] + " : " + fh[1] + "}");
            //System.out.println(fh[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}