<?php
/**
 * Created by Nick on 21 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\enums;


class ClientType {
    public const JAVA = ["ID" => 0, "NAME" => "JAVA"];
    public const PHP = ["ID" => 1, "NAME" => "PHP"];

    private static $CACHE = [self::JAVA, self::PHP];

    public static function getFromName(string $name): array {
        foreach (self::$CACHE as $vars) {
            if (strcasecmp($vars["NAME"], $name)) {
                return $vars;
            }
        }
        return [];
    }


    public static function getFromId(int $id): array {
        foreach (self::$CACHE as $vars) {
            if (strcasecmp($vars["ID"], $id)) {
                return $vars;
            }
        }
        return [];
    }
}