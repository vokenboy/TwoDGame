package org.example.main.net.chat;

public class TextChatEvent extends ChatEvent {
    public String message;

    public TextChatEvent() {}

    public TextChatEvent(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }
}
