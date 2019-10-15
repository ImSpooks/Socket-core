<?php
/**
 * Created by Nick on 15 okt. 2019.
 * Copyright © ImSpooks
 */

class StringUtils {


    public static function startsWith(string $string, string $value): bool {
        return substr($string, 0, strlen($value)) === $value;
    }

    public static function contains(string $string, string $value): bool {
        return strpos($string, $value) !== false;
    }

}