package com.ankit.File.UP.Down.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@RestController
@RequestMapping("/file")
public class FileController {

    public static final String DIRECTORY = System.getProperty("user.home") + "/Downloads/";

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files")List<MultipartFile> multipartFiles) throws IOException {
        List<String> fileNames = new ArrayList<>();
        for(MultipartFile file : multipartFiles){
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Path fileStorage = get(DIRECTORY,fileName).toAbsolutePath().normalize();
            copy(file.getInputStream(),fileStorage,REPLACE_EXISTING);
            fileNames.add(fileName);
        }
        return ResponseEntity.ok().body(fileNames);
    }

    @GetMapping("/download/{fileName}")// path Variable
    public ResponseEntity<Resource> downloadFiles(@PathVariable("fileName") String fileName) throws IOException {
        Path filePath = get(DIRECTORY).toAbsolutePath().normalize().resolve(fileName);
        if(!Files.exists(filePath)){
            throw new FileNotFoundException(fileName + " is not present");
        }
        Resource resource = new UrlResource(filePath.toUri());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File Name",fileName);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;File Name="+fileName);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath))).headers(httpHeaders).body(resource);
    }
}
