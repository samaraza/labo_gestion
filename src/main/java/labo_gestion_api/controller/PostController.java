package labo_gestion_api.controller;

import labo_gestion_api.model.Post;
import labo_gestion_api.model.User;
import labo_gestion_api.service.PostService;
import labo_gestion_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;  // ✅ أضف هذا

    // ✅ الحصول على جميع المنشورات (للمدير العام)
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    // ✅ جلب المنشورات للمدرسة الحالية فقط
    @GetMapping("/my-school")
    public ResponseEntity<List<Post>> getPostsForCurrentSchool(Authentication authentication) {
        List<Post> posts = postService.findPostsForCurrentSchool(authentication);
        return ResponseEntity.ok(posts);
    }

    // ✅ جلب المنشورات مرتبة حسب التاريخ للمدرسة الحالية
    @GetMapping("/my-school/ordered")
    public ResponseEntity<List<Post>> getPostsOrderedForCurrentSchool(Authentication authentication) {
        List<Post> posts = postService.findPostsOrderByDateForCurrentSchool(authentication);
        return ResponseEntity.ok(posts);
    }

    // ✅ الحصول على منشور بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    // ✅ إنشاء منشور جديد للمدرسة الحالية
    @PostMapping("/my-school")
    public ResponseEntity<Post> createPostForCurrentSchool(
            @RequestBody Post post,
            Authentication authentication) {

        Post savedPost = postService.saveForCurrentSchool(post, authentication);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    // ✅ إنشاء منشور جديد (قديم)
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post savedPost = postService.save(post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    // ✅ تحديث منشور
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody Post updatedPost) {

        Post updated = postService.update(id, updatedPost);
        return ResponseEntity.ok(updated);
    }

    // ✅ حذف منشور
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}