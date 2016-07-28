package dlp.bluelupin.dlp.Fragments;

/**
 * Created by Neeraj on 7/28/2016.
 */
public interface ItemTouchHelperInterface {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
