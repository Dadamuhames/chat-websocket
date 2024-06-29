package com.msd.chat.service.file;

import com.msd.chat.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final FileStoreService fileStoreService;
  private final FileGetService fileGetService;

  public String getNewFileName(final String extension) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    return String.format("%d.%s", timestamp.getTime(), extension);
  }

  public String getFileUploadDir() {
    String dirPath;
    LocalDate currentDate = LocalDate.now();

    dirPath =
        "uploads/"
            + String.format(
                "%s/%s/%s/",
                currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth());

    return dirPath;
  }

  public String saveFile(final MultipartFile file) throws IOException {
    fileStoreService.deleteExpiredFiles();
    String savedFilePath;

    byte[] fileBytes = file.getBytes();

    String dirPath = getFileUploadDir();

    String ext = FilenameUtils.getExtension(file.getOriginalFilename());

    String fileName = getNewFileName(ext);

    File dir = new File(dirPath);

    if (!dir.exists()) {
      boolean folderCreated = dir.mkdirs();

      if (!folderCreated) {
        throw new BaseException(Map.of("error", "Error with file uploading"), 403);
      }
    }

    try {
      String savedFileFullPath = dir.getAbsolutePath() + "/";

      File savedFile = new File(savedFileFullPath, fileName);

      BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(savedFile));

      stream.write(fileBytes);
      stream.close();

      savedFilePath = savedFile.getPath();

      String filePath = fileGetService.getCorrectFilePath(savedFilePath);
      fileStoreService.saveFile(filePath);

    } catch (Exception e) {
      throw new BaseException(Map.of("error", e.getMessage()), 403);
    }

    return savedFilePath;
  }
}
