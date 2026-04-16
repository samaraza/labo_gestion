package labo_gestion_api.service;


import labo_gestion_api.model.Post;
import labo_gestion_api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
    }

    // ✅ تصحيح: تعيين التاريخ تلقائياً عند الإنشاء
    public Post save(Post post) {
        if (post.getDate() == null) {
            post.setDate(LocalDateTime.now());
        }
        return postRepository.save(post);
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Post update(Long id, Post updatedPost) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));

        // تحديث الحقول فقط إذا كانت غير null
        if (updatedPost.getTitle() != null) {
            existingPost.setTitle(updatedPost.getTitle());
        }
        if (updatedPost.getContent() != null) {
            existingPost.setContent(updatedPost.getContent());
        }
        if (updatedPost.getDate() != null) {
            existingPost.setDate(updatedPost.getDate());
        } else {
            // تحديث التاريخ تلقائياً عند التعديل
            existingPost.setDate(LocalDateTime.now());
        }

        return postRepository.save(existingPost);
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public void deleteById(Long id) {
        Post post = findById(id);
        postRepository.deleteById(id);
    }
}
