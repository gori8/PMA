package rs.ac.uns.ftn.sportly.ui.my_ratings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyRatingsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MyRatingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my ratings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
