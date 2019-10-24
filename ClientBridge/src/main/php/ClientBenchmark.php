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

$toServer = [];
$toClient = [];
$total = [];

$now = TimeUtils::currentTimeMillis();

for ($i = 0; $i < 1000000; $i++) {
    $client->sendAndReadPacket(new PacketPing(TimeUtils::currentTimeMillis()), function (PacketPingResponse $packet) {
        global $toServer;
        global $toClient;
        global $total;

        $currentTime = TimeUtils::currentTimeMillis();
//        var_dump($packet);
//        echo "client to server time = " . $packet->getEstimatedTime() . " ms\n";
//        echo "server to client time = " . ($currentTime - $packet->getServerTime()) . " ms\n";
//        echo "total time = " . ($packet->getEstimatedTime() + $currentTime - $packet->getServerTime()) . " ms\n";
        array_push($toServer, $packet->getEstimatedTime() + 1);
        array_push($toClient, ($currentTime - $packet->getServerTime()));
        array_push($total, ($packet->getEstimatedTime() + $currentTime - $packet->getServerTime()));

        if (count($toServer) % 1000 == 0)
            echo sprintf("%s packets send\n", count($toServer));
    }, function ($expired) {
        echo "Packet expired.\n";
    });
}

$client->close();

$toServerAvg = 0;
foreach ($toServer as $ms) {
    $toServerAvg += $ms;
}
$toServerAvg /= count($toServer);

$toClientAvg = 0;
foreach ($toClient as $ms) {
    $toClientAvg += $ms;
}
$toClientAvg /= count($toClient);

$totalAvg = 0;
foreach ($total as $ms) {
    $totalAvg += $ms;
}
$totalAvg /= count($total);

echo sprintf("Total packets send: %s packets (Took %s s, %s packets p/s)\n", count($toServer), round((TimeUtils::currentTimeMillis() - $now) / 1000, 3), round(count($toServer) / ((TimeUtils::currentTimeMillis() - $now) / 1000), 3));
echo sprintf("Client to Server average: %s ms\n", round($toServerAvg, 5));
echo sprintf("Server to Client average: %s ms\n", round($toClientAvg, 5));
echo sprintf("Total response time average: %s ms\n", round($totalAvg, 5));

//$client->sendAndReadPacket(new PacketRequestCollection("test_table"), function (PacketRequestCollectionResponse $packet) {
//    var_dump($packet);
//});