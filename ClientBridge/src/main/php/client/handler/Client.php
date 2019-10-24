<?php
/**
 * Created by Nick on 17 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\handler;


use client\packets\collection\network\PacketClosing;
use client\packets\init\Packet;
use client\PhpClient;
use client\utils\TimeUtils;

class Client {

    private $socket;

    // TODO convert to 7.4 if released

    /** @var PhpClient */ private $coreClient;
    /** @var bool */ private $coreConnected;

    /** @var string */ private $ip;
    /** @var int */ private $port;

    public function __construct(PhpClient $coreClient, string $ip, int $port, bool $bridge = true) {
        $this->coreClient = $coreClient;

        $this->socket = socket_create(AF_INET, SOCK_STREAM, 0);
        $this->ip = $ip;
        $this->port = $port;

        $this->coreConnected = $bridge;

        $this->initialize();
    }

    private function initialize() {
        // socket connected
        socket_set_option($this->socket, SOL_SOCKET, SO_RCVTIMEO, array("sec" => 5, "usec" => 0));
        socket_connect($this->socket, $this->ip, $this->port);
    }

    public function write(Packet $packet) {
        $serialized = $packet->serialize() . "\r\n";
        socket_write($this->socket, $serialized, strlen($serialized));
    }

    public function read(): string {
        $databuffer = "";
        while (true) {
            $response = socket_read($this->socket, 1);
            if ($response !== false) {
                if (strlen($response) == 0) {
                    break;
                }
                else if(strstr($response, "\n")) {
                    break;
                }
                else {
                    $databuffer .= $response;
                }
            }
            else {
                break;
            }
        }
        if (socket_last_error($this->socket) !== 0) {
            var_dump(socket_strerror(socket_last_error($this->socket)));
            return "";
        }
        return $databuffer;
//        $response = socket_read($this->socket, 128);
//        if ($response != null) {
//            return $response;
//        }
//        return "";
    }

    public function setCoreConnected(bool $coreConnected): void {
        $this->coreConnected = $coreConnected;
    }

    public function isCoreConnected(): bool {
        return $this->coreConnected;
    }

    public function close() {
        $this->write(new PacketClosing());
        socket_close($this->socket);
    }
}