<?php
/**
 * Created by Nick on 25 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\database;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketRequestCollection extends Packet {

    /** @var string */ 	private $collection;

    public function __construct(string $collection = "") {
        $this->collection = $collection;
    }

    public function receive(WrappedInputStream $in) {
        $this->collection = $in->readString();
    }

    public function send(WrappedOutputStream $out) {
        $out->writeString($this->collection);
    }

    public function getCollection(): string {
        return $this->collection;
    }

}
