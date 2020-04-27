/*package com.github.pmlakner.leaguemealone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpConnection {

    public void openConnection (ConnectionInfo connectionInfo){

    }




    private class TcpConnectionThread implements Runnable {
        private final Socket socket;
        private final DataInputStream dis;
        private final DataOutputStream dos;

        private TcpConnectionThread(Socket socket, DataInputStream dis, DataOutputStream dos) {
            this.socket = socket;
            this.dis = dis;
            this.dos = dos;
        }

        @Override
        public void run() {
            String received;
            String sent;
            while (true) {
                try {
                    received = dis.readUTF();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}*/
