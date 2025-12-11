package org.example.monster;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import org.example.entity.Entity;
import org.example.main.GamePanel;

public abstract class Monster extends Entity {

    protected GamePanel gp;
    private final Random random = new Random();

    public Monster(GamePanel gp) {
        super(gp);
        this.gp = gp;
        this.type = type_monster;
    }

    public abstract void getImage();

    public abstract void getAttackImage();

    public abstract void setAction();

    public abstract void damageReaction();

    public abstract void setDialogue();

    public final void checkDrop() {
        List<DropEntry> drops = dropTable();
        if (drops == null || drops.isEmpty()) {
            onNoDrop(-1);
            return;
        }

        int roll = rollDropChance();
        for (DropEntry entry : drops) {
            if (entry.matches(roll)) {
                dropItem(entry.createItem());
                return;
            }
        }

        onNoDrop(roll);
    }

    protected abstract List<DropEntry> dropTable();

    protected int rollDropChance() {
        return random.nextInt(100) + 1; // 1..100 inclusive
    }

    protected void onNoDrop(int roll) {}

    protected static final class DropEntry {

        private final int thresholdInclusive;
        private final Supplier<Entity> itemSupplier;

        private DropEntry(
            int thresholdInclusive,
            Supplier<Entity> itemSupplier
        ) {
            this.thresholdInclusive = thresholdInclusive;
            this.itemSupplier = itemSupplier;
        }

        public static DropEntry of(
            int thresholdInclusive,
            Supplier<Entity> supplier
        ) {
            return new DropEntry(thresholdInclusive, supplier);
        }

        boolean matches(int roll) {
            return roll <= thresholdInclusive;
        }

        Entity createItem() {
            return itemSupplier.get();
        }
    }
}
