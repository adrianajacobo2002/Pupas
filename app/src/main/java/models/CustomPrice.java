package models;

public class CustomPrice {
    public int id;
    public int partyId;
    public int pupsaId;
    public double price;

    public CustomPrice(int pupsaId, int price) {
        this.pupsaId = pupsaId;
        this.price = price;
    }
}
