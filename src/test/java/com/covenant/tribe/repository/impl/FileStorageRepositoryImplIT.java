package com.covenant.tribe.repository.impl;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.configuration.PathConfiguration;
import com.covenant.tribe.dto.ImageDto;
import com.github.javafaker.Faker;
import net.minidev.json.JSONObject;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Import({FileStorageRepositoryImpl.class, PathConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FileStorageRepositoryImplIT extends AbstractTestcontainers {

    //write in test method is SPRING_PARENT_DIRECTORY

    @Autowired
    FileStorageRepositoryImpl fileStorageRepository;

    @Autowired
    PathConfiguration pathConfiguration;

    Faker faker = new Faker();
    Tika tika = new Tika();
    @Test
    void  saveFileToTmpDir() throws IOException {

        String nameFile = createImageToTmpDir();

        assertFalse(nameFile.isEmpty());
    }

    @Test
    void addEventAvatars() throws IOException {



        String file1 = createImageToTmpDir();

        String file2 = createImageToTmpDir();

        List<String> fileNames = List.of(file1, file2);

        List<String> paths = fileStorageRepository.addEventAvatars(fileNames);

        assertFalse(paths.isEmpty());
        assertTrue(paths.get(0).contains(File.separator));
    }

    @Test
    void  addUserAvatar() throws IOException {

        String file1 = createImageToTmpDir();

        String fileNameForAdding = fileStorageRepository.addUserAvatar(file1);

        assertFalse(fileNameForAdding.isEmpty());
        assertTrue(fileNameForAdding.contains(File.separator));

    }

    @Test
    void getRectangleAnimationJson() throws IOException {

        String file1 = "lottie.json";

        StringBuilder pathToNewFolder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getAnimation()).append(File.separator)
                .append(pathConfiguration.getAddEvents());

        Files.createDirectories(Path.of(pathToNewFolder.toString()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("animation", "Light");

        String pathFile = pathToNewFolder.append(File.separator).append(file1).toString();

        Files.write(Path.of(pathFile), jsonObject.toJSONString().getBytes());

        String a = fileStorageRepository.getRectangleAnimationJson(file1);

        assertFalse(a.isEmpty());

        Files.deleteIfExists(Path.of(pathFile));
    }


    @Test
    void getCircleAnimationJson() throws IOException {

        String file1 = "lottie.json";

        StringBuilder pathToNewFolder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getAnimation()).append(File.separator)
                .append(pathConfiguration.getOnboard());

        Files.createDirectories(Path.of(pathToNewFolder.toString()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("animation", "Light");

        String pathFile = pathToNewFolder.append(File.separator).append(file1).toString();

        Files.write(Path.of(pathFile), jsonObject.toJSONString().getBytes());

        String inside = fileStorageRepository.getCircleAnimationJson(file1);

        assertFalse(inside.isEmpty());

        Files.deleteIfExists(Path.of(pathFile));
    }



    @Test
    void deleteFileInTmpDir() throws IOException {
        String image1 = createImageToTmpDir();
        String image2 = createImageToTmpDir();

        List<String> fileNames = List.of(image1, image2);

        fileStorageRepository.deleteFileInTmpDir(fileNames);

        for (String fileName : fileNames) {
            assertFalse(Files.exists( Path.of(pathConfiguration.getTmp()).resolve(fileName)));
        }

    }

    @Test
    void deleteEventAvatars() throws IOException {
        StringBuilder NewFolder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getImage()).append(File.separator)
                .append(pathConfiguration.getEvent()).append(File.separator)
                .append(pathConfiguration.getAvatar());

        String file1 = "2023-06-21";
        String file2 = "2023-06-22";

        Path path = Path.of(NewFolder.toString());
        Files.createDirectories(path.resolve(file1));
        Files.createDirectories(path.resolve(file2));

        List<String> fileNames = List.of(file1, file2);

        fileStorageRepository.deleteEventAvatars(fileNames);

        for (String fileName : fileNames) {
            assertFalse(Files.exists( path.resolve(fileName)));
        }

    }

    @Test
    void getUserAvatarByFileName() throws IOException {

        String filePath = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getImage()).append(File.separator)
                .append(pathConfiguration.getUser()).append(File.separator)
                .append(pathConfiguration.getAvatar()).append(File.separator)
                .append("2023-06-21").toString();

        String avatarFileName = "2023-06-21" + File.separator + "c1b00948-d59a-4fef-8c99-d6f59e611545.jpg";

        Path path = Path.of(filePath);
        Path avatarPath = path.resolve("c1b00948-d59a-4fef-8c99-d6f59e611545.jpg");
        Files.createDirectories(path);

        byte[] image = FAKER.avatar().image().getBytes();

        Files.write(avatarPath, image);

        byte[] imageByteArray = Files.readAllBytes(avatarPath);
        File file = new File(avatarPath.toString());
        String ContentType = tika.detect(file);
        ImageDto imageDto = new ImageDto(ContentType, imageByteArray);


        ImageDto result = fileStorageRepository.getUserAvatarByFileName(avatarFileName);


        assertEquals(imageDto.getContentType(), result.getContentType());
        assertArrayEquals(imageDto.getImage(), result.getImage());

        Files.deleteIfExists(avatarPath);
    }

    @Test
    void getEventAvatarByFileName() throws IOException {

        String filePath = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getImage()).append(File.separator)
                .append(pathConfiguration.getEvent()).append(File.separator)
                .append(pathConfiguration.getAvatar()).append(File.separator)
                .append("2023-06-22").toString();

        String avatarFileName = "2023-06-22" + File.separator + "c1b00948-d59a-4fef-8c99-d6f59e611545.jpg";

        Path path = Path.of(filePath);
        Path avatarPath = path.resolve("c1b00948-d59a-4fef-8c99-d6f59e611545.jpg");
        Files.createDirectories(path);

        byte[] image = FAKER.avatar().image().getBytes();

        Files.write(avatarPath, image);

        byte[] imageByteArray = Files.readAllBytes(avatarPath);
        File file = new File(avatarPath.toString());
        String ContentType = tika.detect(file);
        ImageDto imageDto = new ImageDto(ContentType, imageByteArray);


        ImageDto result = fileStorageRepository.getEventAvatarByFileName(avatarFileName);


        assertEquals(imageDto.getContentType(), result.getContentType());
        assertArrayEquals(imageDto.getImage(), result.getImage());

        Files.deleteIfExists(avatarPath);
    }


    String createImageToTmpDir() throws IOException {
        String image = faker.avatar().image();
        String contentType = tika.detect(image);

        return fileStorageRepository.saveFileToTmpDir(contentType, image.getBytes());
    }
}
