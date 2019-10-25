<?php
/**
 * Created by Nick on 25 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\database;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketRequestCollectionResponse extends Packet {

    /** @var array */ 	private $documents;

    public function __construct(array $documents = []) {
        $this->documents = $documents;
    }

    public function receive(WrappedInputStream $in) {
        $this->documents = json_decode($in->readString());
    }

    public function send(WrappedOutputStream $out) {
        $out->writeString(json_encode($this->documents));
    }

    public function getDocuments(): array {
        return $this->documents;
    }

}
