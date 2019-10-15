<?php

require_once "../../init/Packet.php";
require_once "../../init/channels/WrappedInputStream.php";
require_once "../../init/channels/WrappedOutputStream.php";

/**
 * Created by Nick on 15 okt 2019.
 * Copyright © ImSpooks
 */

class PacketResponseExpired extends Packet {

    private int $ms;

    public function __construct(int $ms = 0) {
        $this->ms = $ms;
    }

    public function receive(WrappedInputStream $in) {
        $this->ms = $in->readLong();
    }

    public function send(WrappedOutputStream $out) {
        $out->writeLong($this->ms);
    }

}
