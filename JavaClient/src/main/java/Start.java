import me.ImSpooks.core.client.CoreClient;
import me.ImSpooks.core.common.json.JSONConfig;
import me.ImSpooks.core.packets.collection.database.mysql.PacketRequestSqlData;
import me.ImSpooks.core.packets.collection.database.mysql.PacketRequestSqlDataResponse;
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
            client.sendAndReadPacket(new PacketRequestSqlData("*", "test_table"), PacketRequestSqlDataResponse.class, new IncomingPacket<PacketRequestSqlDataResponse>(10 * 1000) {
                @Override
                public boolean onReceive(PacketRequestSqlDataResponse packet) {
                    Logger.info("Received packet respone: " + packet);
                    return true;
                }

                @Override
                public void onExpire() {

                }
            });
        });
    }
}
