package datamodel.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Item implements Article {

    String name;
    String desc; // nullable
    String rPath;
    List<String> unlocks;
    String blueprint;
    boolean tradable;
    boolean lootbox;
    boolean decay;

    public Item(String name, String desc, String rPath, String[] unlocks, String blueprint, boolean isTradable, boolean lootbox, boolean decay) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        this.unlocks = new ArrayList<>(Arrays.asList(unlocks));
        this.blueprint = blueprint;
        this.tradable = isTradable;
        this.lootbox = lootbox;
        this.decay = decay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRPath() {
        return rPath;
    }

    public List<String> getUnlocks() {
        return unlocks;
    }

    public void setUnlocks(List<String> unlocks) {
        this.unlocks = unlocks;
    }

    public void addUnlock(String unlock) {
        this.unlocks.add(unlock);
    }

    public boolean getTradable() {
        return tradable;
    }

    public void setTradable(boolean tradable) {
        this.tradable = tradable;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    public boolean getLootbox() {
        return lootbox;
    }

    public void setLootbox(boolean isLootbox) {
        this.lootbox = isLootbox;
    }

    public boolean getDecay() {
        return decay;
    }

    public void setDecay(boolean decay) {
        this.decay = decay;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item)) return false;
        Item i = (Item) o;
        return name.equals(i.getName()) && Objects.equals(desc, i.getDesc()) && rPath.equals(i.getRPath())
                && Objects.equals(blueprint, i.getBlueprint()) && tradable == i.getTradable() && lootbox == i.getLootbox()
                && decay == i.getDecay() && unlocks.equals(i.getUnlocks());
    }
}
