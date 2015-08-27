package no.milch.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to hold movie information from The Movie DB. Made parceable to ease transfer between objects.
 *
 * Created by Scott on 24.08.2015.
 */

public class MovieInfo implements Parcelable {
    String imageUrl;
    String originalTitle;
    String plotSynopsis;
    String rating;
    String releaseDate;

    public MovieInfo() {
    }

    private MovieInfo(Parcel in) {
        this.imageUrl = in.readString();
        this.originalTitle = in.readString();
        this.plotSynopsis = in.readString();
        this.rating = in.readString();
        this.releaseDate = in.readString();
    }

    // Make nice toString for debuging purposes.
    @Override
    public String toString() {
        return TextUtils.join("|", new String[]{
                originalTitle
                , rating
                , releaseDate
                , plotSynopsis
                , imageUrl
        });
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(originalTitle);
        dest.writeString(plotSynopsis);
        dest.writeString(rating);
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
            public MovieInfo createFromParcel(Parcel in) {
                return new MovieInfo(in);
            }
            public MovieInfo[] newArray(int size) {
                return new MovieInfo[size];
            }
        };

}
