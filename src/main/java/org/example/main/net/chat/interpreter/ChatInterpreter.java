package org.example.main.net.chat.interpreter;

import java.util.ArrayList;
import java.util.List;

public class ChatInterpreter {
    private final List<ChatExpression> expressions = new ArrayList<>();

    public ChatInterpreter() {
        expressions.add(new HelpExpression());
        expressions.add(new WhisperExpression());
        expressions.add(new RNGExpression());
        expressions.add(new TerminalTextExpression());
    }

    public void interpret(ChatContext ctx) {
        for (ChatExpression expr : expressions) {
            expr.interpret(ctx);
            if (ctx.resultEvent != null) {
                break;
            }
        }
    }
}
