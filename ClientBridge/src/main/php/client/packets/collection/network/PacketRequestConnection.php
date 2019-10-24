<?php
/**
 * Created by Nick on 21 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\network;


use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;
use client\packets\init\Packet;
use client\packets\security\shared\SharedEncryption;
use RuntimeException;

class PacketRequestConnection extends Packet {

    /** @var string */ private $password;
    /** @var int */ private $randomKey;
    /** @var string */ private $clientName;
    /** @var int */ private $clientType;

    public function __construct(string $password = "", int $randomKey = 0, string $clientName = "", int $clientType = 0) {
        $this->password = $password;
        $this->randomKey = $randomKey;
        $this->clientName = $clientName;
        $this->clientType = $clientType;
    }

    public function send(WrappedOutputStream $out) {
        if (SharedEncryption::getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        $encrypted = SharedEncryption::getEncryption()->encrypt(json_encode([$this->password, $this->randomKey]));
        $out->writeString($encrypted);
        $out->writeString($this->clientName);
        $out->writeInt($this->clientType);
    }

    public function receive(WrappedInputStream $in) {
        if (SharedEncryption::getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        $result = json_decode(SharedEncryption::getEncryption()->decrypt($in->readString()));
        var_dump($result);

        $this->password = strval($result[0]);
        $this->randomKey = intval($result[1]);

        $this->clientName = $in->readString();
        $this->clientType = $in->readInt();
    }

    public function getPassword(): string {
        return $this->password;
    }

    public function getRandomKey(): int {
        return $this->randomKey;
    }

    public function getClientName(): string {
        return $this->clientName;
    }

    public function getClientType(): int {
        return $this->clientType;
    }


}