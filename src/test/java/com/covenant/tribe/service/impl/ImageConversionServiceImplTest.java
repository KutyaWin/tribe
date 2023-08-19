package com.covenant.tribe.service.impl;

import com.covenant.tribe.service.ImageConversionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageConversionServiceImplTest {

    @Autowired
    private final ImageConversionService imageConversionService;

    private static final int MAXIMUM_WIDTH = 1920;
    private static final int MAXIMUM_HEIGHT = 1080;

    ImageConversionServiceImplTest() {
        this.imageConversionService = new  ImageConversionServiceImpl();
    }

    @Test
    void convertToWebpWithLosslessCompression_shouldThrowExceptionIfBadData() {
        // Given
        byte[] inputData = {0, 1};

        // Then
        assertThrows(Exception.class, () -> imageConversionService.convertToWebpWithLosslessCompression(inputData));
    }

    @Test
    void resize_shouldResizeCorrectlyIfNotExceedsMaxDimensions_DontMaintainRatio()
            throws IOException {
        // Given
        byte[] image300x200jpg = generateImage(300, 200, "jpg");
        byte[] image800x250gif = generateImage(800, 250, "gif");
        byte[] image123x456webp = generateImage(123, 456, "webp");

        // When
        byte[] image600x400jpg = imageConversionService
                .resize(image300x200jpg, "jpg", 600, 400, false);
        byte[] image400x100gif = imageConversionService
                .resize(image800x250gif, "gif", 400, 100, false);
        byte[] image200x900webp = imageConversionService
                .resize(image123x456webp, "webp", 200, 900, false);
        Dimension jpgDimension = getImageDimension(image600x400jpg);
        Dimension gifDimension = getImageDimension(image400x100gif);
        Dimension webpDimension = getImageDimension(image200x900webp);

        // Then
        assertEquals(600, jpgDimension.width);
        assertEquals(400, jpgDimension.height);
        assertEquals(400, gifDimension.width);
        assertEquals(100, gifDimension.height);
        assertEquals(200, webpDimension.width);
        assertEquals(900, webpDimension.height);
    }

    @Test
    void resize_shouldResizeCorrectlyIfNotExceedsMaxDimensions_MaintainRatio()
            throws IOException {
        // Given
        byte[] image300x200jpg = generateImage(300, 200, "jpg");
        byte[] image800x250gif = generateImage(800, 200, "gif");
        byte[] image315x630webp = generateImage(315, 630, "webp");

        // When
        byte[] image150x100jpg = imageConversionService
                .resize(image300x200jpg, "jpg", 150, 100, true);
        byte[] image400x100gif = imageConversionService
                .resize(image800x250gif, "gif", 405, 100, true);
        byte[] image200x400webp = imageConversionService
                .resize(image315x630webp, "webp", 200, 450, true);
        Dimension jpgDimension = getImageDimension(image150x100jpg);
        Dimension gifDimension = getImageDimension(image400x100gif);
        Dimension webpDimension = getImageDimension(image200x400webp);

        // Then
        assertEquals(150, jpgDimension.width);
        assertEquals(100, jpgDimension.height);
        assertEquals(400, gifDimension.width);
        assertEquals(100, gifDimension.height);
        assertEquals(200, webpDimension.width);
        assertEquals(400, webpDimension.height);
    }

    @Test
    void process_shouldThrowExceptionWhenNotMIMEType() {
        // Given
        String wrongMimeType = "wrong MIME type";
        byte[] data = {0, 1};

        // Then
        assertThrows(Exception.class, () -> imageConversionService.process(data, wrongMimeType));
    }

    @Test
    void process_shouldThrowExceptionWhenWrongFormatOrType() {
        // Given
        String wrongImageFormat = "image/img";
        String applicationJsonType = "application/json";
        byte[] data = {0, 1};

        // Then
        assertThrows(IllegalArgumentException.class, () -> imageConversionService.process(data, wrongImageFormat));
        assertThrows(IllegalArgumentException.class, () -> imageConversionService.process(data, applicationJsonType));
    }

    @Test
    void process_shouldNotThrowExceptionWhenCorrectMIMEType() throws IOException {
        // Given
        String mimeType = "image/jpg";
        byte[] data = generateImage(100, 200, "jpg");

        // Then
        assertDoesNotThrow(() -> imageConversionService.process(data, mimeType));
    }

    @Test
    void process_shouldResizeIfExceedsMaxDimensions() throws IOException {
        // Given
        String mimeType1 = "image/jpg";
        String mimeType2 = "image/webp";
        byte[] image1 = generateImage(MAXIMUM_WIDTH + 100, MAXIMUM_HEIGHT, "jpg");
        byte[] image2 = generateImage(MAXIMUM_WIDTH * 2, MAXIMUM_HEIGHT * 2, "webp");

        // When
        byte[] image1Processed = imageConversionService.process(image1, mimeType1);
        byte[] image2Processed = imageConversionService.process(image2, mimeType2);
        Dimension image1Dimension = getImageDimension(image1Processed);
        Dimension image2Dimension = getImageDimension(image2Processed);

        // Then
        assertEquals(MAXIMUM_WIDTH, image1Dimension.width);
        assertEquals(MAXIMUM_HEIGHT, image2Dimension.height);
    }

    private byte[] generateImage(int width, int height, String format) throws IOException {
        // Creates output image
        BufferedImage outputImage = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_RGB);
        // Scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.draw(new Rectangle(0, 0, width, height));
        g2d.dispose();
        // Writes to output byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageOutputStream ios =  ImageIO.createImageOutputStream(bos);
        ImageIO.write(outputImage, format, ios);
        ios.flush();

        return bos.toByteArray();
    }

    private Dimension getImageDimension (byte[] data) throws IOException {
        // Reads input image
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage inputImage = ImageIO.read(bis);

        return new Dimension(inputImage.getWidth(), inputImage.getHeight());
    }
}