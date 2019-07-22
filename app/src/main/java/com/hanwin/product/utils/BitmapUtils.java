package com.hanwin.product.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 图片压缩的工具类
 * 
 * 是有损压缩的
 */
public class BitmapUtils {
	public static Bitmap createBitmapThumbnail(Bitmap bitMap) {
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		// 设置想要的大小
		int newWidth = 99;
		int newHeight = 99;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
		return newBitMap;
	}

	/**
	 * 质量压缩
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 32) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 质量压缩，生成文件到指定目录
	 * @param outPath
	 * @param maxSize
	 *            设置最大图片大小(kb)
	 * @throws IOException
	 */
	public static void compressAndGenImage(String souPath, String outPath, int maxSize) throws IOException {
		// 将源文件转化为流形式
		FileInputStream inputStream = new FileInputStream(souPath);
		// 通过流形式生成Bitmap
		Bitmap image = BitmapFactory.decodeStream(inputStream);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int options = 80;
		// Store the bitmap into output stream(no compress)
		image.compress(Bitmap.CompressFormat.JPEG, options, os);
		// Compress by loop
		while (os.toByteArray().length / 1024 > maxSize) {
			// Clean up os
			os.reset();
			// interval 10
			options -= 15;
			image.compress(Bitmap.CompressFormat.JPEG, options, os);
		}

		// Generate compressed image file
		FileOutputStream fos = new FileOutputStream(outPath);
		fos.write(os.toByteArray());
		fos.flush();
		fos.close();
	}

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap scaleImage(Bitmap bitmap, int maxWidth, int maxHeight) {
		int w = maxWidth;
		int h = maxHeight;
		if (bitmap.getHeight() > bitmap.getWidth()) {
			w = maxHeight;
			h = maxWidth;
		}

		float sW = (float) w / bitmap.getWidth();
		float sH = (float) h / bitmap.getHeight();

		float scale = Math.max(sW, sH);

		Matrix matrix = new Matrix(); // 矩阵，用于图片比例缩放
		matrix.postScale(scale, scale);

		// 缩放后的BitMap
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBmp;
	}

	/**
	 * 
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
		if (options == null) {
			return -1;
		}
		int width = options.outWidth;
		int height = options.outHeight;
		int sampleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			final int halfWidth = width / 2;
			final int halfHeight = height / 2;
			while ((halfHeight / sampleSize) >= reqHeight && (halfWidth / sampleSize) >= reqWidth) {
				sampleSize *= 2;
			}
			long totalPixels = width / sampleSize * height / sampleSize;
			final long totalReqPixelsCap = reqWidth * reqHeight * 2;
			while (totalPixels > totalReqPixelsCap) {
				sampleSize *= 2;
				totalPixels /= 2;
			}
		}
		return sampleSize;
	}

	public static Bitmap drawViewToBitmap(View view, int width, int height, int downSampling) {
		return drawViewToBitmap(view, width, height, 0f, 0f, downSampling);
	}

	public static Bitmap drawViewToBitmap(View view, int width, int height, float translateX, float translateY, int downSampling) {
		float scale = 1f / downSampling;
		int bmpWidth = (int) (width * scale - translateX / downSampling);
		int bmpHeight = (int) (height * scale - translateY / downSampling);
		Bitmap dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Config.ARGB_8888);
		Canvas c = new Canvas(dest);
		c.translate(-translateX / downSampling, -translateY / downSampling);
		if (downSampling > 1) {
			c.scale(scale, scale);
		}
		view.draw(c);
		return dest;
	}

	public static String saveImg(Bitmap b, String name) throws Exception {
		String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "tender/";
		File mediaFile = new File(path + File.separator + name + ".jpg");
		if (mediaFile.exists()) {
			mediaFile.delete();
		}
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		mediaFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(mediaFile);
		b.compress(Bitmap.CompressFormat.PNG, 100, fos);
		fos.flush();
		fos.close();
		b.recycle();
		b = null;
		System.gc();
		return mediaFile.getPath();
	}

	public static Bitmap base64ToBitmap(String base64Data, Context context) {
		return base64ToBitmap(base64Data, context, DisplayMetrics.DENSITY_HIGH);
	}

	public static Bitmap base64ToBitmap(String base64Data, Context context, int displayMetricsDensity) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (context != null) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			options.inTargetDensity = metrics.densityDpi;
		} else {
			options.inTargetDensity = DisplayMetrics.DENSITY_MEDIUM;
		}
		options.inDensity = displayMetricsDensity;
		options.inScaled = false;
		byte[] imageBytes = Base64.decode(base64Data, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
		return bitmap;
	}

	public static Bitmap base64ToBitmap(String base64Data) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		byte[] imageBytes = Base64.decode(base64Data, Base64.DEFAULT);
		BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
		options.inSampleSize = calculateInSampleSize(options, 720, 1280);
		// options.inSampleSize = 4;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
		return bitmap;
	}

	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	/**
	 * 保存图片
	 * 
	 * @param bitmap
	 */
	public static void saveBitmap(Bitmap bitmap, File f, String... msg) {
		// File f = new
		// File(Environment.getExternalStorageDirectory().getAbsolutePath() +
		// "/cib_location_img.jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			/*
			 * if (msg != null && msg.length > 0) { bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out); } else {
			 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); }
			 */
			out.flush();
			out.close();
			bitmap = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将彩色图转换为纯黑白二色
	 * @return 返回转换好的位图
	 */
	public static Bitmap convertToBlackWhite(Bitmap bmp) {
		int width = bmp.getWidth(); // 获取位图的宽
		int height = bmp.getHeight(); // 获取位图的高
		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				// 分离三原色
				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				// 转化成灰度像素
				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		// 新建图片
		Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
		// 设置图片数据
		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

		Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
		return resizeBmp;
	}

	/**
	 * bitmap转为base64
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				baos.flush();
				baos.close();
				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
