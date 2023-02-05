package qxmk.mk.talk;

import java.io.DataInputStream;
import java.net.Socket;

import static qxmk.ejz.ejzto;
import static qxmk.mk.key.rkey;

public class listen implements Runnable {
    private final DataInputStream dataInputStream;
    private final Socket socket;

    public listen(DataInputStream dataInputStream, Socket socket) {
        this.dataInputStream = dataInputStream;
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        while (true) {
            try {
                int r = dataInputStream.read(buf);
                String[] fh = new String(ejzto(new String(buf, 0, r), rkey())).split("qxmk");
                //String[] fh = new String(buf, 0, r).split("qxmk");
                System.out.println(fh[0] + " : " + fh[1]);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("服务端已退出");
                System.exit(1);
            }
        }
    }
}
