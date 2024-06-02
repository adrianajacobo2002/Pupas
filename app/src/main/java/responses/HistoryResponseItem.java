package responses;

import java.util.List;

import models.Participant;

public class HistoryResponseItem {
    public int id;
    public String name;
    public String code;
    public double total;
    public List<Participant> participants;
}
