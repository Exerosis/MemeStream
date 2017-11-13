package stream.meme.app.application;

import com.google.common.base.Optional;

import lombok.Getter;

import static com.google.common.base.Optional.fromNullable;
import static java.util.Objects.hash;

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Comment && ((Comment) obj).author.equals(author) && ((Comment) obj).content.equals(content);
    }

    @Override
    public int hashCode() {
        return hash(author, content);
    }
}