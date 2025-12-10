package org.example.main.net.chat.interpreter;

import org.example.main.net.chat.ChatEvent;

public class ChatContext {
    public final String rawInput;
    public final String senderId;
    public ChatEvent resultEvent;

    public ChatContext(String rawInput, String senderId) {
        this.rawInput = rawInput;
        this.senderId = senderId;
    }
}
