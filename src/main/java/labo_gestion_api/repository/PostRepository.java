package labo_gestion_api.repository;
import labo_gestion_api.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {


    // ✅ جلب جميع المنشورات لمدرسة محددة
    @Query("SELECT p FROM Post p WHERE p.ecole.id = :ecoleId")
    List<Post> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب المنشورات مرتبة حسب التاريخ لمدرسة محددة
    @Query("SELECT p FROM Post p WHERE p.ecole.id = :ecoleId ORDER BY p.createdDate DESC")
    List<Post> findByEcoleIdOrderByDateDesc(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب منشور محدد والتأكد أنه ينتمي للمدرسة
    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.ecole.id = :ecoleId")
    Post findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);
}