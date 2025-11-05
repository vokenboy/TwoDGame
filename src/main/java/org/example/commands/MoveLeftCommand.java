package org.example.commands;

import org.example.entity.Player;

public class MoveLeftCommand implements Command {
    @Override
    public void execute(Player player) {
        player.direction = "left";
    }
}

