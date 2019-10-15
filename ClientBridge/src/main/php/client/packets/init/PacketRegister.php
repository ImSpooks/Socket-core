<?php
/**
 * Created by Nick on 15 okt. 2019.
 * Copyright © ImSpooks
 */

class PacketRegister {
    private static array $REGISTERED_PACKETS = [];
    private static array $REGISTERED_IDS = [];
    private static array $PACKET_TYPES = [];

    private const MAX_PACKETS = 65536;

    public static function init() {
        //network
        self::register(3, PacketResponseExpired::getClassName(), PacketType::NETWORK);

        // database
        self::register(1, PacketRequestData::getClassName(), PacketType::DATABASE);
        self::register(2, PacketRequestDataResponse::getClassName(), PacketType::DATABASE);
        self::register(3, PacketRequestCollection::getClassName(), PacketType::DATABASE);
        self::register(4, PacketRequestCollectionResponse::getClassName(), PacketType::DATABASE);
        self::register(5, PacketUpdateData::getClassName(), PacketType::DATABASE);

        //other

    }

    private static function register(int $id, string $packet, array $packetType) {
        $id = $packetType["START_ID"] + $id - 1;

        if (array_key_exists($id, self::$REGISTERED_PACKETS)) {
            throw new InvalidArgumentException(sprintf("Packet with ID %s already registered for type %s", $id - $packetType["START_ID"] + 1, $packetType["NAME"]));
        }
        if (array_key_exists($packet, self::$REGISTERED_IDS)) {
            throw new InvalidArgumentException(sprintf("Packet %s already registered.", $packet));
        }
        if (count(self::$REGISTERED_PACKETS) > self::MAX_PACKETS) {
            throw new InvalidArgumentException(sprintf("Cannot register packet \'%s\': The maximum packet limit (%s) has been reached", $packet, self::MAX_PACKETS));
        }

        array_push(self::$REGISTERED_PACKETS, $id, $packet);
        array_push(self::$REGISTERED_IDS, $packet, $id);
        array_push(self::$PACKET_TYPES, $id, $packetType);
    }

    public static function createInstance(int $id): Packet {
        if ($id < 0 || $id > self::MAX_PACKETS)
            throw new InvalidArgumentException("Illegal id range " . $id);

        if (!array_key_exists($id, self::$REGISTERED_PACKETS))
            throw new InvalidArgumentException("Unknowk packet ID " . $id);

        $classname = self::$REGISTERED_PACKETS[$id];

        return new $classname();
    }

    public static function getId(Packet $packet): int {
        if ($packet == null)
            throw new InvalidArgumentException("Packet may not be null");

        if (!array_key_exists(get_class($packet), self::$REGISTERED_IDS))
            throw new InvalidArgumentException("Unknown packet ID " . get_class($packet));

        $id = self::$REGISTERED_IDS[get_class($packet)];

        if ($id < 0 || $id > self::MAX_PACKETS)
            throw new AssertionError("Packet id had impossible value " . $id);

        return $id;
    }

    public static function getPacketType(int $id): array {
        if ($id < 0 || $id > self::MAX_PACKETS)
            throw new InvalidArgumentException("Illegal id range " . $id);
        if (!array_key_exists($id, self::$PACKET_TYPES))
            throw new InvalidArgumentException("Invalid packet ID " . $id);
        return self::$PACKET_TYPES[$id];
    }

    public static function getPacketName(int $id): string {
        if ($id < 0 || $id > self::MAX_PACKETS)
            throw new InvalidArgumentException("Illegal id range " . $id);
        if (!array_key_exists($id, self::$REGISTERED_PACKETS))
            throw new InvalidArgumentException("Invalid packet ID " . $id);
        return self::$REGISTERED_PACKETS[$id];
    }

    public static function getPackets(): array {
        return array_values(self::$REGISTERED_IDS);
    }

    /*

        public static Class<? extends Packet> getPacketFromClassName(String className) throws ClassNotFoundException {
        for (Class<? extends Packet> packet : getPackets()) {
            if (packet.getSimpleName().equalsIgnoreCase(className)) {
                return packet;
            }
        }
            throw new ClassNotFoundException("No registered packets with name \'" + className + "\' found");
        }

        public static List<Class<? extends Packet>> getPackets() {
            return new ArrayList<>(REGISTERED_IDS.keySet());
        }*/
}