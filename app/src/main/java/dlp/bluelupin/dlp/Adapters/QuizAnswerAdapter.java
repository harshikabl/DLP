package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;

/**
 * Created by harsh on 24-04-2017.
 */

public class QuizAnswerAdapter extends RecyclerView.Adapter<QuizAnswerAdapter.ViewHolder> {
    private List<Data> answer_List;
    Typeface VodafoneRg;
    Typeface materialdesignicons_font;
    private Context context;

    public QuizAnswerAdapter(Context context, List<Data> answerList) {
        this.answer_List = answerList;
        this.context = context;
        VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        materialdesignicons_font = FontManager.getFontTypeface(context, "fonts/materialdesignicons-webfont.otf");
    }

    @Override
    public QuizAnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_answer_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QuizAnswerAdapter.ViewHolder holder, final int position) {
        holder.radio_button.setTypeface(materialdesignicons_font);
        holder.radio_button.setText(Html.fromHtml("&#xf43d;"));
        holder.option.setTypeface(VodafoneRg);
        holder.answer.setTypeface(VodafoneRg);
        holder.option.setText(answer_List.get(position).getOption());
        holder.answer.setText(answer_List.get(position).getAnswer());
        holder.radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.radio_button.setTypeface(materialdesignicons_font);
                holder.radio_button.setText(Html.fromHtml("&#xf43e;"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return answer_List.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView option, answer,radio_button;


        public ViewHolder(View itemView) {
            super(itemView);
            option = (TextView) itemView.findViewById(R.id.option);
            answer = (TextView) itemView.findViewById(R.id.answer);
            radio_button = (TextView) itemView.findViewById(R.id.radio_button);


        }
    }

}
