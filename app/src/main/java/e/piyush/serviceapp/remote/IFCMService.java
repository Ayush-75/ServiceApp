package e.piyush.serviceapp.remote;

import e.piyush.serviceapp.Model.MyResponse;
import e.piyush.serviceapp.Model.Request;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
                 "Authorization:key=AAAAVZcTpbk:APA91bFTFPWfxtew8YsN-kTZbLXEdx8MfyIwropdTt97LLE5MfXASOcKe8K5NnLz-_EutX2-4gTWDGRDKyn98vzStivQ70_toSza2CWdOMqBQS2v9MEE6pU5yet1SiwLux-yKXgGDD9y"



    })
    @POST("fcm/send")
    Observable<MyResponse>sendFriendRequestToUser(@Body Request body);
}
