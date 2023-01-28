package com.example.commander;


public sealed interface RawResult permits DefaultRawResult {
    byte[] result();
}
