package stream.meme.backend;

import lombok.Data;

import static java.util.Objects.hash;

@Data
public class Comment {
    private final User author;
    private final String date, content;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Comment &&
                ((Comment) obj).author.equals(author) &&
                ((Comment) obj).content.equals(content) &&
                ((Comment) obj).date.equals(date);
    }

    @Override
    public int hashCode() {
        return hash(author, content, date);
    }
}