package com.power.filter.utils;

import java.io.File;
import java.util.Hashtable;
import java.util.UUID;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.power.platform.common.config.Global;

public class QRCodeUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String initRrCode(String url,String name) throws Exception{
        int width = 400;  
        int height = 400;  
        String format = "png";
        String path =System.getProperty("user.dir")+File.separator+"src/main/webapp/static/image"+File.separator+name+"."+format;
		Hashtable hints= new Hashtable();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height,hints);  
        File outputFile = new File(path);  
        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile); 
        System.out.println("It is ok!");
        return path;
	}
	
	public static void main(String[] args) {
		try {
			initRrCode(null,UUID.randomUUID().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String initQRCode(String url,String name) throws Exception{
        int width = 400;  
        int height = 400;  
        String format = "png";
        String path =Global.getConfig("upload_file_path")+File.separator+name+"."+format;
		Hashtable hints= new Hashtable();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height,hints);  
        File outputFile = new File(path);  
        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile); 
        System.out.println("It is ok!");
        return path;
	}
}
