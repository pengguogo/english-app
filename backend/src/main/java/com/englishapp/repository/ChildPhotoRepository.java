package com.englishapp.repository;

import com.englishapp.domain.ChildPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChildPhotoRepository extends JpaRepository<ChildPhoto, Integer> {
    List<ChildPhoto> findByUserIdOrderByTakenAtDescIdDesc(Integer userId);
    Optional<ChildPhoto> findByIdAndUserId(Integer id, Integer userId);
}
