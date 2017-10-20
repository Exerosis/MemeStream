package stream.meme.app.application;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by Exerosis on 10/17/2017.
 */
@Data
public class Comment {
    final User author;
    final String date;
    final String content;
    final List<Comment> replies = new ArrayList<>();

}
