<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <style>
        body {
            background-color: #37383e;
            color: #dadada;
        }
    </style>
</head>
<body>
    Test 2?<br>

    <?php

    use client\packets\collection\database\PacketRequestCollection;
    use client\packets\collection\database\PacketRequestCollectionResponse;
    use client\packets\collection\database\PacketUpdateData;
    use client\PhpClient;

    require_once __DIR__ . "/client/PhpClient.php";
    PhpClient::requireAll(__DIR__);
    $client = new PhpClient(SERVER_IP, SERVER_PORT);

    $rows = [];

    $client->sendAndReadPacket(new PacketRequestCollection("test_table"), function (PacketRequestCollectionResponse $packet) {
        global $rows;
        $rows = $packet->getDocuments();
    }, function ($expired) {});
    echo "first iteration: " . json_encode($rows) . "\n";

    $client->sendPacket(new PacketUpdateData("test_table", "id", 3, "new_column", "new value for column"));

    $rows = [];
    $client->sendAndReadPacket(new PacketRequestCollection("test_table"), function (PacketRequestCollectionResponse $packet) {
        global $rows;
        $rows = $packet->getDocuments();
    }, function ($expired) {});
    echo "second iteration: " . json_encode($rows) . "\n";

    $client->close();

    //printf("\nUsing \\parallel\\Runtime is %s\n", $future->value());
    ?>
</body>
</html>