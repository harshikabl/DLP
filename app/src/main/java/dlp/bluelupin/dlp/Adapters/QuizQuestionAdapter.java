package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.QuizAnswer;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;

/**
 * Created by harsh on 25-04-2017.
 */

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.ViewHolder> {
    private List<Data> optionList;
    Typeface VodafoneRg;
    Typeface materialdesignicons_font;
    private Context context;
    private List<String> OptionAtoZList;
    private int selectedItemPosition = -1;
    private int quizId;
    private int questionId;

    public QuizQuestionAdapter(Context context, List<Data> optionList, List<String> OptionAtoZList, int quizId, int questionId) {
        this.optionList = optionList;
        this.OptionAtoZList = OptionAtoZList;
        this.context = context;
        this.quizId = quizId;
        this.questionId = questionId;
        VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        materialdesignicons_font = FontManager.getFontTypeface(context, "fonts/materialdesignicons-webfont.otf");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_text_item, parent, false);
        QuizQuestionAdapter.ViewHolder viewHolder = new QuizQuestionAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QuizQuestionAdapter.ViewHolder holder, final int position) {

        holder.option.setText(OptionAtoZList.get(position).toString() + ")");
        if (optionList.get(position).getLang_resource_name() != null) {
            holder.answer.setText(optionList.get(position).getLang_resource_name());
        }
        if (selectedItemPosition == position) {
            holder.radio.setChecked(true);
        } else {
            holder.radio.setChecked(false);
        }
        holder.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItemPosition = position;
                SharedPreferences prefs = context.getSharedPreferences("OptionPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("optionId", optionList.get(position).getId());
                editor.putBoolean("selectCheck", true);
                editor.commit();
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView option, answer;
        public RadioButton radio;
        public LinearLayout mainlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            option = (TextView) itemView.findViewById(R.id.option);
            answer = (TextView) itemView.findViewById(R.id.answer);
            radio = (RadioButton) itemView.findViewById(R.id.radio);
            option.setTypeface(VodafoneRg);
            answer.setTypeface(VodafoneRg);
            mainlayout = (LinearLayout) itemView.findViewById(R.id.mainlayout);
        }
    }

}
