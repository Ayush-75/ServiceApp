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
                 "Authorization:key=your key


    })
    @POST("fcm/send")
    Observable<MyResponse>sendFriendRequestToUser(@Body Request body);
}
