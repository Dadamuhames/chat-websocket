package com.msd.chat.service.file;

import com.msd.chat.domain.FileStoreEntity;
import com.msd.chat.repository.FileStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStoreService {
  private final FileStoreRepository fileStoreRepository;
  private final FileDeleteService fileDeleteService;

  public void saveFile(String filePath) {
    FileStoreEntity file =
            FileStoreEntity.builder().file(filePath).expireAt(LocalDateTime.now().plusDays(2)).build();

    fileStoreRepository.save(file);
  }

  public void deleteExpiredFiles() {
    LocalDateTime now = LocalDateTime.now();
    List<FileStoreEntity> files = fileStoreRepository.findByExpireAt(now);

    for (FileStoreEntity file : files) {
      String filePath = file.getFile();
      fileDeleteService.deleteFile(filePath);
    }

    fileStoreRepository.deleteByExpireAt(now);
  }


  // delete if file is used
  public void deleteByFile(String file) {
    fileStoreRepository.deleteAllByFile(file);
  }
}
