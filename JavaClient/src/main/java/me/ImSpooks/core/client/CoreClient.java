package me.ImSpooks.core.client;

import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.client.commands.CommandStop;
import me.ImSpooks.core.client.init.Client;
import me.ImSpooks.core.common.commands.CommandManager;
import me.ImSpooks.core.common.interfaces.CoreImplementation;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.packets.init.IncomingPacket;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.PacketReceiver;

/**
 * Created by Nick on 26 sep. 2019.
 * Copyright © ImSpooks
 */
public class CoreClient implements CoreImplementation {

    @Getter private final long verificationId = Global.RANDOM.nextLong();

    @Getter private PacketReceiver packetReceiver;
    @Getter private Client client;

    @Getter private final String ip;
    @Getter private final int port;
    @Getter private final String password;
    @Getter private final String clientName;

    @Getter private CommandManager commandManager;

    @Getter @Setter private Runnable coreConnected = () -> {};

    public CoreClient(String ip, int port, String password, String clientName, boolean commandLine) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.clientName = clientName;
        this.packetReceiver = new PacketReceiver();

        this.client = new Client(this.ip, this.port, this.clientName, this);

        new Thread(() -> {
            while (true) {
                JavaHelpers.sleep(1000);
                this.packetReceiver.removeExpired();
            }
        }, "Expired Packet Handler");

        if (commandLine) {
            this.commandManager = new CommandManager(System.in, this);
            this.commandManager.registerCommand(new CommandStop(this));
        }
    }

    public void connect() {
        this.client.connect();
    }

    public void sendPacket(Packet packet) {
        this.client.write(packet);
    }

    public void sendAndReadPacket(Packet packet, Class<? extends Packet> responseClass, IncomingPacket<? extends Packet> response) {
        this.packetReceiver.addListener(responseClass, response);
        this.sendPacket(packet);
    }

    @Override
    public void stop() {
        this.shutdownHook().run();
        System.exit(0);
    }

    @Override
    public Runnable shutdownHook() {
        return () -> {
            System.out.println("Shutting down client.");

            this.client.close();
            if (this.commandManager != null) this.commandManager.stop();
        };
    }
}
