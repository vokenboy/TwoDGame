package org.example.entity;

import java.util.List;

public interface ItemComponent {

    boolean isContainer();

    List<Entity> getChildren();

    boolean transferToInventory(Player player);
}
