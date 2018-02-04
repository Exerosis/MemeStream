package stream.meme.app.controller.alpha.comments;

import android.content.Context;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import stream.meme.app.application.Comment;
import stream.meme.app.databinding.CommentViewBinding;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static stream.meme.app.R.color.disabled_text;
import static stream.meme.app.R.color.red;
import static stream.meme.app.R.layout.comment_view;
import static stream.meme.app.util.Operators.ifPresent;
import static stream.meme.app.util.Operators.ignored;

public class CommentView extends StatefulViewComponent<Comment, CommentViewBinding> {
    private final Observable<Nothing> reply;

    public CommentView(@NonNull Context context) {
        super(context, comment_view);

        reply = getViews()
                .switchMap(view -> clicks(view.reply))
                .compose(ignored());


        getViews().subscribe(view -> {
            getStates().subscribe(state -> {
                view.author.setText(state.getAuthor().getName());
                view.image.setImageBitmap(state.getAuthor().getImage());
                view.content.setText(state.getContent());
                view.date.setText(state.getDate());
            });
            getStates().map(Comment::getStatus)
                    .compose(ifPresent())
                    .distinctUntilChanged()
                    .subscribe(sending -> {
                        if (sending) {
                            view.image.setAlpha(0.5f);
                            view.reply.setAlpha(0.5f);
                            view.author.setAlpha(0.5f);
                            view.date.setAlpha(0.5f);
                            view.content.setAlpha(0.5f);
                        }
                        int color = getResources().getColor(sending ? disabled_text : red);
                        view.author.setTextColor(color);
                        view.date.setTextColor(color);
                        view.content.setTextColor(color);
                    });
        });
    }

    public Observable<Nothing> onReply() {
        return reply;
    }
}