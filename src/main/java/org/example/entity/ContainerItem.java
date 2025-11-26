package org.example.entity;

import org.example.main.GamePanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerItem extends Entity {

    private final List<Entity> children = new ArrayList<>();
    private final int maxItems;

    public ContainerItem(GamePanel gp) {
        this(gp, Integer.MAX_VALUE);
    }

    public ContainerItem(GamePanel gp, int maxItems) {
        super(gp);
        this.maxItems = maxItems;
    }

    public boolean addItem(Entity item) {
        if (children.size() >= maxItems) {
            return false;
        }
        children.add(item);
        return true;
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public List<Entity> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean transferToInventory(Player player) {
        boolean added = false;
        Iterator<Entity> iterator = children.iterator();
        while (iterator.hasNext()) {
            Entity child = iterator.next();
            if (player.canObtainItem(child)) {
                iterator.remove();
                added = true;
            }
        }
        return added;
    }

    public String describeContents() {
        if (children.isEmpty()) {
            return "nothing";
        }
        return children.stream()
                .map(entity -> entity.name != null ? entity.name : "item")
                .collect(Collectors.joining(", "));
    }
}
