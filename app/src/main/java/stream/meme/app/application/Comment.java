package stream.meme.app.application;

import com.google.common.base.Optional;

import lombok.Getter;

import static com.google.common.base.Optional.fromNullable;

@Getter
public class Comment {
    public static final Boolean ERROR = false;
    public static final Boolean SENDING = true;
    public static final Boolean SUCCESS = null;
    final User author;
    final String date;
    final String content;
    final Boolean status;

    public Comment(User author, String date, String content, Boolean status) {
        this.author = author;
        this.date = date;
        this.content = content;
        this.status = status;
    }

    public Comment(User author, String date, String content) {
        this(author, date, content, SUCCESS);
    }

    public Optional<Boolean> getStatus() {
        return fromNullable(status);
    }
}