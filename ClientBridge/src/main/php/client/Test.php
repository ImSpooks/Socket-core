<?php
/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */

require_once "packets/init/Packet.php";

class Test {

    public function __construct() {
        echo Packet::getClassName();
    }

}