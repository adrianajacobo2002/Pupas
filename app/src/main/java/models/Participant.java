package models;

import com.google.gson.annotations.SerializedName;

public class Participant {
    public int id;
    @SerializedName("user_id")
    public int userId;
    public String names;
    @SerializedName("last_names")
    public String lastNames;
    public Double total;
    @SerializedName("is_host")
    public boolean isHost;
}
