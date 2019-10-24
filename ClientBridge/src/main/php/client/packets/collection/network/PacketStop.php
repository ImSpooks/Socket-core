<?php
/**
 * Created by Nick on 24 okt 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\collection\network;

use client\packets\init\Packet;
use client\packets\init\channels\WrappedInputStream;
use client\packets\init\channels\WrappedOutputStream;

class PacketStop extends Packet {


    public function __construct() {
    }

    public function receive(WrappedInputStream $in) {
    }

    public function send(WrappedOutputStream $out) {
    }

}
