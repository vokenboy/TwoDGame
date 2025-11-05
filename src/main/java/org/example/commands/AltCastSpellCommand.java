package org.example.commands;

import org.example.entity.Player;

public class AltCastSpellCommand implements Command {
    @Override
    public void execute(Player player) {
        if (player != null) {
            player.castAltSpell();
        }
    }
}
