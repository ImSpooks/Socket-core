<?php
/**
 * Created by Nick on 14 okt. 2019.
 * Copyright © ImSpooks
 */

class WrappedOutputStream {

   private array $out;

    public function __construct() {
        $this->out = [];
    }

    public function write($bytes) {
        array_push($out, $bytes);
    }

    public function writeBoolean(bool $b) {
        array_push($out, $b);
    }

    public function writeDouble(float $d) {
        array_push($out, $d);
    }

    public function writeFloat(float $f) {
        array_push($out, $f);
    }

    public function writeInt(int $i) {
        array_push($out, $i);
    }

    public function writeLong(int $l) {
        array_push($out, $l);
    }

    public function writeShort(int $s) {
        array_push($out, $s);
    }

    public function writeString(String $s) {
        array_push($out, $s);
    }

    public function writeTypePrefixed(object $o) {
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