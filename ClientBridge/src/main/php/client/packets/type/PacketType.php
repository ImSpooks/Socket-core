<?php
/**
 * Created by Nick on 15 okt. 2019.
 * Copyright © ImSpooks
 */

class PacketType {

    public const NETWORK = ["NAME" => "NETWORK", "START_ID" => 100];
    public const DATABASE = ["NAME" => "DATABASE", "START_ID" => 200];
    public const OTHER = ["NAME" => "OTHER", "START_ID" => 300];

    public int $START_ID;

    public function __construct(int $START_ID) {
        $this->START_ID = $START_ID;
    }
}