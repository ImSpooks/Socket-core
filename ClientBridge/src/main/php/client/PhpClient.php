<?php
/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */

namespace client;

use client\enums\ClientType;
use client\handler\Client;
use client\packets\collection\network\PacketConfirmConnection;
use client\packets\collection\network\PacketRequestConnection;
use client\packets\collection\other\PacketResponseExpired;
use client\packets\init\Packet;
use client\packets\init\PacketRegister;
use client\packets\security\SecurityEncryption;
use client\packets\security\shared\SharedEncryption;

class PhpClient {

    // TODO convert to 7.4 if released
    /** @var int  */ private $verificationId;
    /** @var Client */ private $client;

    public function __construct(string $ip, int $port, bool $bridge = false) {
        $this->verificationId = rand(PHP_INT_MIN, PHP_INT_MAX);
        $this->client = new Client($this, $ip, $port, $bridge);

        if (!$bridge) {
            $this->tryConnect();
        }
    }

    private function tryConnect() {
        $this->sendAndReadPacket(new PacketRequestConnection(PASSWORD, $this->verificationId, CLIENT_NAME, ClientType::PHP["ID"]), function (PacketConfirmConnection $packet) {
            if ($packet->getRandomKey() == $this->verificationId) {
                echo "Connection confirmed, packets can now be send.\n";
                $this->client->setCoreConnected(true);
            }
            else {
                die(sprintf("Verification key was %s but had to be %s, disconnecting...\n", $packet->getRandomKey(), $this->verificationId));
            }
        }, function ($expire) {
            $this->tryConnect();
        });
    }

    public function sendPacket(Packet $packet): bool {
        if ($this->client->isCoreConnected() || $packet instanceof PacketRequestConnection) {
            $this->client->write($packet);
            return true;
        }
        return false;
    }

    public function sendAndReadPacket(Packet $packet, $function, $expire) {
        if (!$this->sendPacket($packet))
            return;
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
        foreach (self::listFolderFiles($dir . "/client/utils", true) as $file) {
            require_once "utils/" . $file;
        }

        // enums
        foreach (self::listFolderFiles($dir . "/client/enums", true) as $file) {
            require_once "enums/" . $file;
        }

        // security
        require_once "packets/security/SecurityEncryption.php";
        require_once "packets/security/shared/SharedEncryption.php";
        SharedEncryption::setEncryption(SecurityEncryption::newInstance(ENCRYPTION_KEY, INIT_VECTOR));


        // packet init
        require_once "ClientSettingsExample.php";
        require_once "packets/init/channels/WrappedInputStream.php";
        require_once "packets/init/channels/WrappedOutputStream.php";
        require_once "packets/type/PacketType.php";
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

    public function close() {
        $this->client->close();
    }
}