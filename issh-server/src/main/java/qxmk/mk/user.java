package qxmk.mk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class user {
    private final String USERNAME;
    private final Socket socket;

    public user(String USERNAME, Socket socket) {
        this.USERNAME = USERNAME;
        this.socket = socket;

    }

    public String getIP() {
        return socket.getInetAddress().getHostAddress();
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public DataInputStream getDataInputStream() throws IOException {
        return new DataInputStream(socket.getInputStream());
    }

    public DataOutputStream getDataOutputStream() throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    public Socket getSocket() {
        return socket;
    }
}
