<?php
/**
 * Created by Nick on 15 okt. 2019.
 * Copyright © ImSpooks
 */

use client\packets\collection\other\PacketPing;
use client\packets\collection\other\PacketPingResponse;
use client\PhpClient;
use client\utils\TimeUtils;

require_once "client/PhpClient.php";
PhpClient::requireAll(__DIR__);
$client = new PhpClient(SERVER_IP, SERVER_PORT);

$clientserver = [];
$serverclient = [];
$total = [];

$now = TimeUtils::currentTimeMillis();

for ($i = 0; $i < 10000; $i++) {
    $client->sendAndReadPacket(new PacketPing(TimeUtils::currentTimeMillis()), function (PacketPingResponse $packet) {
        global $clientserver;
        global $serverclient;
        global $total;

        $currentTime = TimeUtils::currentTimeMillis();
//        var_dump($packet);
//        echo "client to server time = " . $packet->getEstimatedTime() . " ms\n";
//        echo "server to client time = " . ($currentTime - $packet->getServerTime()) . " ms\n";
//        echo "total time = " . ($packet->getEstimatedTime() + $currentTime - $packet->getServerTime()) . " ms\n";
        array_push($clientserver, $packet->getEstimatedTime());
        array_push($serverclient, ($currentTime - $packet->getServerTime()));
        array_push($total, ($packet->getEstimatedTime() + $currentTime - $packet->getServerTime()));

        if (count($clientserver) % 100 == 0)
            echo sprintf("%s packets send\n", count($clientserver));
    });
}

$clientserverAvg = 0;
foreach ($clientserver as $ms) {
    $clientserverAvg += $ms;
}
$clientserverAvg /= count($clientserver);

$serverclientAvg = 0;
foreach ($serverclient as $ms) {
    $serverclientAvg += $ms;
}
$serverclientAvg /= count($serverclient);

$totalAvg = 0;
foreach ($total as $ms) {
    $totalAvg += $ms;
}
$totalAvg /= count($total);

echo sprintf("Total packets send: %s packets (Took %s ms, %s packets p/s)\n", count($clientserver), TimeUtils::currentTimeMillis() - $now, count($clientserver) / ((TimeUtils::currentTimeMillis() - $now) / 1000));
echo sprintf("Client to Server average: %s ms\n", $clientserverAvg);
echo sprintf("Server to Client average: %s ms\n", $serverclientAvg);
echo sprintf("Total response time average: %s ms\n", $totalAvg);

//$client->sendAndReadPacket(new PacketRequestCollection("test_table"), function (PacketRequestCollectionResponse $packet) {
//    var_dump($packet);
//});