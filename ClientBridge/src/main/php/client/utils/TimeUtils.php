<?php
/**
 * Created by Nick on 18 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\utils;


class TimeUtils {
    public static function currentTimeMillis() {
        $mt = explode(' ', microtime());
        return ((int)$mt[1]) * 1000 + ((int)round($mt[0] * 1000));
    }
}