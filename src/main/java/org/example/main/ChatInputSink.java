package org.example.main;

public interface ChatInputSink {
    void onTypedChar(char c);
    void onBackspace();
    void onSubmit();
    void onCancel();
}
