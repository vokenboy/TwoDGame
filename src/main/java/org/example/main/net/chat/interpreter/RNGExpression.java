package org.example.main.net.chat.interpreter;

import org.example.main.net.chat.ChatCommandEvent;
import org.example.main.net.chat.ChatCommandType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RNGExpression extends ChatExpression {
    private final Random rng = new Random();

    @Override
    public void interpret(ChatContext ctx) {
        if (ctx.resultEvent != null) return;
        String raw = ctx.rawInput == null ? "" : ctx.rawInput.trim();
        if (!raw.startsWith("/rng")) {
            return;
        }
        int max = 100;
        String[] parts = raw.split(" ");
        if (parts.length >= 2) {
            try {
                max = Math.max(1, Integer.parseInt(parts[1]));
            } catch (NumberFormatException ignored) {}
        }
        int roll = rng.nextInt(max) + 1;
        Map<String, String> args = new HashMap<>();
        args.put("max", Integer.toString(max));
        args.put("roll", Integer.toString(roll));
        ctx.resultEvent = new ChatCommandEvent(ctx.senderId, ChatCommandType.RNG, args);
    }
}
