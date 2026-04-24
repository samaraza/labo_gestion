package labo_gestion_api.service;

import labo_gestion_api.model.Post;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // ✅ أفضل من @Autowired
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;  // ✅ أضف هذا

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
    }

    // ✅ جلب جميع المنشورات للمدرسة الحالية
    public List<Post> findPostsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return postRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ جلب المنشورات مرتبة حسب التاريخ للمدرسة الحالية
    public List<Post> findPostsOrderByDateForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return postRepository.findByEcoleIdOrderByDateDesc(currentUser.getEcole().getId());
    }

    // ✅ إضافة منشور جديد للمدرسة الحالية
    public Post saveForCurrentSchool(Post post, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        if (post.getDate() == null) {
            post.setDate(LocalDateTime.now());
        }
        post.setEcole(currentUser.getEcole());  // ✅ ربط المنشور بالمدرسة
        return postRepository.save(post);
    }

    public Post save(Post post) {
        if (post.getDate() == null) {
            post.setDate(LocalDateTime.now());
        }
        return postRepository.save(post);
    }

    public Post update(Long id, Post updatedPost) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));

        if (updatedPost.getTitle() != null) {
            existingPost.setTitle(updatedPost.getTitle());
        }
        if (updatedPost.getContent() != null) {
            existingPost.setContent(updatedPost.getContent());
        }
        if (updatedPost.getDate() != null) {
            existingPost.setDate(updatedPost.getDate());
        } else {
            existingPost.setDate(LocalDateTime.now());
        }

        return postRepository.save(existingPost);
    }

    public void deleteById(Long id) {
        Post post = findById(id);
        postRepository.deleteById(id);
    }
}