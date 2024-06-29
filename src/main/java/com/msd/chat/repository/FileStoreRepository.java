package com.msd.chat.repository;

import com.msd.chat.domain.FileStoreEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface FileStoreRepository extends CrudRepository<FileStoreEntity, Long> {
  @Query("SELECT f FROM FileStoreEntity f WHERE f.expireAt <= :expireAt")
  List<FileStoreEntity> findByExpireAt(@Param("expireAt") LocalDateTime expireAt);

  @Transactional
  @Modifying
  @Query("DELETE FROM FileStoreEntity f WHERE f.expireAt <= :expireAt")
  void deleteByExpireAt(@Param("expireAt") LocalDateTime expireAt);

  @Transactional
  @Modifying
  @Query("DELETE FROM FileStoreEntity f WHERE f.file = :file")
  void deleteAllByFile(@Param("file") String file);
}
