package org.example.main.net.chat;

import java.util.Map;

public class ChatCommandEvent extends ChatEvent {
    public ChatCommandType command;
    public Map<String, String> args;

    public ChatCommandEvent() {}

    public ChatCommandEvent(String senderId, ChatCommandType command, Map<String, String> args) {
        this.senderId = senderId;
        this.command = command;
        this.args = args;
    }
}
