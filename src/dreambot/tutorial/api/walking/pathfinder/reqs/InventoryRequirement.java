package dreambot.tutorial.api.walking.pathfinder.reqs;

import org.dreambot.api.methods.container.impl.Inventory;

public final class InventoryRequirement extends ItemRequirement {

    public InventoryRequirement(int id, int minQuantity, boolean noted) {
        super(id, minQuantity, noted);
    }

    public InventoryRequirement(int id) {
        super(id);
    }

    public InventoryRequirement(int id, int minQuantity) {
        super(id, minQuantity);
    }

    public InventoryRequirement(String name, int minQuantity, boolean noted) {
        super(name, minQuantity, noted);
    }

    public InventoryRequirement(String name) {
        super(name);
    }

    public InventoryRequirement(String name, int minQuantity) {
        super(name, minQuantity);
    }

    @Override
    public boolean isMet() {
        return hasRequirement(Inventory.all(i -> i != null && i.isValid()));
    }

}
