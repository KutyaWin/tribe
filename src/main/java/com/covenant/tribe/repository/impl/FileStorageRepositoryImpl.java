package com.covenant.tribe.repository.impl;

import com.covenant.tribe.configuration.PathConfiguration;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.exeption.storage.FilesNotHandleException;
import com.covenant.tribe.repository.FileStorageRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FileStorageRepositoryImpl implements FileStorageRepository {

    PathConfiguration pathConfiguration;
    @Override
    public String saveFileToTmpDir(String contentType, byte[] image) throws FilesNotHandleException {
        String fileName = UUID.randomUUID().toString();
        String fileExtension = contentType.split("/")[1];
        StringBuilder pathToTmpDirBuilder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain())
                .append(File.separator)
                .append(pathConfiguration.getTmp());
        System.out.println("Path for avatar is " + pathToTmpDirBuilder);
        log.info("Path for avatar is {}", pathToTmpDirBuilder);
        try {
            Files.createDirectories(Path.of(pathToTmpDirBuilder.toString()));
            String filePath = pathToTmpDirBuilder
                    .append(fileName)
                    .append(".")
                    .append(fileExtension)
                    .toString();
            Files.write(Path.of(filePath), image);
            return fileName + "." + fileExtension;
        } catch (IOException e) {
            String message = String.format("File didn't save, because %s'", e.getMessage());
            log.info("Path for avatar is {}", pathToTmpDirBuilder);
            throw new FilesNotHandleException(message);
        }
    }

    @Override
    public List<String> addEventAvatars(List<String> fileNames) throws IOException {
        String currentDate = LocalDate.now().toString();
        ArrayList<String> paths = new ArrayList<>();
        String pathToTmpDir = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getTmp()).toString();
        String pathToNewFolder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getImage()).append(File.separator)
                .append(pathConfiguration.getEvent()).append(File.separator)
                .append(pathConfiguration.getAvatar()).append(File.separator)
                .append(currentDate).append(File.separator)
                .toString();
        System.out.println("Path to new folder is " + pathToNewFolder);
        Files.createDirectories(Path.of(pathToNewFolder));

        for (String fileName : fileNames) {
            String pathForDb = currentDate + File.separator + fileName;
            String pathForFile = pathToNewFolder + File.separator + fileName;
            paths.add(pathForDb);
            Files.copy(
                    Path.of(pathToTmpDir + File.separator + fileName),
                    Path.of(pathForFile));
        }
        return paths;
    }

    @Override
    public String addUserAvatar(String fileNameForAdding) throws IOException {
        String currentDate = LocalDate.now().toString();
        String pathToTmpDir = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getTmp()).toString();
        String pathToNewFolder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getImage()).append(File.separator)
                .append(pathConfiguration.getUser()).append(File.separator)
                .append(pathConfiguration.getAvatar()).append(File.separator)
                .append(currentDate).append(File.separator)
                .toString();
        Files.createDirectories(Path.of(pathToNewFolder));
        String pathForDb = currentDate + File.separator + fileNameForAdding;
        String pathForFile = pathToNewFolder + File.separator + fileNameForAdding;
        Files.copy(
                Path.of(pathToTmpDir + File.separator + fileNameForAdding),
                Path.of(pathForFile));

        return pathForDb;
    }

    @Override
    public String getRectangleAnimationJson(String fileName) throws IOException {
        String filePath = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getAnimation()).append(File.separator)
                .append(pathConfiguration.getAddEvents()).append(File.separator)
                .append(fileName).toString();
        return Files.readString(Path.of(filePath));
    }

    @Override
    public String getCircleAnimationJson(String fileName) throws IOException {
        String filePath = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getAnimation()).append(File.separator)
                .append(pathConfiguration.getOnboard()).append(File.separator)
                .append(fileName).toString();
        return Files.readString(Path.of(filePath));
    }

    @Override
    public void deleteFileInTmpDir(List<String> fileNames) throws IOException {
        String pathToTmpDir = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getTmp()).toString();
        for (String fileName : fileNames) {
            Files.deleteIfExists(Path.of(pathToTmpDir + File.separator + fileName));
        }
    }

    @Override
    public void deleteEventAvatars(List<String> fileNames) throws IOException {
        for (String fileName : fileNames) {
            String pathToEventAvatarsDir = new StringBuilder(pathConfiguration.getHome())
                    .append(pathConfiguration.getMain()).append(File.separator)
                    .append(pathConfiguration.getImage()).append(File.separator)
                    .append(pathConfiguration.getEvent()).append(File.separator)
                    .append(pathConfiguration.getAvatar()).append(File.separator)
                    .append(fileName)
                    .toString();
            Files.deleteIfExists(Path.of(pathToEventAvatarsDir));
        }
    }

    @Override
    public ImageDto getUserAvatarByFileName(String fileName) throws FileNotFoundException {
        String filePath = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getImage()).append(File.separator)
                .append(pathConfiguration.getUser()).append(File.separator)
                .append(pathConfiguration.getAvatar()).append(File.separator)
                .append(fileName).toString();
        return getImageDto(fileName, filePath);
    }

    @Override
    public ImageDto getEventAvatarByFileName(String avatarFileName) throws FileNotFoundException {
        String filePath = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain())
                .append(File.separator)
                .append(pathConfiguration.getImage())
                .append(File.separator)
                .append(pathConfiguration.getEvent())
                .append(File.separator)
                .append(pathConfiguration.getAvatar())
                .append(File.separator)
                .append(avatarFileName)
                .toString();
        return getImageDto(avatarFileName, filePath);
    }

    @NotNull
    private static ImageDto getImageDto(String avatarFileName, String filePath) throws FileNotFoundException {
        try {
            Path pathToFile = Path.of(filePath);
            byte[] imageByteArray = Files.readAllBytes(pathToFile);
            File image = new File(filePath);
            Tika tika = new Tika();
            String mimeType = tika.detect(image);
            return new ImageDto(mimeType, imageByteArray);
        } catch (IOException e) {
            String message = String.format("File with name: %s does not exist", avatarFileName);
            throw new FileNotFoundException(message);
        }
    }
}
