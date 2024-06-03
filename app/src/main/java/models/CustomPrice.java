package models;

public class CustomPrice {
    public int id;
    public int partyId;
    public int pupusaId;
    public double price;

    public CustomPrice(int pupusaId, int price) {
        this.pupusaId = pupusaId;
        this.price = price;
    }
}
