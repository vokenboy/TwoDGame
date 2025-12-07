package org.example.multiplayer.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Binary codec that keeps the protocol simple and allocation friendly.
 */
public final class NetworkMessageCodec {

    private NetworkMessageCodec() {
    }

    public static void write(NetworkMessage message, DataOutputStream out) throws IOException {
        out.writeUTF(message.type());
        Map<String, String> data = message.data();
        out.writeInt(data.size());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
        out.flush();
    }

    public static NetworkMessage read(DataInputStream in) throws IOException {
        String type = in.readUTF();
        int entries = in.readInt();
        Map<String, String> data = new LinkedHashMap<>(entries);
        for (int i = 0; i < entries; i++) {
            String key = in.readUTF();
            String value = in.readUTF();
            data.put(key, value);
        }
        return NetworkMessage.from(type, data);
    }
}
