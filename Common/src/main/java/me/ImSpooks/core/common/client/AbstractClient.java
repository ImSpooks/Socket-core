package me.ImSpooks.core.common.client;

import com.google.common.base.Ascii;
import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.common.interfaces.IClient;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import org.tinylog.Logger;

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
            byte[] buffer = new byte[length > 0 ? length : 128];
            in.read(buffer);
            String decoded = new String(buffer);

            // Got corrupted data, splitting each packet with Form Feed
            for (byte splitter : new byte[] {Ascii.FF, Ascii.DC2}) {
                decoded = decoded.replace(
                        Character.toString((char) splitter),
                        "\n"
                );
            }

            if (decoded.contains("\n")) {
                String[] split = decoded.split("\n");

                for (String s : split) {
                    this.handlePacket(Packet.deserialize(s.trim()));
                }
                return;
            }

            this.handlePacket(Packet.deserialize(decoded));
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    @Override
    public void write(Packet packet) {
        try {
            byte[] serialized = packet.serialize(new WrappedOutputStream());
            this.out.writeInt(serialized.length);
            this.out.write(serialized);

            // Sleaping 10 milisecond to prevent data from being sent too fast that can cause corruption
            JavaHelpers.sleep(10);
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
