<?php
/**
 * Created by Nick on 21 okt. 2019.
 * Copyright © ImSpooks
 */

namespace client\packets\security;


use client\utils\BufferUtils;
use InvalidArgumentException;
use StringUtils;

class SecurityEncryption {

    //TODO change to 7.4
    /** @var string */ private $method = "aes-128-cbc";

    private $key;
    private $initVector;

    public function __construct(string $key, string $initVector) {
        $this->key = $key;
        $this->initVector = $initVector;
    }


    public static function newInstance(string $key, string $initVector): SecurityEncryption {
        if (strlen($key) != 16)
            throw new InvalidArgumentException(sprintf("Invalid key size (%s) must be 16", strlen($key)));
        if (strlen($initVector) != 16)
            throw new InvalidArgumentException(sprintf("Invalid initVector size (%s) must be 16", strlen($initVector)));

        return new SecurityEncryption($key, $initVector);
    }

    public function encrypt(string $value): string {
        return base64_encode(openssl_encrypt($value, $this->method, $this->key, OPENSSL_RAW_DATA, $this->initVector));
    }

    public function decrypt(string $value): string {
        return openssl_decrypt(base64_decode($value), $this->method, $this->key, OPENSSL_RAW_DATA, $this->initVector);
    }
}