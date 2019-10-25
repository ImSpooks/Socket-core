<?php
/**
 * Created by Nick on 15 okt. 2019.
 * Copyright © ImSpooks
 */

use client\enums\ClientType;
use client\packets\collection\database\PacketRequestCollection;
use client\packets\collection\database\PacketRequestCollectionResponse;
use client\packets\collection\other\PacketPing;
use client\packets\collection\other\PacketPingResponse;
use client\packets\security\shared\SharedEncryption;
use client\PhpClient;
use client\utils\TimeUtils;

require_once "client/PhpClient.php";
PhpClient::requireAll(__DIR__);
$client = new PhpClient(SERVER_IP, SERVER_PORT);

$rows = [];

$client->sendAndReadPacket(new PacketRequestCollection("test_table"), function (PacketRequestCollectionResponse $packet) {
    echo json_encode($packet->getDocuments()) . "\n";
    $rows = $packet->getDocuments();
}, function ($expired) {

});

$client->close();