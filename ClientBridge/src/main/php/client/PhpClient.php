<?php
/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */

namespace client;

use client\handler\Client;
use client\packets\collection\other\PacketResponseExpired;
use client\packets\init\Packet;
use client\packets\init\PacketRegister;

class PhpClient {

    // TODO convert to 7.4 if released
    /** @var Client */ private $client;

    public function __construct(string $ip, int $port) {
        $this->client = new Client($ip, $port);
    }

    public function sendPacket(Packet $packet) {
        $this->client->write($packet);
    }

    public function sendAndReadPacket(Packet $packet, $function, $expire) {
        $this->sendPacket($packet);
        $response = $this->client->read();

        if ($response != null && !empty($response)) {
            $packet = Packet::deserialize($response);

            if ($response != null && !empty($response) && !($packet instanceof PacketResponseExpired))
                $function($packet);
            else if ($expire != null) {
                $expire($packet);
            }
        }
    }

    public static function requireAll($dir) {
        // settings
        require_once "ClientSettings.php";

        // utils
        require_once "utils/ArrayUtils.php";
        require_once "utils/StringUtils.php";
        require_once "utils/TimeUtils.php";

        // packet init
        require_once "packets/init/channels/WrappedInputStream.php";
        require_once "packets/init/channels/WrappedOutputStream.php";
        require_once "packets/type/PacketType.php";
        require_once "packets/init/Packet.php";
        require_once "packets/init/Packet.php";

        foreach (self::listFolderFiles($dir . "/client/packets/collection", true) as $file) {
//            echo $file . "\n";
            require_once "packets/collection/" . $file;
        }

        require_once "packets/init/PacketRegister.php";
        PacketRegister::init();

        // client
        require_once "handler/Client.php";
    }

    // function that gets all files with subfolders
    private static function listFolderFiles(string $dir, bool $subfolders = false, string $dirName = null) {
        global $mainDir;

        $directories = [];

        $ffs = scandir($dir);

        unset($ffs[array_search(".", $ffs, true)]);
        unset($ffs[array_search("..", $ffs, true)]);

        if (count($ffs) < 1)
            return [];

        foreach ($ffs as $ff) {
            if (is_dir($dir. "/" .$ff) && $subfolders) {
                foreach (self::listFolderFiles($dir . "/" . $ff, $subfolders, $dirName == null ? $ff : $dirName . "/" . $ff) as $sff) {
                    array_push($directories, $sff);
                }
            }
            else {
                if ($dir != $mainDir) {
                    array_push($directories, $dirName . "/" . $ff);
                }
                else {
                    array_push($directories, $ff);
                }
            }
        }

        return $directories;
    }
}