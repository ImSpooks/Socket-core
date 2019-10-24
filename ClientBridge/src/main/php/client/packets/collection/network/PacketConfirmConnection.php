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

class PacketConfirmConnection extends Packet {

    /** @var int */ private $randomKey;

    public function send(WrappedOutputStream $out) {
        if (SharedEncryption::getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        $encrypted = SharedEncryption::getEncryption()->encrypt(sprintf("[%s]", $this->randomKey));
        $out->writeString($encrypted);
    }

    public function receive(WrappedInputStream $in) {
        if (SharedEncryption::getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        $encrypted = json_decode(SharedEncryption::getEncryption()->decrypt($in->readString()));
        $this->randomKey = $encrypted[0];
    }

    public function getRandomKey(): int {
        return $this->randomKey;
    }
}