<?php
/**
 * Created by Nick on 25 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\database;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketRequestDataResponse extends Packet {

    /** @var array */ 	private $document;

    public function __construct(array $document = []) {
        $this->document = $document;
    }

    public function receive(WrappedInputStream $in) {
        $this->document = json_decode($in->readString());
    }

    public function send(WrappedOutputStream $out) {
        $out->writeString(json_encode($this->document));
    }

    public function getDocument(): array {
        return $this->document;
    }

}
