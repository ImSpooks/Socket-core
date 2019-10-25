<?php
/**
 * Created by Nick on 25 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\database;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketRequestData extends Packet {

    /** @var string */ 	private $collection;
    /** @var string */ 	private $key;
    private $value;

    public function __construct(string $collection = "", string $key = "", $value = null) {
        $this->collection = $collection;
        $this->key = $key;
        $this->value = $value;
    }

    public function receive(WrappedInputStream $in) {
        $this->collection = $in->readString();
        $this->key = $in->readString();
        $this->value = $in->readTypePrefixed();
    }

    public function send(WrappedOutputStream $out) {
        $out->writeString($this->collection);
        $out->writeString($this->key);
        $out->writeTypePrefixed($this->value);
    }

    public function getCollection(): string {
        return $this->collection;
    }

    public function getKey(): string {
        return $this->key;
    }

    public function getValue() {
        return $this->value;
    }

}
