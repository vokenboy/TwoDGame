package org.example.main.net.chat.interpreter;

import org.example.main.net.chat.ChatCommandEvent;
import org.example.main.net.chat.ChatCommandType;

import java.util.Collections;

public class HelpExpression extends ChatExpression {
    @Override
    public void interpret(ChatContext ctx) {
        if (ctx.resultEvent != null) return;
        String lower = ctx.rawInput == null ? "" : ctx.rawInput.trim().toLowerCase();
        if (lower.equals("/help")) {
            ctx.resultEvent = new ChatCommandEvent(ctx.senderId, ChatCommandType.HELP, Collections.emptyMap());
        }
    }
}
