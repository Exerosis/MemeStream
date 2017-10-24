package stream.meme.app.application;

import lombok.Data;

/**
 * Created by Exerosis on 10/17/2017.
 */
@Data
public class Comment {
    final User author;
    final String date;
    final String content;
}
