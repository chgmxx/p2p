package com.power.platform.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.power.platform.common.config.Global;

public class PdfUtils {
	
	private static final String FILE_PATH=Global.getConfig("pdf.template.name");
	
	private static final String OUT_PATH=Global.getConfig("pdf.out.path");
	
	private static final String LIN_FILE_PATH =Global.getConfig("pdf.lin.path");
	
	private static final String ZTMG_IMAGE=Global.getConfig("pdf.ztmg.images");
	
	 private static final String UNIT = "万千佰拾亿千佰拾万千佰拾元角分";  
    private static final String DIGIT = "零壹贰叁肆伍陆柒捌玖";  
    private static final double MAX_VALUE = 9999999999999.99D;

	private static OutputStream fos; 
	
	/**
	 * 更具模版生成带插入表格的pdf 定期投资
	 * @param templateName 模版名称（模版都必须放在 resources下的template/pdf下 ）
	 * @param Map data key是pdf模版文件的域的名称 value会显示在域的位置
	 * @param title 附标题
	 * @param Image 担保机构公章可以为空
	 * @param rowTitle String[] rowTitle=new String[]{"标题1","标题2","标题3","标题4","标题5"};
	 * @param dataList 
	 * @return  合同pdf地址
	 * @throws Exception
	 * 
		调用demo
			
		 String[] rowTitle=new String[]{"标题1","标题2","标题3","标题4","标题5"};
		Map<String, String> map =new HashMap<String, String>();
		map.put("contract_no", DateUtils.getDateStr());
		map.put("project_name", "恒大房产项目001(一期）");
		map.put("project_no", "测试项目测试项目001试项目001试项目001");
		map.put("rmb_da", "壹千万壹千万壹千万壹千万壹千万壹千万");
		map.put("uses", "干什么用去了");
		map.put("rmb", "2000000.0");
				String[] data=new String[]{"数据1","数据2","数据3","数据4","数据5"};
		String[] data1=new String[]{"数据6","数据7","数据8","数据9","数据0"};
		String[] data2=new String[]{"数据11","数据12","数据13","数据14","数据15"};
         List<String[]> dataList =new ArrayList<String[]>();
         dataList.add(data);
         dataList.add(data1);
         dataList.add(data2);
		String pdfPath =PdfUtils.createPdfByTemplate("pdf_template.pdf", map,"测试标题",rowTitle,dataList, "");
		System.out.println(pdfPath);
	 */
	public static String createPdfByTemplate(String templateName,Map<String, String> data,String title,String []rowTitle,List<String[]> dataList,String images, List<String[]> investList) throws Exception {
		
		String folderName =OUT_PATH+DateUtils.getFileDate();
		String newFileName=UUID.randomUUID().toString().replace("-", "")+".pdf";
		
		String linName =Global.getUserfilesBaseDir()+LIN_FILE_PATH+"hetong.pdf";
		String linNameTables =Global.getUserfilesBaseDir()+LIN_FILE_PATH+"tables.pdf";	// 投资本人还款计划表
		String investListTable = "";
		if(investList != null){
			investListTable = Global.getUserfilesBaseDir()+LIN_FILE_PATH+"invests.pdf";	// 出借人本金利息表
		}
		
		
			FileUtils.createDirectory(FileUtils.path(folderName));
			PdfReader reader = new PdfReader(FILE_PATH+templateName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfStamper ps = new PdfStamper(reader, bos);
			
			PdfContentByte pdfContentByte = ps.getOverContent(6);
			//待完善 start=====================
			//担保公司签章
			if(StringUtils.isNotBlank(images)){
				Image img = Image.getInstance(Global.getUserfilesBaseDir()+images);
				img.scaleToFit(280, 180);
				img.setAbsolutePosition(180,430);
				pdfContentByte.addImage(img); 
			}
			//中投摩根签章
			Image img2 = Image.getInstance(Global.getUserfilesBaseDir()+ZTMG_IMAGE);
			img2.scaleToFit(280,180);
			img2.setAbsolutePosition(200,250);
			pdfContentByte.addImage(img2); 
			//待完善 end=====================
			
			AcroFields fields = ps.getAcroFields();
			fillData(fields, data);
			ps.setFormFlattening(true);
			ps.close();
			fos = new FileOutputStream(FileUtils.path(linName));
			fos.write(bos.toByteArray());
			
			PdfGenerateTables.generateAllParts(title,rowTitle,dataList,linNameTables);							//创建表格pdf
			
			String[] files = null;
			if(investList != null){
				String[] investArr = new String[]{"编号", "出借人", "身份证", "出借金额", "利息总额"};
				PdfGenerateTables.generateAllParts("附表：出借人本金利息表",investArr,investList,investListTable);		//创建表格pdf
				files = new String[]{linName, linNameTables, investListTable};	
			} else {
				files = new String[]{linName, linNameTables};	
			}
			
			
			MergeFileUtils.mergePdfFiles(files, FileUtils.path(folderName+File.separator+newFileName));
			
		return FileUtils.path(OUT_PATH+DateUtils.getFileDate()+File.separator+newFileName);
	}
	
	/**
	 * 根据pdf模版生产合同 债券转让
	 * @param templateName 模版名称（模版都必须放在 resources下的template/pdf下 ）
	 * @param Map data key是pdf模版文件的域的名称 value会显示在域的位置
	 * @return 生成pdf的路径位置
	  	调用 demo
	   根据模版生成合同
	  	Map<String, String> map =new HashMap<String, String>();
		map.put("transfer_person", "曹智");
		map.put("assignee_person", "李云");
		map.put("year", "2015");
		map.put("month", "12");
		map.put("day", "30");
		map.put("contract_no", "NO_"+DateUtils.getCurrentDateTimeStr());
		map.put("transfer_amout", "3000");
		map.put("username", "18518757286");
		map.put("vip_no", UUID.randomUUID().toString());
		map.put("term_date", "180");
		map.put("start_date", DateUtils.getDateBefore());
		map.put("end_date", DateUtils.getDateStr());
		map.put("year_interest", "8");
		map.put("back_date", "12");
		map.put("back_mode", "还本付息");
		String pdfPath =PdfUtils.createPdfByTemplate("win11_trans_demand.pdf", map);
		System.out.println(pdfPath);
	 * @throws Exception 
	 *
	 */
	public static String createTransfer(String templateName,Map<String, String> data) throws Exception {
			PdfReader reader = new PdfReader(FILE_PATH+templateName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfStamper ps = new PdfStamper(reader, bos);
			
			PdfContentByte pdfContentByte = ps.getOverContent(3);
			//中投摩根签章
			Image img2 = Image.getInstance(Global.getUserfilesBaseDir()+ZTMG_IMAGE);
			img2.scaleToFit(280,180);
			img2.setAbsolutePosition(200,240);
			pdfContentByte.addImage(img2); 
			
			AcroFields fields = ps.getAcroFields();
			fillData(fields, data);
			ps.setFormFlattening(true);
			ps.close();
			String newFileName=UUID.randomUUID().toString().replace("-", "")+".pdf";
			String folderName =Global.getUserfilesBaseDir()+OUT_PATH+DateUtils.getFileDate()+"-huo";
			FileUtils.createDirectory(FileUtils.path(folderName));
			fos = new FileOutputStream(FileUtils.path(folderName+File.separator+newFileName));
			fos.write(bos.toByteArray());
		return FileUtils.path(OUT_PATH+DateUtils.getFileDate()+newFileName);
	}
	
	/**
	 * 向pdf的域中赋值
	 * @param fields
	 * @param data 根据key 将域的值更为value的值
	 * @throws IOException
	 */
	public static void fillData(AcroFields fields, Map<String, String> data)
			throws Exception{
		for (String key : data.keySet()) {
			String value = data.get(key);
			fields.setField(key, value);
		}
	}
	
	/**
	 * 小写数字转大写
	 * @param v
	 * @return
	 */
   public static String change(double v) {  
      if (v < 0 || v > MAX_VALUE){  
          return "参数非法!";  
      }  
      long l = Math.round(v * 100);  
      if (l == 0){  
          return "零元整";  
      }  
      String strValue = l + "";  
      // i用来控制数  
      int i = 0;  
      // j用来控制单位  
      int j = UNIT.length() - strValue.length();  
      String rs = "";  
      boolean isZero = false;  
      for (; i < strValue.length(); i++, j++) {  
       char ch = strValue.charAt(i);  
       if (ch == '0') {  
        isZero = true;  
        if (UNIT.charAt(j) == '亿' || UNIT.charAt(j) == '万' || UNIT.charAt(j) == '元') {  
         rs = rs + UNIT.charAt(j);  
         isZero = false;  
        }  
       } else {  
        if (isZero) {  
         rs = rs + "零";  
         isZero = false;  
        }  
        rs = rs + DIGIT.charAt(ch - '0') + UNIT.charAt(j);  
       }  
      }  
      if (!rs.endsWith("分")) {  
       rs = rs + "整";  
      }  
      rs = rs.replaceAll("亿万", "亿");  
      return rs;  
     }  
     
     public static void main(String[] args) {
		 PdfUtils.change(12356789.9845);
		  //整数
	        System.out.println( PdfUtils.change(0));              // 零元整
	        System.out.println( PdfUtils.change(123));            // 壹佰贰拾叁元整
	        System.out.println( PdfUtils.change(1033000));        // 壹佰万元整
	        System.out.println( PdfUtils.change(100000001));      // 壹亿零壹元整
	        System.out.println( PdfUtils.change(1000000000));     // 壹拾亿元整
	        System.out.println( PdfUtils.change(1234567890));     // 壹拾贰亿叁仟肆佰伍拾陆万柒仟捌佰玖拾元整
	        System.out.println( PdfUtils.change(1001100101));     // 壹拾亿零壹佰壹拾万零壹佰零壹元整
	        System.out.println( PdfUtils.change(110101010));      // 壹亿壹仟零壹拾万壹仟零壹拾元整
	     
	        //小数
	        System.out.println("================");
	        System.out.println( PdfUtils.change(0.94));          // 壹角贰分
	        System.out.println( PdfUtils.change(123.34));        // 壹佰贰拾叁元叁角肆分
	        System.out.println( PdfUtils.change(1000000.56));    // 壹佰万元伍角陆分
	        System.out.println( PdfUtils.change(100000001.78));  // 壹亿零壹元柒角捌分
	        System.out.println( PdfUtils.change(1000000000.90)); // 壹拾亿元玖角
	        System.out.println( PdfUtils.change(1234567890.03)); // 壹拾贰亿叁仟肆佰伍拾陆万柒仟捌佰玖拾元叁分
	        System.out.println( PdfUtils.change(1001100101.00)); // 壹拾亿零壹佰壹拾万零壹佰零壹元整
	        System.out.println( PdfUtils.change(110101010.10));  // 壹亿壹仟零壹拾万壹仟零壹拾元壹角
	      //负数
	        System.out.println( PdfUtils.change(-0.12));          // 负壹角贰分
	        System.out.println( PdfUtils.change(-123.34));        // 负壹佰贰拾叁元叁角肆分
	        System.out.println( PdfUtils.change(-1000000.56));    // 负壹佰万元伍角陆分
	        System.out.println( PdfUtils.change(-100000001.78));  // 负壹亿零壹元柒角捌分
	        System.out.println( PdfUtils.change(-1000000000.90)); // 负壹拾亿元玖角
	        System.out.println( PdfUtils.change(-1234567890.03)); // 负壹拾贰亿叁仟肆佰伍拾陆万柒仟捌佰玖拾元叁分
	        System.out.println( PdfUtils.change(-1001100101.00)); // 负壹拾亿零壹佰壹拾万零壹佰零壹元整
	        System.out.println( PdfUtils.change(-110101010.10));  // 负壹亿壹仟零壹拾万壹仟零壹拾元壹角
	}
     
	
}
