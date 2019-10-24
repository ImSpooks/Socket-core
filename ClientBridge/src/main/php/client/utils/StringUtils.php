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

    public static function char_at(string $string, int $position) {
        return $string{$position};
    }

    public static function getBytes($string): array {
        return array_values(unpack('C*', $string));
    }

    public static function fromBytes($bytes): string {
        return implode(array_map("chr", $bytes));
    }
}