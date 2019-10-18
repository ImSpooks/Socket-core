<?php
/**
 * Created by Nick on 18 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\other;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketResponseExpired extends Packet {

    /** @var int */ 	private $ms;

    public function __construct(int $ms = 0) {
        $this->ms = $ms;
    }

    public function send(WrappedOutputStream $out) {
        $out->writeLong($this->ms);
    }

    public function receive(WrappedInputStream $in) {
        $this->ms = $in->readLong();
    }

}
