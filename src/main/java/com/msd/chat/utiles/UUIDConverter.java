package com.msd.chat.utiles;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;


@Component
public class UUIDConverter {
    public UUID getUUIDFromBytes(final byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }
}
