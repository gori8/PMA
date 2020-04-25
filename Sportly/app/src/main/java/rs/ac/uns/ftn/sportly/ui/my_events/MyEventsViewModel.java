package rs.ac.uns.ftn.sportly.ui.my_events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyEventsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MyEventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my events fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

