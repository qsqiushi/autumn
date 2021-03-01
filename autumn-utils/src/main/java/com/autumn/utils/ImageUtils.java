package com.autumn.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;

@Slf4j
public class ImageUtils {

  private static final Base64.Decoder decoder = Base64.getDecoder();

  private static final Base64.Encoder encoder = Base64.getEncoder();

  private static final String IMAGE_PREFIX_PNG = "data:image/png;base64,";

  /**
   * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
   *
   * @param image
   * @return
   */
  public static String encodeImageToBase64(BufferedImage image) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ImageIO.write(image, "png", outputStream);
      return IMAGE_PREFIX_PNG + encoder.encodeToString(outputStream.toByteArray());
    } catch (MalformedURLException e1) {
      log.error("图片64编码失败", e1);
    } catch (IOException e) {
      log.error("图片64编码失败", e);
    }

    return null;
  }

  /**
   * 将Base64的字节转换为Image
   *
   * @param base64String
   * @return
   */
  public static BufferedImage decodeBase64ToImage(String base64String) {
    byte[] bytes1 = decoder.decode(base64String);
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
    BufferedImage image = null;
    try {
      image = ImageIO.read(bais);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return image;
  }
}
