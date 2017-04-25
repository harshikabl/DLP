package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;

/**
 * Created by harsh on 25-04-2017.
 */

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.ViewHolder> {
    private List<Data> question_List;
    Typeface VodafoneRg;
    Typeface materialdesignicons_font;
    private Context context;

    public QuizQuestionAdapter(Context context, List<Data> questionList) {
        this.question_List = questionList;
        this.context = context;
        VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        materialdesignicons_font = FontManager.getFontTypeface(context, "fonts/materialdesignicons-webfont.otf");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_question_listitem, parent, false);
        QuizQuestionAdapter.ViewHolder viewHolder = new QuizQuestionAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QuizQuestionAdapter.ViewHolder holder, final int position) {
        holder.view_icon.setTypeface(materialdesignicons_font);
        holder.view_icon.setText(Html.fromHtml("&#xf43e;"));
        holder.view_text.setTypeface(VodafoneRg);
        holder.question_option.setTypeface(VodafoneRg);
        holder.question_option.setText(question_List.get(position).getOption());
        holder.radio_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.radio_option.setTypeface(materialdesignicons_font);
                holder.radio_option.setText(Html.fromHtml("&#xf43e;"));
            }
        });



    }

    @Override
    public int getItemCount() {
        return question_List.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView view_icon, question_option, view_text,radio_option;
        public ImageView question_image;

        public ViewHolder(View itemView) {
            super(itemView);
            question_image = (ImageView) itemView.findViewById(R.id.question_image);
            view_text = (TextView) itemView.findViewById(R.id.view_text);
            view_icon = (TextView) itemView.findViewById(R.id.view_icon);
            question_option = (TextView) itemView.findViewById(R.id.question_option);
            radio_option = (TextView) itemView.findViewById(R.id.radio_option);


        }
    }

}
