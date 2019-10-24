<?php
/**
 * Created by Nick on 21 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\security\shared;


use client\packets\security\SecurityEncryption;
use InvalidArgumentException;

class SharedEncryption {

    //TODO convert to 7.4
    /** @var SecurityEncryption */ private static $encryption;

	public static function setEncryption(SecurityEncryption $encryption) {
		if ($encryption == null)
			throw new InvalidArgumentException("Encryption may not be null");
		self::$encryption = $encryption;
	}

    public static function getEncryption(): SecurityEncryption {
		return self::$encryption;
	}

}