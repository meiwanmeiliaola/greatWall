package com.msm.admin.modules.common.controller;

import com.msm.admin.config.FileUploadProperties;
import com.msm.admin.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author quavario@gmail.com
 * @date 2021/5/26 9:09
 */
@Controller
@RequestMapping("/watermark/data")
@Slf4j
public class ImageController {
    private static String logo = "/image/logo5.png";
    private static BufferedImage logoBufferImage;

    static {
        try {
            //logoBufferImage = ImageIO.read(new File("/data/logo3.png"));
            logoBufferImage = ImageIO.read(new File("D:/logo3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int scalePercent = 25;

    @Resource
    private HttpServletResponse response;

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @RequestMapping(value = "/image/**", produces = MediaType.ALL_VALUE)
    @ResponseBody
    public void image(HttpServletRequest request) throws IOException {
//        return ImageIO.read(new FileInputStream(new File("E:\\Dev\\git_repository\\msm\\msm-admin\\src\\main\\resources\\image\\logo3.png")));
        String fileDir = fileUploadProperties.getMappingPath() + "/" + fileUploadProperties.getApplicationName();
        String filePath = fileDir + request.getRequestURI().replace("/watermark/data", "");
        OutputStream os = null;
        try {
//        ????????????
            File sourceFile = new File(filePath);
            if (!sourceFile.exists()) {
                throw new RuntimeException("???????????????" + filePath);
            }
            response.setContentType(Files.probeContentType(Paths.get(filePath)));
            os = response.getOutputStream();

            if (sourceFile.exists()) {
                String path = this.getClass().getResource(logo).getPath();
                File logoFile = new File(path);

                if (!sourceFile.isFile()) {
                    throw new RuntimeException("???????????????");
                }

                //???icon??????????????????
                log.info("logo???????????????{}", path);
                //icon??????
                int logoImageHeight = logoBufferImage.getHeight(null);
                int logoImageWidth = logoBufferImage.getWidth(null);

                //???????????????????????????
                BufferedImage sourceBufferImage = ImageIO.read(sourceFile);

                // ????????????
                int sourceImageWidth = sourceBufferImage.getWidth(null);
                // ????????????
                int sourceImageHeight = sourceBufferImage.getHeight(null);

                BufferedImage bi = new BufferedImage(sourceImageWidth,sourceImageHeight,BufferedImage.TYPE_INT_RGB);
                //?????????????????? BufferedImage ??? Graphics2D ??????
                Graphics2D g = bi.createGraphics();

                // logo????????????????????? ???????????????20??????20%

                double scaledHeight = sourceImageHeight * 0.25;
                // ???????????????
                Double scale = scaledHeight / logoImageHeight;

                //x,y???????????????0????????????
                int x = (int) ((sourceImageWidth / 2) - (logoImageWidth * scale / 2));
                int y = (int) ((sourceImageHeight / 2) - (logoImageHeight * scale / 2));

                //???????????????????????????????????????
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                //??????????????????????????????????????????????????????????????????????????????
                g.drawImage(sourceBufferImage,0,0,null);

                //????????????????????? ???????????????gif??????png??????????????????????????????
                //??????Image?????????
                //????????????????????????0???????????????1
//            float clarity = 0.6f;
//            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,clarity));
                //?????????????????????????????????(x,y)
//            g.drawImage(con, 300, 220, null);

                g.drawImage(scaleBufferImage(logoBufferImage, scale), x, y, null);


                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                g.dispose();
                ImageIO.write(bi, "png", os);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }

    }
    /**
     * @param sourceBufferImage ??????
     * @param scale ????????????
     * @return bufferImage??????
     */
    private static BufferedImage scaleBufferImage(BufferedImage sourceBufferImage, Double scale) {
        // ????????????????????????
        int scaledWidth = (int) (sourceBufferImage.getWidth() * scale);
        int scaledHeight = (int) (sourceBufferImage.getHeight() * scale);

        // ??????????????????image
        Image scaledImage = sourceBufferImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        // ??????scaled BufferImage
        BufferedImage scaledBufferImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);

        // ????????????
        Graphics2D g2d = scaledBufferImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        // ??????
        g2d.dispose();
        return scaledBufferImage;
    }
}
