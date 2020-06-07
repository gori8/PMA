package rs.ac.uns.ftn.sportly.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ValueList implements Parcelable {
    private List<String> values;

    public ValueList(){
        values = new ArrayList<>();
    }

    public ValueList(List<String> v){
        values = v;
    }

    protected ValueList(Parcel in) {
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public static final Creator<ValueList> CREATOR = new Creator<ValueList>() {
        @Override
        public ValueList createFromParcel(Parcel in) {
            return new ValueList(in);
        }

        @Override
        public ValueList[] newArray(int size) {
            return new ValueList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
