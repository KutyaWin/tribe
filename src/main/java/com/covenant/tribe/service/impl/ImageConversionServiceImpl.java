package com.covenant.tribe.service.impl;

import com.covenant.tribe.service.ImageConversionService;
import com.luciad.imageio.webp.WebPWriteParam;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

@Service
public class ImageConversionServiceImpl implements ImageConversionService {

    private static final int MAXIMUM_WIDTH = 1920;
    private static final int MAXIMUM_HEIGHT = 1080;
    @Override
    public byte[] convertToWebpWithLosslessCompression(byte[] data) throws IOException {
        // Reads input image
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(bis);

        ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
        // Notify encoder to consider WebPWriteParams
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        // Set lossy compression
        writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);

        // Writes to output byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageOutputStream ios =  ImageIO.createImageOutputStream(bos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), writeParam);
        ios.flush();

        return bos.toByteArray();
    }

    @Override
    public byte[] resize(byte[] data, String format, int scaledWidth, int scaledHeight, boolean maintainAspectRatio)
            throws IOException {
        // Reads input image
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage inputImage = ImageIO.read(bis);

        if (maintainAspectRatio) {
            Dimension scaledDimension =
                    getScaledDimension(new Dimension(inputImage.getWidth(), inputImage.getHeight()),
                            new Dimension(scaledWidth, scaledHeight));
            scaledWidth = (int)scaledDimension.getWidth();
            scaledHeight = (int)scaledDimension.getHeight();
        }

        // Creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // Scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // Writes to output byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageOutputStream ios =  ImageIO.createImageOutputStream(bos);
        ImageIO.write(outputImage, format, ios);
        ios.flush();

        return bos.toByteArray();
    }

    @Override
    public byte[] process(byte[] inputData, String mimeType) throws IOException {
        String type = mimeType.split("/")[0];
        String format = mimeType.split("/")[1];
        Set<String> supportedFormats = Set.of("jpg", "jpeg", "bmp", "webp", "gif", "png");

        if (!type.equals("image")) {
            throw new IllegalArgumentException("Data type is not image");
        }

        if (!supportedFormats.contains(format)) {
            throw new IllegalArgumentException("Unsupported image format");
        }

        byte[] data = inputData;
        if (!mimeType.equals("webp")) {
            data = convertToWebpWithLosslessCompression(inputData);
        }
        Dimension imageDimension = getImageDimension(data);
        if (imageDimension.width <= MAXIMUM_WIDTH && imageDimension.height <= MAXIMUM_HEIGHT) {
            return data;
        }

        int imageWidth = Math.min(imageDimension.width, MAXIMUM_WIDTH);
        int imageHeight = Math.min(imageDimension.height, MAXIMUM_HEIGHT);
        data = resize(data, format, imageWidth, imageHeight, true);
        return data;
    }

    private Dimension getImageDimension (byte[] data) throws IOException {
        // Reads input image
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage inputImage = ImageIO.read(bis);

        return new Dimension(inputImage.getWidth(), inputImage.getHeight());
    }

    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // First check if we need to scale width
        if (original_width > bound_width) {
            // Scale width to fit
            new_width = bound_width;
            // Scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // Then check if we need to scale even with the new height
        if (new_height > bound_height) {
            // Scale height to fit instead
            new_height = bound_height;
            // Scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
}
