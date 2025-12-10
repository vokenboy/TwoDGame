package org.example.main.net.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerMediator implements ChatMediator {
    private final Map<String, ChatColleague> players = new ConcurrentHashMap<>();

    @Override
    public void register(String id, ChatColleague colleague) {
        if (id != null && colleague != null) {
            players.put(id, colleague);
        }
    }

    @Override
    public void unregister(String id) {
        if (id != null) {
            players.remove(id);
        }
    }

    @Override
    public void distribute(String senderId, ChatEvent event) {
        if (event == null) return;
        if (event instanceof ChatCommandEvent) {
            ChatColleague target = players.get(senderId);
            if (target != null) {
                target.receiveChat(event);
                return;
            }
            // Fallback: if senderId is missing or mismatched, still show locally once.
            for (ChatColleague c : players.values()) {
                if (c != null) {
                    c.receiveChat(event);
                    break;
                }
            }
            return;
        }

        for (Map.Entry<String, ChatColleague> entry : players.entrySet()) {
            ChatColleague c = entry.getValue();
            if (c != null) {
                c.receiveChat(event);
            }
        }
    }

    @Override
    public Map<String, ChatColleague> getColleagues() {
        return players;
    }
}
