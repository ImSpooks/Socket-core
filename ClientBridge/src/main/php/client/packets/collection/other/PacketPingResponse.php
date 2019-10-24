<?php
/**
 * Created by Nick on 24 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\other;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketPingResponse extends Packet {

    /** @var int */ 	private $serverTime;
    /** @var int */ 	private $estimatedTime;

    public function __construct(int $serverTime = 0, int $estimatedTime = 0) {
        $this->serverTime = $serverTime;
        $this->estimatedTime = $estimatedTime;
    }

    public function receive(WrappedInputStream $in) {
        $this->serverTime = $in->readLong();
        $this->estimatedTime = $in->readLong();
    }

    public function send(WrappedOutputStream $out) {
        $out->writeLong($this->serverTime);
        $out->writeLong($this->estimatedTime);
    }

    public function getEstimatedTime(): int {
        return $this->estimatedTime;
    }

    public function getServerTime(): int {
        return $this->serverTime;
    }

}
