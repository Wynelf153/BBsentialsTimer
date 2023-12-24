package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.constants.ChChestItem;

public class ChChestData {
    public String finder;
    public Position coords;
    public ChChestItem[] items;

    public ChChestData(String finder, Position coords, ChChestItem[] items) {
        this.finder = finder;
        this.coords = coords;
        this.items = items;
    }
}
