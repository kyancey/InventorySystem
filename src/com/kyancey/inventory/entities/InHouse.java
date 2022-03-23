package com.kyancey.inventory.entities;

/**
 * <p>This class stores parts that are manufactured in house.</p>
 */
public class InHouse extends Part {
    private int machineId;

    /**
     * <p>Constructor</p>
     * @param id Part Id.
     * @param name Part name.
     * @param price Part price.
     * @param stock How many of the part in stock.
     * @param min Minimum number of the part in stock.
     * @param max Maximum number of the part in stock.
     * @param machineId Id of the machine that made the part.
     */
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    /**
     * <p>Gets machine id.</p>
     * @return Machine id.
     */
    public int getMachineId() {
        return machineId;
    }

    /**
     * Sets machine id
     * @param machineId Machine id.
     */
    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }
}
