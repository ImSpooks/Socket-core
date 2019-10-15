<?php
/**
 * Created by Nick on 14 okt. 2019.
 * Copyright © ImSpooks
 */

class ArrayUtils {

    public static function isMap(array $array): bool {
        return count(array_filter(array_keys($array), 'is_string')) > 0;
    }
}