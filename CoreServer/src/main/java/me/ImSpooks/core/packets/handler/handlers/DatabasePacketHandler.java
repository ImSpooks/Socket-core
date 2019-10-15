package me.ImSpooks.core.packets.handler.handlers;

import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.common.database.DataKey;
import me.ImSpooks.core.packets.collection.database.*;
import me.ImSpooks.core.packets.handler.PacketHandler;
import me.ImSpooks.core.packets.handler.PacketHandling;
import me.ImSpooks.core.packets.handler.SubPacketHandler;
import org.bson.Document;
import org.tinylog.Logger;

import java.util.List;

/**
 * Created by Nick on 01 okt. 2019.
 * Copyright © ImSpooks
 */
public class DatabasePacketHandler extends SubPacketHandler {

    public DatabasePacketHandler(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @PacketHandling
    private void handlePacket(PacketRequestCollection packet, AbstractClient client) {
        try {
            List<Document> documents = this.packetHandler.getCoreServer().getDatabase().getCollection(packet.getCollection());
            client.write(new PacketRequestCollectionResponse(documents));
        } catch (Exception e) {
            Logger.error(e, "Something went wrong while requesting data with packet {}", packet);
        }
    }

    @PacketHandling
    private void handlePacket(PacketRequestData packet, AbstractClient client) {
        try {
            Document documents = this.packetHandler.getCoreServer().getDatabase().getData(packet.getCollection(), new DataKey(packet.getKey(), packet.getValue()));
            client.write(new PacketRequestDataResponse(documents));
        } catch (Exception e) {
            Logger.error(e, "Something went wrong while requesting collection with packet {}", packet);
        }
    }

    @PacketHandling
    private void handlePacket(PacketUpdateData packet, AbstractClient client) {
        try {
            this.packetHandler.getCoreServer().getDatabase().update(packet.getCollection(), new DataKey(packet.getKey(), packet.getKeyValue()), packet.getColumn(), packet.getValue());
        } catch (Exception e) {
            Logger.error(e, "Something went wrong while updating data with packet {}", packet);
        }
    }
}
