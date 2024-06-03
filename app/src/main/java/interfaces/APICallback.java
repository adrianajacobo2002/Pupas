package interfaces;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface APICallback<T> {
    void onFailure(@NonNull Call call, @NonNull IOException e);

    void onResponse(T ResponseObject, @NonNull Call call, @NonNull Response response) throws IOException;
}
