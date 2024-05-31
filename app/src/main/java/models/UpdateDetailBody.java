package models;

public class UpdateDetailBody {
    public int pupusaId;
    public int amount;

    public UpdateDetailBody(int pupusaId, int amount) {
        this.pupusaId = pupusaId;
        this.amount = amount;
    }
}
