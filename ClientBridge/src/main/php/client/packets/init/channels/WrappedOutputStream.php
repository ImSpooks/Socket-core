<?php

namespace client\packets\init\channels;

use ArrayUtils;
use UnexpectedValueException;

/**
 * Created by Nick on 14 okt. 2019.
 * Copyright © ImSpooks
 */
class WrappedOutputStream {
    // TODO convert to 7.4 if released

    /** @var array */ private $out = [];

    public function write($bytes) {
        array_push($this->out, $bytes);
    }

    public function writeBoolean(bool $b) {
        array_push($this->out, $b);
    }

    public function writeDouble(float $d) {
        array_push($this->out, $d);
    }

    public function writeFloat(float $f) {
        array_push($this->out, $f);
    }

    public function writeInt(int $i) {
        array_push($this->out, $i);
    }

    public function writeLong(int $l) {
        array_push($this->out, $l);
    }

    public function writeShort(int $s) {
        array_push($this->out, $s);
    }

    public function writeString(String $s) {
        array_push($this->out, $s);
    }

    public function writeTypePrefixed($o) {
        if ($o == null) {
            $this->write(-1);
        }
        else if (is_string($o)) {
            $this->write(0);
            $this->writeString($o);
        }
        else if (is_int($o)) {
            $this->write(1);
            $this->writeInt($o);
        }
        else if (is_long($o)) {
            $this->write(2);
            $this->writeLong($o);
        }
        else if (is_double($o)) {
            $this->write(3);
            $this->writeDouble((Double) $o);
        }
        else if (is_float($o)) {
            $this->write(4);
            $this->writeFloat((Float) $o);
        }
        else if (is_bool($o)) {
            $this->write(5);
            $this->writeBoolean($o);
        }
        else if (is_array($o)) {
            if (!ArrayUtils::isMap($o)) { // list
                $this->write(7);
                $this->writeInt(sizeof($o));
                foreach($o as $inList)
                    $this->writeTypePrefixed($inList);
            }
            else { // map
                $this->write(8);
                $this->writeString(json_encode($o));
            }
        }
        else {
            throw new UnexpectedValueException(sprintf("Cannot write data with class \'%s\'", gettype($o)));
        }
    }

    public function getOut(): array {
        return $this->out;
    }
}