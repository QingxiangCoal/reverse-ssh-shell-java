package qxmk.mk.talk;

import java.io.DataOutputStream;

public class sa implements Runnable {
    private DataOutputStream dataOutputStream;
    private byte[] fs;

    public sa(DataOutputStream dataOutputStream, byte[] fs) {
        this.dataOutputStream = dataOutputStream;
        this.fs = fs;
    }

    @Override
    public void run() {
        try {
            dataOutputStream.write(fs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
