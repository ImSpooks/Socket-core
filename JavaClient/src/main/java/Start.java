import me.ImSpooks.core.client.CoreClient;
import me.ImSpooks.core.common.json.JSONConfig;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.helpers.MathHelpers;
import me.ImSpooks.core.packets.collection.database.PacketRequestCollection;
import me.ImSpooks.core.packets.collection.database.PacketRequestCollectionResponse;
import me.ImSpooks.core.packets.collection.database.PacketUpdateData;
import me.ImSpooks.core.packets.collection.other.PacketPing;
import me.ImSpooks.core.packets.collection.other.PacketPingResponse;
import me.ImSpooks.core.packets.init.IncomingPacket;
import me.ImSpooks.core.packets.security.InvalidCredentialsException;
import me.ImSpooks.core.packets.security.SecurityEncryption;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nick on 27 sep. 2019.
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
                clientDetails.getString("client_name"),
                true);


        //client.setCoreConnected(dataTester(client));

        client.setCoreConnected(benchmark(client));
        client.connect();

    }

    private static Runnable benchmark(CoreClient client) {
        return () -> {
            List<Long> toServer = new ArrayList<>();
            List<Long> toClient = new ArrayList<>();
            List<Long> total = new ArrayList<>();

            long start = System.currentTimeMillis();
            int max = 8000;


            AtomicInteger received = new AtomicInteger(0);

            new Thread(() -> {
                int last = 0;
                while (true) {
                    synchronized (received) {
                        JavaHelpers.sleep(5000);
                        if (last == received.get()) {
                            System.out.println("received = " + received.get());
                            received.set(max);
                        }
                        if (received.get() == max) {
                            System.out.println("Max reached, stopping");
                            break;
                        }
                        last = received.get();
                    }
                }
            }).start();

            for (int i = 1; i <= max; i++) {
                client.sendAndReadPacket(new PacketPing(System.currentTimeMillis()), PacketPingResponse.class, new IncomingPacket<PacketPingResponse>(TimeUnit.SECONDS.toMillis(10)) {
                    @Override
                    public boolean onReceive(PacketPingResponse packet) {
                        received.set(received.get() + 1);
                        long now = System.currentTimeMillis();

                        toServer.add(packet.getEstimatedTime());
                        toClient.add(now - packet.getServerTime());
                        total.add((packet.getEstimatedTime()) + (now - packet.getServerTime()));

                        if ((received.get() % Math.round(max / 20.0)) == 0) {
                            Logger.info("{} packets received", received.get());
                        }


                        if (received.get() == max) {
                            int decimals = 3;

                            double toServerAvg = 0;
                            for (Long aLong : toServer)
                                toServerAvg += aLong;
                            toServerAvg = toServerAvg / (double) toServer.size();

                            double toClientAvg = 0;
                            for (Long aLong : toClient)
                                toClientAvg += aLong;
                            toClientAvg = toClientAvg / (double) toClient.size();

                            double totalAvg = 0;
                            for (Long aLong : toServer)
                                totalAvg += aLong;
                            totalAvg = MathHelpers.round(totalAvg / (long) toClient.size(), decimals);

                            Logger.info("Total packets send: {} packets (Took {} s, {} packets p/s)", total.size(), MathHelpers.round((double) (System.currentTimeMillis() - start) / 1000.0, decimals), MathHelpers.round(total.size() / ((double) (System.currentTimeMillis() - start) / 1000.0), decimals));
                            Logger.info("Client to Server average: {} ms", MathHelpers.round(toServerAvg, decimals));
                            Logger.info("Server to Client average: {} ms", MathHelpers.round(toClientAvg, decimals));
                            Logger.info("Total response time average: {} ms", MathHelpers.round(totalAvg, decimals));
                        }

                        return true;
                    }

                    @Override
                    public void onExpire() {
                        received.set(received.get() + 1);
                        Logger.warn("Packet expired");
                    }
                });

                if ((i % Math.round((max / 20.0))) == 0) {
                    Logger.info("{} packets send", i);
                }
                if (i == max) {
                    Logger.info("Done sending packets");
                }
            }
        };
    }

    private static Runnable dataTester(CoreClient client) {
        return () -> {
            client.sendAndReadPacket(new PacketRequestCollection("test_table"), PacketRequestCollectionResponse.class, new IncomingPacket<PacketRequestCollectionResponse>(10 * 1000) {
                @Override
                public boolean onReceive(PacketRequestCollectionResponse packet) {
                    Logger.info("data 1:  " + Global.GSON.toJson(packet.getDocuments()));
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
        };
    }
}
