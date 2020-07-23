package com.mossle.user;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageUtils {
    public static void zoomImage(InputStream inputStream,
            OutputStream outputStream, int x1, int y1, int x2, int y2)
            throws Exception {
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        //
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        int defaultSize = Math.min(512, Math.min(height, width));

        if (height > width) {
            int h2 = defaultSize;
            int w2 = (defaultSize * width) / height;
            bufferedImage = zoomImage(bufferedImage, w2, h2);
        } else {
            int w2 = defaultSize;
            int h2 = (defaultSize * height) / width;
            bufferedImage = zoomImage(bufferedImage, w2, h2);
        }

        //
        BufferedImage outImage = bufferedImage.getSubimage(x1, y1, x2 - x1, y2
                - y1);
        ImageIO.write(outImage, "png", outputStream);
        inputStream.close();
        outputStream.flush();
    }

    public static void zoomImage(InputStream inputStream,
            OutputStream outputStream, int toWidth, int toHeight)
            throws Exception {
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        bufferedImage = zoomImage(bufferedImage, toWidth, toHeight);

        BufferedImage outImage = bufferedImage;
        ImageIO.write(outImage, "png", outputStream);
        inputStream.close();
        outputStream.flush();
    }

    /**
     * 缩放.
     * 
     * @param srcImage
     *            原始图像
     * @param toWidth
     *            宽度
     * @param toHeight
     *            高度
     * @return 返回处理后的图像
     */
    public static BufferedImage zoomImage(BufferedImage srcImage, int toWidth,
            int toHeight) {
        BufferedImage result = null;

        try {
            BufferedImage im = srcImage;

            /* 原始图像的宽度和高度 */
            // int width = im.getWidth();
            // int height = im.getHeight();

            /* 新生成结果图片 */
            result = new BufferedImage(toWidth, toHeight,
                    BufferedImage.TYPE_INT_RGB);

            result.getGraphics()
                    .drawImage(
                            im.getScaledInstance(toWidth, toHeight,
                                    Image.SCALE_SMOOTH), 0, 0, null);
        } catch (Exception e) {
            System.out.println("创建缩略图发生异常" + e.getMessage());
        }

        return result;
    }
}
