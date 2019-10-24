<?php
/**
 * Created by Nick on 24 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\other;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketPing extends Packet {

    /** @var int */ 	private $clientTime;

    public function __construct(int $clientTime = 0) {
        $this->clientTime = $clientTime;
    }

    public function receive(WrappedInputStream $in) {
        $this->clientTime = $in->readLong();
    }

    public function send(WrappedOutputStream $out) {
        $out->writeLong($this->clientTime);
    }

    public function getClientTime(): int {
        return $this->clientTime;
    }

}
