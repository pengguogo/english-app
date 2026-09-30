package com.englishapp.repository;

import com.englishapp.domain.ChildPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ChildPhotoRepository extends JpaRepository<ChildPhoto, Integer> {
    List<ChildPhoto> findByUserIdOrderByTakenAtDescIdDesc(Integer userId);

    Optional<ChildPhoto> findByIdAndUserId(Integer id, Integer userId);

    /** 查询用户已使用的分类(去重、非空、按名称排序),供前端筛选标签聚合 */
    @Query("select distinct p.category from ChildPhoto p "
            + "where p.userId = ?1 and p.category is not null order by p.category")
    List<String> findDistinctCategories(Integer userId);
}
