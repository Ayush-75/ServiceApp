package e.piyush.serviceapp.Interface;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseloadUserNameDone(List<String> lstEmail);
    void onFirebaseLoadFailed(String message);


}
