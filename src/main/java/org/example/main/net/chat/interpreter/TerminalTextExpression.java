package org.example.main.net.chat.interpreter;

import org.example.main.net.chat.TextChatEvent;

public class TerminalTextExpression extends ChatExpression {
    @Override
    public void interpret(ChatContext ctx) {
        if (ctx.resultEvent == null) {
            ctx.resultEvent = new TextChatEvent(ctx.senderId, ctx.rawInput);
        }
    }
}
