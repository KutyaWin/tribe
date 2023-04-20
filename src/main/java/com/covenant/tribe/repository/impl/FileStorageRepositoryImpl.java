package com.covenant.tribe.repository.impl;

import com.covenant.tribe.configuration.PathConfiguration;
import com.covenant.tribe.dto.ImageDTO;
import com.covenant.tribe.exeption.storage.FileNotSavedException;
import com.covenant.tribe.repository.FileStorageRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FileStorageRepositoryImpl implements FileStorageRepository {

    PathConfiguration pathConfiguration;

    @Override
    public String saveFileToTmpDir(String contentType, byte[] image) throws FileNotSavedException {
        String fileName = UUID.randomUUID().toString();
        String fileExtension = contentType.split("/")[1];
        StringBuilder pathToTmpDirBuilder = new StringBuilder() // TODO После изменения пользоватебя в docker, необходимо заменить / на ~/
                .append("/")
                .append(pathConfiguration.getMain())
                .append("/")
                .append(pathConfiguration.getTmp());
        log.info("Path for avatar is {}", pathToTmpDirBuilder);
        try {
            Files.createDirectories(Path.of(pathToTmpDirBuilder.toString()));
            String filePath = pathToTmpDirBuilder
                    .append("/")
                    .append(fileName)
                    .append(".")
                    .append(fileExtension)
                    .toString();
            Files.write(Path.of(filePath), image);
            return fileName + "." + fileExtension;
        } catch (IOException e) {
            String message = String.format("File didn't save, because %s'", e.getMessage());
            throw new FileNotSavedException(message);
        }
    }

    @Override
    public ImageDTO getEventAvatarByFileName(String avatarFileName) throws FileNotFoundException {
        String filePath = new StringBuilder("/")
                .append(pathConfiguration.getMain())
                .append("/")
                .append(pathConfiguration.getImage())
                .append("/")
                .append(pathConfiguration.getEvent())
                .append("/")
                .append(pathConfiguration.getAvatar())
                .append("/")
                .append(avatarFileName)
                .toString();
        try {
            Path pathToFile = Path.of(filePath);
            byte[] imageByteArray = Files.readAllBytes(pathToFile);
            String imageContentType = Files.probeContentType(pathToFile);
            return new ImageDTO(imageContentType, imageByteArray);
        } catch (IOException e) {
            String message = String.format("File with name: %s does not exist", avatarFileName);
            throw new FileNotFoundException(message);
        }
    }
}
