package com.msd.chat.controller;


import com.msd.chat.exception.BaseException;
import com.msd.chat.service.file.FileDeleteService;
import com.msd.chat.service.file.FileGetService;
import com.msd.chat.service.file.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/")
public class FileUploadController {
  private final FileUploadService fileUploadService;
  private final FileDeleteService fileDeleteService;
  private final FileGetService fileGetService;

  // upload file
  @PostMapping("/upload")
  public Map<String, String> uploadFile(
      @RequestParam("file") MultipartFile file,
      HttpServletResponse response,
      HttpServletRequest request) {

    Map<String, String> result = new HashMap<>();

    if (!file.isEmpty()) {
      try {
        String fileSavedPath = fileUploadService.saveFile(file);

        String filePath = fileGetService.getCorrectFilePath(fileSavedPath);

        result.put("filePath", filePath);
        result.put("showUrl", fileGetService.getShowUrl(filePath));

      } catch (BaseException ex) { throw ex;
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        result.put("error", e.getMessage());
      }

    } else {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      result.put("error", "file cannot be null");
    }

    return result;
  }

  // delete file by path
  @PostMapping("/delete")
  public Map<String, Object> deleteFile(
      @RequestParam("filePath") String filePath, HttpServletResponse response) {

    Map<String, Object> result = new HashMap<>();
    Map<String, Object> errors = new HashMap<>();

    if (!filePath.isEmpty()) {
      try {
        Boolean deleted = fileDeleteService.deleteFile(filePath);
        result.put("deleted", deleted);

      } catch (Exception e) {
        errors.put("error", e.getMessage());
      }

    } else {
      errors.put("error", "filePath cannot be null");
    }

    if (!errors.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return errors;
    }

    return result;
  }
}
