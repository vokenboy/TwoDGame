package org.example.main.net.chat.interpreter;

import org.example.main.net.chat.ChatCommandEvent;
import org.example.main.net.chat.ChatCommandType;

import java.util.HashMap;
import java.util.Map;

public class WhisperExpression extends ChatExpression {
    @Override
    public void interpret(ChatContext ctx) {
        if (ctx.resultEvent != null) return;
        String raw = ctx.rawInput == null ? "" : ctx.rawInput.trim();
        if (!raw.startsWith("/w ") && !raw.startsWith("/whisper ")) {
            return;
        }
        String[] parts = raw.split(" ", 3);
        if (parts.length < 3) {
            return;
        }
        Map<String, String> args = new HashMap<>();
        args.put("target", parts[1]);
        args.put("message", parts[2]);
        ctx.resultEvent = new ChatCommandEvent(ctx.senderId, ChatCommandType.WHISPER, args);
    }
}
