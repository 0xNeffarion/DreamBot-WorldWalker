package com.neffware.api.walking.pathfinder.reqs;

import org.dreambot.api.wrappers.items.Item;

import java.util.List;

public abstract class ItemRequirement implements Requirement {

    private final String name;
    private final int minQuantity;
    private final boolean noted;

    public ItemRequirement(int id, int minQuantity, boolean noted) {
        this.name = fetchName(id);
        this.minQuantity = minQuantity;
        this.noted = noted;
    }

    public ItemRequirement(int id) {
        this(id, 1, false);
    }

    public ItemRequirement(int id, int minQuantity) {
        this(id, minQuantity, false);
    }

    public ItemRequirement(String name, int minQuantity, boolean noted) {
        this.name = name;
        this.minQuantity = minQuantity;
        this.noted = noted;
    }

    public ItemRequirement(String name) {
        this(name, 1, false);
    }

    public ItemRequirement(String name, int minQuantity) {
        this(name, minQuantity, false);
    }

    private String fetchName(int id) {
        return new Item(id, 1).getName();
    }

    public final String getName() {
        return name;
    }

    public final int getMinimumQuantity() {
        return minQuantity;
    }

    public final boolean isNoted() {
        return noted;
    }

    protected final boolean isRequiredItem(final Item item) {
        return item != null && item.getName() != null && item.getName().equals(getName()) && (item.isNoted() == isNoted()) && item.getAmount() >= getMinimumQuantity();
    }

    protected boolean hasRequirement(final List<Item> pool) {
        return hasRequirement(getAmount(pool));
    }

    protected boolean hasRequirement(final int amount) {
        return amount >= getMinimumQuantity();
    }

    protected final int getAmount(final List<Item> pool) {
        int c = 0;
        for (Item i : pool) {
            if (!isRequiredItem(i)) {
                continue;
            }

            if (!i.isStackable()) {
                c++;
            } else {
                return i.getAmount();
            }
        }

        return c;
    }


}
