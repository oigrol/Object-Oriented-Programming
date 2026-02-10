package social;

import java.util.List;

public class PostRepository extends GenericRepository<Post, String> {
    public PostRepository() {
        super(Post.class);
    }
}
