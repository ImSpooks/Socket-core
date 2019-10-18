<?php
/**
 * Created by Nick on 17 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\handler;


use client\packets\init\Packet;

class Client {

    private $socket;

    public function __construct(string $ip, int $port) {
        $this->socket = socket_create(AF_INET, SOCK_STREAM, 0);
        socket_set_option($this->socket,SOL_SOCKET, SO_RCVTIMEO, array("sec" => 10, "usec" => 0));
        socket_connect($this->socket, $ip, $port);
    }

    public function write(Packet $packet) {
        $serialized = "\r" . $packet->serialize() . "\n";
        $length = "\r" . strlen($serialized) . "\n";

        socket_write($this->socket, $length, strlen($length));
        socket_write($this->socket, $serialized, strlen($serialized));
    }

    public function read(): string {
        $length = intval(socket_read($this->socket, 4));
        if ($length > 0) {
            $response = socket_read($this->socket, $length);
            if ($response != null) {
                return $response;
            }
        }
        return "";
    }
}