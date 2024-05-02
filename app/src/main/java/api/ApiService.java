package api;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Body;
import com.example.leaf_Service.VoiceData;
import com.example.leaf_Service.VoiceDataResponse;

public interface ApiService {
    @POST("voiceData")
    Call<VoiceDataResponse> uploadVoiceData(@Body VoiceData voiceData);
}

