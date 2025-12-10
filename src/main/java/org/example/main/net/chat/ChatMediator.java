package org.example.main.net.chat;

import java.util.Map;

public interface ChatMediator {
    void register(String id, ChatColleague colleague);
    void unregister(String id);
    void distribute(String senderId, ChatEvent event);
    Map<String, ChatColleague> getColleagues();
}
