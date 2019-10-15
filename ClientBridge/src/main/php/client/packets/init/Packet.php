<?php
/**
 * Created by Nick on 14 okt. 2019.
 * Copyright © ImSpooks
 */

abstract class Packet {

    public function serialize(): string {
        $out = new WrappedOutputStream();

        $out->write(1);
        $out->writeInt($this->getId());
        $this->send($out);

        return pack(json_encode($out));
    }

    public static function deserialize(string $input): Packet {
        $packetId = -1;
        try {
            $input = trim($input);

            if (!StringUtils::startsWith($input, "[")) $input = "[" . $input;

            $data = json_decode($input);
            $in = new WrappedInputStream($data);

            $in->skip();
            $packetId = $in->readInt();
            $packet = PacketRegister::createInstance($packetId);
            $packet->receive($in);

            return $packet;
        } catch (Exception $e) {
            echo sprintf("Something went wrong while deserializing packet %s with input \'%s\'", $packetId != -1 ? PacketRegister::getPacketName($packetId) : "\"unknown\"", $input);
        }
        return null;
    }


    public abstract function send(WrappedOutputStream $out);
    public abstract function receive(WrappedInputStream $in);

    private $id = -1;
    public function getId(): int {
        if ($this->id == -1) {
            $this->id = PacketRegister::getId($this);
        }
        return $this->id;
    }

    public static function getClassName(): string {
        return get_class();
    }

    public function __toString(): string {
        $json = json_encode($this);
        return get_class($this) . "[" . substr($json, 1, strlen($json) - 1) . "]";
    }
}