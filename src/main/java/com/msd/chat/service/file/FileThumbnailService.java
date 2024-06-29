package com.msd.chat.service.file;

import com.msd.chat.exception.BaseException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileThumbnailService {
  public String getThumbFolder(final String filePath) {
    String fileExt = "." + FilenameUtils.getExtension(filePath);
    String fileName = FilenameUtils.getName(filePath).replace(fileExt, "");

    String fileNameWithExt = fileName + fileExt;

    String mainFolderPath = new File(filePath).getAbsolutePath().replace(fileNameWithExt, "");

    String folderPath = String.format("%s_THUMBS/", fileName);

    return mainFolderPath + folderPath;
  }

  public String getThumbFilePath(final String filePath, final String size, final String format) {
    String fileExt = "." + FilenameUtils.getExtension(filePath);
    String fileName = FilenameUtils.getName(filePath).replace(fileExt, "");

    String path = getThumbFolder(filePath) + fileName;

    return String.format("%s.%s.%s", path, size, format);
  }

  public String getThumbnailImage(
      final String filePath, final int width, final int height, final String format) {

    String thumbFilePath = getThumbFilePath(filePath, String.format("%dx%d", width, height), format);

    thumbFilePath = Arrays.stream(thumbFilePath.split("/uploads/")).toList().getLast();

    boolean fileExists = new File("uploads/", thumbFilePath).exists();

    if (!fileExists) {
      thumbFilePath = generateThumbnailImage(filePath, width, height, format);
    }

    return thumbFilePath == null ? "" : thumbFilePath;
  }

  public String generateThumbnailImage(
      final String filePath, final int width, final int height, final String format) {

    String thumbFilePath = getThumbFilePath(filePath, String.format("%dx%d", width, height), format);

    String folderPath = getThumbFolder(filePath);

    File dir = new File(folderPath);

    if (!dir.exists()) {
      boolean folderCreated = dir.mkdirs();

      if (!folderCreated) {
        throw new BaseException(Map.of("error", "Folder cannot be created"), 403);
      }
    }

    try {
      Thumbnails.of(filePath).size(width, height).outputFormat(format).toFile(thumbFilePath);
    } catch (Exception e) {
      thumbFilePath = null;
    }

    return thumbFilePath;
  }

  public void saveThumbnailAsWebp(final BufferedImage bufferedImage, final String savePath)
      throws IOException {
    File outputFile = new File(FilenameUtils.getName(savePath));

    ImageIO.write(bufferedImage, "webp", outputFile);
  }
}
