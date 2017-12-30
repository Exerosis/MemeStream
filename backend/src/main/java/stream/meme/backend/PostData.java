package stream.meme.backend;

import lombok.Data;

import static java.util.Objects.hash;

/**
 * Created by Home on 12/29/2017.
 */
@Data
public class PostData {
    private final Long id;
    private final String title, subtitle;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Post && id.equals(((PostData) obj).id);
    }

    @Override
    public int hashCode() {
        return hash(id);
    }
}
