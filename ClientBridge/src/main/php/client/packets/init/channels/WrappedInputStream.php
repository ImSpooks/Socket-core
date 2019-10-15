<?php
/**
 * Created by Nick on 14 okt. 2019.
 * Copyright © ImSpooks
 */

class WrappedInputStream {

    private array $in;
    private int $index = 0;

    public function __construct(array $in) {
        $this->in = $in;
    }

    public function read(string $type, bool $increment = true) {
        $object = $this->in[$this->index];
        settype($object, $type);
        if ($increment) {
            $this->in++;
        }
        return $object;
    }

    public function skip(int $amount = 1) {
        $this->in += $amount;
    }

    public function readBoolean(): bool {
        return $this->read("bool");
    }

    public function readDouble(): float {
        return $this->read("double");
    }

    public function readFloat(): float {
        return $this->read("float");
    }

    public function readInt(): int {
        return $this->read("int");
    }

    public function readLong(): int {
        return $this->read("int");
    }

    public function readShort(): int {
        return $this->read("int");
    }

    public function readBytes(): array {
        return $this->read("array");
    }

    public function readString(): string {
        return $this->read("string");
    }

    public function readTypePrefixed() {
        $id = $this->readInt();

        switch ($id) {
            case -1:
                return null;
            case 1:
                return $this->readString();
            case 2:
                return $this->readInt();
            case 3:
                return $this->readLong();
            case 4:
                return $this->readDouble();
            case 5:
                return $this->readFloat();
            case 6:
                return $this->readBytes();
            case 7: {
                $size = $this->readInt();
                $list = [];
                for ($i = 0; $i < $size; $i++) {
                    array_push($list, $this->readTypePrefixed());
                }
                return $list;
            }
            case 8:
            case 9:
                return json_decode($this->readString());
            default: {
                throw new UnexpectedValueException(sprintf("Cannot read data with given id \'%s\'", $id));
            }
        }
    }

    public function getIn(): array {
        return $this->in;
    }
}