package org.example.main.net.chat;

public interface ChatColleague {
    void receiveChat(ChatEvent event);
    void sendChat(String text);
}
