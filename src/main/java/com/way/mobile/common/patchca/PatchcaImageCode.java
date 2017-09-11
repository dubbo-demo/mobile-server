package com.way.mobile.common.patchca;

import com.github.bingoohuang.patchca.background.BackgroundFactory;
import com.github.bingoohuang.patchca.color.ColorFactory;
import com.github.bingoohuang.patchca.color.RandomColorFactory;
import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.ConfigurableFilterFactory;
import com.github.bingoohuang.patchca.filter.library.AbstractImageOp;
import com.github.bingoohuang.patchca.filter.library.WobbleImageOp;
import com.github.bingoohuang.patchca.font.RandomFontFactory;
import com.github.bingoohuang.patchca.text.renderer.BestFitTextRenderer;
import com.github.bingoohuang.patchca.text.renderer.TextRenderer;
import com.github.bingoohuang.patchca.utils.encoder.EncoderHelper;
import com.github.bingoohuang.patchca.word.RandomWordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @ClassName: PatchcaImageCode
 * @Description: Patchca生成图片验证码
 * @author: xinpei.xu
 * @date: 2017/08/17 20:23
 */
public class PatchcaImageCode {
	private static Logger logger = LoggerFactory.getLogger(PatchcaImageCode.class);
	
	private static ConfigurableCaptchaService configurableCaptchaService = null;
	private static ColorFactory colorFactory = null;
	private static RandomFontFactory fontFactory = null;
	private static RandomWordFactory wordFactory = null;
	private static TextRenderer textRenderer = null;
	
	private final static String word = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/**
	 * @Title: init
	 * @Description: 初始化数据
	 * @return: void
	 */
	private void init () {
		configurableCaptchaService = new ConfigurableCaptchaService();
		// 颜色创建工厂,使用一定范围内的随机色
		colorFactory = new RandomColorFactory();
		configurableCaptchaService.setColorFactory(colorFactory);
		// 随机字体生成器
		fontFactory = new RandomFontFactory();
		fontFactory.setMaxSize(26);
		fontFactory.setMinSize(22);
		configurableCaptchaService.setFontFactory(fontFactory);
		// 随机字符生成器,去除掉容易混淆的字母和数字,如o和0等
		wordFactory = new RandomWordFactory();
		wordFactory.setCharacters(word);
		wordFactory.setMaxLength(4);
		wordFactory.setMinLength(4);
		configurableCaptchaService.setWordFactory(wordFactory);

		// 自定义验证码图片背景
		MyCustomBackgroundFactory backgroundFactory = new MyCustomBackgroundFactory();
		configurableCaptchaService.setBackgroundFactory(backgroundFactory);

		// 图片滤镜设置
		ConfigurableFilterFactory filterFactory = new ConfigurableFilterFactory();
		List<BufferedImageOp> filters = new ArrayList<BufferedImageOp>();
		WobbleImageOp wobbleImageOp = new WobbleImageOp();
		wobbleImageOp.setEdgeMode(AbstractImageOp.EDGE_MIRROR);
		wobbleImageOp.setxAmplitude(2.0);
		wobbleImageOp.setyAmplitude(1.0);
		filters.add(wobbleImageOp);
		filterFactory.setFilters(filters);
		configurableCaptchaService.setFilterFactory(filterFactory);
		// 文字渲染器设置
		textRenderer = new BestFitTextRenderer();
		textRenderer.setBottomMargin(2);
		textRenderer.setTopMargin(2);
		configurableCaptchaService.setTextRenderer(textRenderer);
		// 验证码图片的大小
		configurableCaptchaService.setWidth(100);
		configurableCaptchaService.setHeight(40);
	}

	/**
	 * @ClassName: MyCustomBackgroundFactory
	 * @Description: 自定义验证码图片背景,主要画一些噪点和干扰线
	 */
	private class MyCustomBackgroundFactory implements BackgroundFactory {
		private Random random = new Random();
		public void fillBackground(BufferedImage image) {
			Graphics graphics = image.getGraphics();
			// 验证码图片的宽高
			int imgWidth = image.getWidth();
			int imgHeight = image.getHeight();
			// 填充为白色背景
			graphics.setColor(new Color(255, 255, 255));
			graphics.fillRect(0, 0, imgWidth, imgHeight);
			// 画100个噪点(颜色及位置随机)
			for (int i = 0; i < 100; i++) {
				// 随机颜色
				int rInt = random.nextInt(255);
				int gInt = random.nextInt(255);
				int bInt = random.nextInt(255);
				graphics.setColor(new Color(rInt, gInt, bInt));
				// 随机位置
				int xInt = random.nextInt(imgWidth - 1);
				int yInt = random.nextInt(imgHeight);
				// 随机旋转角度
				int sAngleInt = random.nextInt(360);
				int eAngleInt = random.nextInt(360);
				// 随机大小
				int wInt = random.nextInt(6);
				int hInt = random.nextInt(6);
				graphics.fillArc(xInt, yInt, wInt, hInt, sAngleInt, eAngleInt);
				// 画5条干扰线
				if (i % 20 == 0) {
					int xInt2 = random.nextInt(imgWidth);
					int yInt2 = random.nextInt(imgHeight);
					graphics.drawLine(xInt, yInt, xInt2, yInt2);
				}
			}
		}
	}

	/**
	 * @Title: setResponseHeaders
	 * @Description: 设置header
	 * @return: void
	 */
	public static void setResponseHeaders(HttpServletResponse response) {
		response.setContentType("image/png");
		response.setHeader("Cache-Control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		long time = System.currentTimeMillis();
		response.setDateHeader("Last-Modified", time);
		response.setDateHeader("Date", time);
		response.setDateHeader("Expires", time);
	}
	
	/**
	 * @Title: getImgCode
	 * @Description: 生成图片验证码，并返回
	 * @return: String
	 */
	public static String getImgCode(final HttpServletRequest request, final HttpServletResponse response, final String deviceNo) {
		OutputStream outputStream = null;
		String imgCode = null;
		try {
			if (null == configurableCaptchaService) {
				new PatchcaImageCode().init();
			}
			outputStream = response.getOutputStream();
			setResponseHeaders(response);
			// 得到验证码对象,有验证码图片和验证码字符串
			imgCode = EncoderHelper.getChallangeAndWriteImage(configurableCaptchaService, "png", response.getOutputStream());
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			logger.error("生成图片验证码异常", e);
		} finally {
			if (null != outputStream) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return imgCode;
	}
}
