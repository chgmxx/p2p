package com.power.platform.common.utils;

import java.awt.Color;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;



public class PdfGenerateTables {
	public static void main(String[] args) throws Exception {
		String[] rowTitle=new String[]{"标题1","标题2","标题3","标题4","标题5"};
		
		String[] data=new String[]{"数据1","数据2","数据3","数据4","数据5"};
		String[] data1=new String[]{"数据6","数据7","数据8","数据9","数据0"};
		String[] data2=new String[]{"数据11","数据12","数据13","数据14","数据15"};
		
		List<String[]> list =new ArrayList<String[]>();
		
		list.add(data);
		list.add(data1);
		list.add(data2);
		
		generateAllParts("测试标题",rowTitle,list,"");
	}
	/**
	 * 生成带表格数据的pdf
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @param outPath
	 */
	public static void generateAllParts(String title,String []rowTitle,List<String[]> dataList,String outPath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outPath));
            
            // 生成字体
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            // 标题字体
            Font f30 = new Font(bfChinese, 12, Font.BOLD, Color.BLACK);
            // 正文字体
            Font f8 = new Font(bfChinese, 10, Font.NORMAL, Color.BLACK);
            
            document.open();
            
            // 标题
            document.add(new Paragraph(title, f30));
            // 换行
            document.add(new Chunk("\n"));
            // 添加table实例
            PdfPTable table = new PdfPTable(rowTitle.length);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
            PdfPCell cell = new PdfPCell();
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            // 表格标题
            for(int i =0 ;i<rowTitle.length;i++){
            	   cell.setPhrase(new Paragraph(rowTitle[i], f8));
            	   table.addCell(cell);
            }
            // 表格数据
            PdfPCell newcell = new PdfPCell();
            newcell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            for (String[] data:dataList) {
				for (int i = 0; i < data.length; i++) {
					 	newcell.setPhrase(new Paragraph(data[i], f8));
			            table.addCell(newcell);
				}
			}
            document.add(table);
            document.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
