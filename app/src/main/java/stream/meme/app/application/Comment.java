package stream.meme.app.application;

import com.google.common.base.Optional;

import lombok.Data;

import static com.google.common.base.Optional.*;

/**
 * Created by Exerosis on 10/17/2017.
 */
@Data
public class Comment {
    final User author;
    final String date;
    final String content;
    final Boolean sent;

    public Optional<Boolean> isSent() {
        return fromNullable(sent);
    }
}
