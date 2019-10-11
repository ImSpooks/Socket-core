import me.ImSpooks.core.client.CoreClient;
import me.ImSpooks.core.common.json.JSONConfig;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.collection.database.PacketRequestCollection;
import me.ImSpooks.core.packets.collection.database.PacketRequestCollectionResponse;
import me.ImSpooks.core.packets.collection.database.PacketUpdateData;
import me.ImSpooks.core.packets.init.IncomingPacket;
import me.ImSpooks.core.packets.security.InvalidCredentialsException;
import me.ImSpooks.core.packets.security.SecurityEncryption;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;
import org.tinylog.Logger;

/**
 * Created by Nick on 27 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class Start {

    public static void main(String[] args) {
        JSONConfig clientDetails = new JSONConfig("config", "credentials.json");

        clientDetails.expect("server_ip", "127.0.0.1");
        clientDetails.expect("server_port", 7000);
        clientDetails.expect("client_name", "defaultClient");

        for (String d : new String[]{"password", "encryption_key", "init_vector"}) {
            clientDetails.expect(d, "INSERT");
        }

        try {
            SharedEncryption.setEncryption(SecurityEncryption.newInstance(
                    clientDetails.getString("encryption_key"),
                    clientDetails.getString("init_vector")));
        } catch (InvalidCredentialsException e) {
            Logger.error(e);
        }

        CoreClient client = new CoreClient(
                clientDetails.getString("server_ip"),
                clientDetails.getInt("server_port"),
                clientDetails.getString("password"),
                clientDetails.getString("client_name"));
        client.connect();


        client.setCoreConnected(() -> {
            client.sendAndReadPacket(new PacketRequestCollection("test_table"), PacketRequestCollectionResponse.class, new IncomingPacket<PacketRequestCollectionResponse>(10 * 1000) {
                @Override
                public boolean onReceive(PacketRequestCollectionResponse packet) {
                    return true;
                }

                @Override
                public void onExpire() {
                    Logger.info("Packet listener expired");
                }
            });

            client.sendPacket(new PacketUpdateData("test_table", "id", 1, "test_val", "test"));

            client.sendAndReadPacket(new PacketRequestCollection("test_table"), PacketRequestCollectionResponse.class, new IncomingPacket<PacketRequestCollectionResponse>(10 * 1000) {
                @Override
                public boolean onReceive(PacketRequestCollectionResponse packet) {
                    Logger.info("data 2:  " + Global.GSON.toJson(packet.getDocuments()));
                    return true;
                }

                @Override
                public void onExpire() {
                    Logger.info("Packet listener expired");
                }
            });

            client.sendPacket(new PacketUpdateData("test_table", "id", 1, "test_val", null));

            client.sendAndReadPacket(new PacketRequestCollection("test_table"), PacketRequestCollectionResponse.class, new IncomingPacket<PacketRequestCollectionResponse>(10 * 1000) {
                @Override
                public boolean onReceive(PacketRequestCollectionResponse packet) {
                    Logger.info("data 3:  " + Global.GSON.toJson(packet.getDocuments()));
                    return true;
                }

                @Override
                public void onExpire() {
                    Logger.info("Packet listener expired");
                }
            });

            client.sendPacket(new PacketUpdateData("test_table", "id", 1, "test_val", 4.0F));

            client.sendAndReadPacket(new PacketRequestCollection("test_table"), PacketRequestCollectionResponse.class, new IncomingPacket<PacketRequestCollectionResponse>(10 * 1000) {
                @Override
                public boolean onReceive(PacketRequestCollectionResponse packet) {
                    Logger.info("data 4:  " + Global.GSON.toJson(packet.getDocuments()));
                    return true;
                }

                @Override
                public void onExpire() {
                    Logger.info("Packet listener expired");
                }
            });

        });
    }
}
