<?php
/**
 * Created by Nick on 15 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\init;

use AssertionError;
use client\packets\collection\other\PacketPing;
use client\packets\collection\other\PacketPingResponse;
use client\packets\type\PacketType;

use client\packets\collection\database\PacketRequestCollection;
use client\packets\collection\database\PacketRequestCollectionResponse;
use client\packets\collection\database\PacketRequestData;
use client\packets\collection\database\PacketRequestDataResponse;
use client\packets\collection\database\PacketUpdateData;
use client\packets\collection\other\PacketResponseExpired;
use InvalidArgumentException;
use stdClass;

class PacketRegister {
    // TODO convert to 7.4 if released
    /** @var array */ private static $REGISTERED_PACKETS = [];
    /** @var array */ private static $REGISTERED_IDS = [];
    /** @var array */ private static $PACKET_TYPES = [];

    private const MAX_PACKETS = 65536;

    public static function init() {
        //network
        self::register(3, PacketResponseExpired::class, PacketType::NETWORK);

        // database
        self::register(1, PacketRequestData::class, PacketType::DATABASE);
        self::register(2, PacketRequestDataResponse::class, PacketType::DATABASE);
        self::register(3, PacketRequestCollection::class, PacketType::DATABASE);
        self::register(4, PacketRequestCollectionResponse::class, PacketType::DATABASE);
        self::register(5, PacketUpdateData::class, PacketType::DATABASE);

        //other
        self::register(1, PacketPing::class, PacketType::OTHER);
        self::register(2, PacketPingResponse::class, PacketType::OTHER);
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

        self::$REGISTERED_PACKETS[$id] = $packet;
        self::$REGISTERED_IDS[$packet] = $id;
        self::$PACKET_TYPES[$id] = $packetType;
    }

    public static function createInstance(int $id): Packet {
        if ($id < 0 || $id > self::MAX_PACKETS)
            throw new InvalidArgumentException("Illegal id range " . $id);

        if (!array_key_exists($id, self::$REGISTERED_PACKETS))
            throw new InvalidArgumentException("Unknowk packet ID " . $id);

        $classname = self::$REGISTERED_PACKETS[$id];

        return new $classname();
    }

    public static function getId(string $packet): int {
        if ($packet == null)
            throw new InvalidArgumentException("Packet may not be null");

        if (!array_key_exists($packet, self::$REGISTERED_IDS))
            throw new InvalidArgumentException("Unknown packet ID " . $packet);

        $id = self::$REGISTERED_IDS[$packet];

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
}