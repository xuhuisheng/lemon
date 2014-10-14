package com.mossle.user.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;














import org.springframework.web.bind.annotation.RequestParam;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.UserBaseManager;


@Controller
@RequestMapping("/user")
public class QRCodeController {
	private UserBaseManager userBaseManager;
	@RequestMapping("qrcode")
	public void qrcode(@RequestParam(value = "id", required = false) Long id,HttpServletResponse response) throws IOException{
		System.out.println(id);
		UserBase userBase = null;
		if (id != null) {
            userBase = userBaseManager.get(id);
        }else {
        	userBase = new UserBase();
        }

		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN:VCARD");sb.append("\r\n");
		sb.append("VERSION:3.0");sb.append("\r\n");
		sb.append("FN:"+userBase.getUsername());sb.append("\r\n");
		sb.append("EMAIL;PREF;INTERNET:"+userBase.getEmail());sb.append("\r\n");
		sb.append("TEL;CELL;VOICE:"+userBase.getMobile());sb.append("\r\n");
		sb.append("END:VCARD");sb.append("\r\n");
		
		 ByteArrayOutputStream out = QRCode.from(sb.toString()).to(ImageType.PNG).stream();
		 response.setContentType("image/png");
		 response.setContentLength(out.size());
		 OutputStream outStream = response.getOutputStream();
		 outStream.write(out.toByteArray());
		 outStream.flush();
		 outStream.close();
		
	}
	/**
	 * 编码
	 * 
	 * @param contents
	 * @param width
	 * @param height
	 * @param imgPath
	 */
	public static void encode(String contents, int width, int height, String imgPath) {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		// 指定编码格式
		hints.put(EncodeHintType.CHARACTER_SET, "GBK");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
					BarcodeFormat.QR_CODE, width, height, hints);

			MatrixToImageWriter
					.writeToFile(bitMatrix, "png", new File(imgPath));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }
}
