package com.example.commander;

public sealed interface RawCommand permits DefaultRawCommand{
    byte[] command();
    byte[] signature();
}
