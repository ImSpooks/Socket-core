package me.ImSpooks.core.common.client;

import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.common.interfaces.IClient;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public abstract class AbstractClient implements IClient {
    @Getter @Setter protected String clientName = "";

    @Getter protected Socket socket;

    @Getter protected DataInputStream in;
    protected DataOutputStream out;

    @Getter protected boolean started = false;

    @Override
    public void handleClient() {
        try {
            if (this.in.available() == 0) {
                JavaHelpers.sleep(50);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            int length = in.readInt();
            byte[] buffer = new byte[length];
            in.read(buffer);
            try {
                this.handlePacket(Packet.deserialize(buffer));
            } catch (IOException e) {
                throw e;
            } catch (JsonParseException e) {
                throw e;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Packet packet) {
        try {
            byte[] serialized = packet.serialize(new WrappedOutputStream());
            this.out.writeInt(serialized.length);
            this.out.write(serialized);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            started = false;
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected boolean isConnected() throws SocketDisconnectedException {
        try {
            return socket.getInputStream().read() == -1;
        } catch (IOException e) {
            throw new SocketDisconnectedException(String.format("Client \'%s\' has disconnected (Crash?)", this.clientName));
        }
    }
}
