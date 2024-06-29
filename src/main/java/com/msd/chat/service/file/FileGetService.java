package com.msd.chat.service.file;

import com.msd.chat.config.properties.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;

@Service
@RequiredArgsConstructor
public class FileGetService {
  private final UploadProperties uploadProperties;
  private final FileThumbnailService fileThumbnailService;

  public String getShowUrl(final String filePath) {
    String newFilePath = getCorrectFilePath(filePath);

    if (newFilePath != null) {
      String domain = uploadProperties.getUrl();
      newFilePath = domain + "/files/" + newFilePath;
    }

    return newFilePath;
  }

  public String getCorrectFilePath(final String path) {
    String[] pathSplit = path.split("uploads/");

    return "uploads/" + Array.get(pathSplit, pathSplit.length - 1);
  }

  public String getFileAbsoluteUrl(final String photoUrl, final int width, final int height) {
    return getFileAbsoluteUrl(photoUrl, width, height, "jpeg");
  }

  public String getFileAbsoluteUrl(final String photoUrl, final int width, final int height, final String format) {
    if (photoUrl == null || photoUrl.isEmpty()) {
      return null;
    }

    String fileThumbUrl = fileThumbnailService.getThumbnailImage(photoUrl, width, height, format);

    if (fileThumbUrl == null || fileThumbUrl.isEmpty()) {
      return null;
    }

    return getShowUrl(fileThumbUrl);
  }
}
