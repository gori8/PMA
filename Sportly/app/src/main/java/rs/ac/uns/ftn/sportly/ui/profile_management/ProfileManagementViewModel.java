package rs.ac.uns.ftn.sportly.ui.profile_management;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileManagementViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ProfileManagementViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is profile management fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
