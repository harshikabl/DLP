package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 4/26/2017.
 */

public class Pivot {
    private int content_id;
    private int quiz_id;

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public int getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    @Override
    public String toString() {
        return "ClassPojo [content_id = " + content_id + ", quiz_id = " + quiz_id + "]";
    }
}
