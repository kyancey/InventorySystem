package com.kyancey.inventory.entities;

/**
 * <p>This class stores parts that are purchased from outside manufacturers.</p>
 * FUTURE ENHANCEMENT: Although part classes are currently protected from bad
 * input by form validation, it would make sense to put validation directly
 * in the class methods as well to protect against invalid data.
 */
public class Outsourced extends Part {
    private String companyName;

    /**
     * <p>Constructor</p>
     * @param id Part id.
     * @param name Part name.
     * @param price Part price.
     * @param stock Number of part in stock.
     * @param min Minimum number of part in stock.
     * @param max Maximum number of part in stock.
     * @param companyName Name of company that produces the part.
     */
    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    /**
     * <p>Gets the company name.</p>
     * @return Company name.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * <p>Sets the company name.</p>
     * @param companyName Company name.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
