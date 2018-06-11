package toluog.femoji;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

    public interface EmojiClicked {
        public void embedEmoji(int index);
    }

    private int[] emojis;
    private EditImageFragment fragment;

    EmojiAdapter(EditImageFragment fragment, int[] emojis) {
        this.emojis = emojis;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.emoji_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int image = emojis[position];
        holder.bind(image);
    }

    @Override
    public int getItemCount() {
        return emojis.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView emojiView;

        public ViewHolder(View itemView) {
            super(itemView);
            emojiView = itemView.findViewById(R.id.emojiView);
        }

        public void bind(int image) {
            emojiView.setImageResource(image);
            emojiView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.embedEmoji(getAdapterPosition());
                }
            });
        }
    }
}
