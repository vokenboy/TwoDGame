package org.example.commands;

import org.example.entity.Player;

public interface Command {
    void execute(Player player);
}
