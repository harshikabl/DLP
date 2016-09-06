package dlp.bluelupin.dlp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by subod on 05-Sep-16.
 */
public class DownloadData  implements Parcelable {

    public DownloadData(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int progress;
    private int currentFileSize;
    private int totalFileSize;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(int currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(int totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(progress);
        dest.writeInt(currentFileSize);
        dest.writeInt(totalFileSize);
    }

    private DownloadData(Parcel in) {
        id = in.readInt();
        progress = in.readInt();
        currentFileSize = in.readInt();
        totalFileSize = in.readInt();
    }

    public static final Parcelable.Creator<DownloadData> CREATOR = new Parcelable.Creator<DownloadData>() {
        public DownloadData createFromParcel(Parcel in) {
            return new DownloadData(in);
        }

        public DownloadData[] newArray(int size) {
            return new DownloadData[size];
        }
    };
}

