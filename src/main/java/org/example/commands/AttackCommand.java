package org.example.commands;

import org.example.entity.Player;

public class AttackCommand implements Command {
    @Override
    public void execute(Player player) {
        player.attackAction();
    }
}
