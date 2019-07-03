/*********************************************************************************
 * PROJECT	: CoMis
 * NAME		: com.visionic.util.ExcelDownUtil.java
 * DESC		: 
 * AUTHOR	: 배민기
 * VER		: 1.0
 * Copyright 2018 VISIONIC All rights reserved
 *********************************************************************************
 *				변				경				사				항
 *********************************************************************************
 *		DATE			|		AUTHOR		|		DESCRIPTION 
 *********************************************************************************
 *		2018. 3. 16.	|		배민기			|		최초프로그램 작성
 *********************************************************************************/
package com.visionic.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.visionic.dto.ExcelBean;
import com.visionic.dto.MemberBean;
import com.visionic.dto.UserColInfoBean;
import com.visionic.mybatis.dao.GeneralDaoI;



public class ExcelDownUtil {
	
	static XSSFRow row;
	static XSSFCell cell;
	
	static int MaxLevel;
	static int rownm;
	
	public static XSSFWorkbook getExcelFile(ExcelBean excelBean, List<Map<String, Object>> excelDataList, GeneralDaoI genDao) throws SQLException{
		int colIdx = 0;
		XSSFWorkbook workbook = new XSSFWorkbook();
		rownm = 0;
		MaxLevel=0;
		
		XSSFSheet sheet = workbook.createSheet(excelBean.getTablenm());
		
		
		List<String> colList = excelBean.getColumnList();
		List<String> colNmList = excelBean.getColumnNmList();
		
		UserColInfoBean usrColBean = new UserColInfoBean();		
		usrColBean.setTablenm(excelBean.getTablenm());
		usrColBean.setUserid(excelBean.getUserid());
		
		
		
		XSSFCellStyle colStyle = workbook.createCellStyle();
		XSSFFont colFont = workbook.createFont();
		
		colFont.setColor(new XSSFColor(Color.WHITE));
		colStyle.setFont(colFont);
		colStyle.setLocked(true);
		
		
		//출력 row 생성
		// 1행의 컬럼 ID 입력
		row = sheet.createRow(colIdx++);
		row.setHeight((short) 1);
		for(int i = 0; i < colList.size(); i++){
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(colList.get(i).toUpperCase());
			cell.setCellStyle(colStyle);
		}
		
		rownm++;
		
		
		// 2행의 컬럼 명 입력
		
		
		drawHeader(workbook,sheet,excelBean.getColumnList(),excelBean.getColumnNmList(),excelBean.getParentNmList());

		//데이터 그리기
		drawBody(workbook,sheet,excelDataList,excelBean.getColumnList());
		
		//컬럼 사이즈 자동 조절
		for(int i = 0; i < colNmList.size(); i++){
			sheet.autoSizeColumn(i);
		}

		return workbook;
	}
	
	public static XSSFCellStyle[] setBodyStyle(XSSFWorkbook workbook) {
		XSSFCellStyle[] dataStyles = new XSSFCellStyle[3];
		XSSFCellStyle datacellStyleFirst=workbook.createCellStyle();
		datacellStyleFirst.setBorderRight(BorderStyle.THIN);
		datacellStyleFirst.setBorderTop(BorderStyle.DOUBLE);
		XSSFCellStyle datacellStyleRest=workbook.createCellStyle();
		datacellStyleRest.setBorderRight(BorderStyle.THIN);
		XSSFCellStyle lastRowStyle = workbook.createCellStyle();
		lastRowStyle.setBorderTop(BorderStyle.THIN);
		
		
		dataStyles[0]=datacellStyleFirst;
		dataStyles[1]=datacellStyleRest;
		dataStyles[2]=lastRowStyle;
		
		return dataStyles;
	}
	
	public static XSSFCellStyle[] setHeaderStyle(XSSFWorkbook workbook) {
		XSSFCellStyle[] headStyle = new XSSFCellStyle[MaxLevel+1];
		
		XSSFCellStyle headStyle1 = workbook.createCellStyle();
		headStyle1.setFillForegroundColor(new XSSFColor(Color.LIGHT_GRAY));
		headStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headStyle1.setBorderTop(BorderStyle.THICK);
		headStyle1.setBorderRight(BorderStyle.THIN);
		headStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		headStyle1.setAlignment(HorizontalAlignment.CENTER);
		XSSFFont headFont = workbook.createFont();
		headFont.setFontName("맑은 고딕");
		headFont.setBold(true);
		headFont.setFontHeightInPoints((short)16);
		headStyle1.setFont(headFont);
		
		
		
		XSSFCellStyle headStyle2 = workbook.createCellStyle();
		headStyle2.setFillForegroundColor(new XSSFColor(Color.LIGHT_GRAY));
		headStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headStyle2.setBorderTop(BorderStyle.THIN);
		headStyle2.setBorderRight(BorderStyle.THIN);
		headStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
		headStyle2.setAlignment(HorizontalAlignment.CENTER);
		XSSFFont headFont2 = workbook.createFont();
		headFont2.setFontName("맑은 고딕");
		headFont2.setBold(true);
		headFont2.setFontHeightInPoints((short)16);
		headStyle2.setFont(headFont2);
		
		for(int i=0;i<headStyle.length;i++) {
			headStyle[i]=headStyle2;
			if(i==0) {
				headStyle[i]=headStyle1;
			}
						
		}
		
		return headStyle;
	}
	
	public static XSSFWorkbook getExcelFileForBreakTop(ExcelBean excelBean,List<List<Map<String, Object>>> excelDataList, GeneralDaoI genDao) throws SQLException{
		rownm = 0;
		
		MaxLevel=0;
		
		
		int imgheight=13;
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		XSSFSheet sheet = workbook.createSheet(excelBean.getTablenm());
		

		
		BufferedImage[] images;
		try {
			images = StringtoImage(excelBean);
		
		
			for(int i=0;i<excelBean.getColumnListM().size();i++) {
				drawChart(images[i],workbook,sheet,excelBean.getColumnNmListM().get(i).size(),imgheight);
				drawHeader(workbook,sheet,excelBean.getColumnListM().get(i),excelBean.getColumnNmListM().get(i),excelBean.getParentNmList());				
			    drawBody(workbook,sheet,excelDataList.get(i),excelBean.getColumnListM().get(i));
				
			}
			
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
		for(int i = 0; i < excelBean.getColumnListM().get(0).size(); i++){
			sheet.autoSizeColumn(i);
		}

		
		return workbook;
	}
	
	public static BufferedImage[] StringtoImage(ExcelBean excelBean) throws IOException {
		BufferedImage[] imges= new BufferedImage[excelBean.getImageUrl().size()];
		
		for(int i=0;i<imges.length;i++) {
			BufferedImage img;
			String base64Image =excelBean.getImageUrl().get(i).split(",")[1];
			base64Image = base64Image.replaceAll(" ", "+");
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			imges[i]=img;
		}
		
		return imges;
		
		
	}
	
	
	public static void drawChart(BufferedImage img, XSSFWorkbook workbook,XSSFSheet sheet,int columnNm,int height) throws IOException {
		// 이미지
		ByteArrayOutputStream baps = new ByteArrayOutputStream();
//		img = resize(img, 400, 800);
		ImageIO.write(img, "png", baps);
		int pictureIdx = workbook.addPicture(baps.toByteArray(),Workbook.PICTURE_TYPE_PNG);
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		XSSFCreationHelper helper = workbook.getCreationHelper();
		XSSFClientAnchor anchor = helper.createClientAnchor();
		anchor.setAnchorType(AnchorType.MOVE_AND_RESIZE);
		anchor.setCol1(0);
		 anchor.setCol2(columnNm); 
		anchor.setRow1(rownm);
		anchor.setRow2(rownm+height);
		
		
		Picture picture = drawing.createPicture(anchor, pictureIdx);
//		picture.resize();
//		rownm += img.getHeight() / 20;
		rownm+=height;

	}

	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	
	public static void drawBody(XSSFWorkbook workbook,XSSFSheet sheet,List<Map<String, Object>> excelDataList,List<String> colList) {
		XSSFRow row;
		
		XSSFCellStyle[] datacellStyles=setBodyStyle(workbook);
		
		for(int k=0;k<excelDataList.size();k++){
			
			Map<String, Object> excel=excelDataList.get(k);
			System.out.println(excel+"=");
			row = sheet.createRow(rownm++);
			XSSFCellStyle datacellStyle=datacellStyles[1];
			if(k==0) {
				 datacellStyle=datacellStyles[0];
			}
			for(int i = 0; i < colList.size(); i++){
				Object data = excel.get(colList.get(i).toUpperCase());
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(data != null ? data.toString() : "");
				
				cell.setCellStyle(datacellStyle);

			}
		}
		
		//마지막 밑에 굵은줄 표시하기
		row = sheet.createRow(rownm++);
	
		for(int i = 0; i < colList.size(); i++){
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(datacellStyles[2]);
		}
				
		
	}
	/**
	 * ExcelBean에 엑셀 다운로드에 필요한 정보 세팅
	 * @param request
	 * @param excelBean
	 * @param genDao
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, Object> setExtraExcelBeanData(HttpServletRequest request, ExcelBean excelBean, GeneralDaoI genDao) throws SQLException{
		Map<String, Object> bean = new HashMap<String, Object>();
		
		HttpSession session = request.getSession(false);
		MemberBean member = (MemberBean)session.getAttribute("member");
		
		// 사용자 사이트 및 언어코드 입력
		bean.put("siteid", member.getSiteid());
		bean.put("langCode", member.getLangCode());
		bean.put("customId", member.getCustomId());
		
		// where 절에 입력할 조회 조건 추가
		bean = setWhereData(excelBean, bean);
		
		return bean;
	}
	
	/**
	 * Where에 넣을 데이터 입력
	 * @param excelBean
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> setWhereData(ExcelBean excelBean, Map<String, Object> bean){
		Map<String, Object> rtnBean = bean;
		String params = excelBean.getExtraParams();
		
		if(params != null && !"".equals(params)){
//			System.out.println("extraParams is not null");
			
			params = params.replace("\"", "");
			String[] paramArr = params.split(",");
			
			for(String paramMap : paramArr){
				paramMap = paramMap.replace("{", "").replace("}", "").trim();
				if(paramMap != null && !"".equals(paramMap)){
					String[] keyVal = paramMap.split(":");
					String key = keyVal[0].trim();
					
					if(!isNumber(key) && keyVal.length > 1){
						String value = keyVal[1].trim();
//					System.out.println("Param : " + key + " / " + value);
						rtnBean.put(key, value);
					}
				}
			}
		}
		
//		System.out.println("@@@ setWhereData : " + rtnBean);
		
		return rtnBean;
	}
	
	/**
	 * 숫자 확인 함수
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str){
		boolean result = false;
		
		try{
			Double.parseDouble(str);
			result = true;
		} catch(Exception e){
			result = false;
		}
		
		return result;
	}

	
	public static XSSFSheet drawHeader(XSSFWorkbook workbook,XSSFSheet sheet,List<String> columnList,List<String> columnNmList,List<String> parentNmList) {
		List<Header> headerlist=getHeaderlist(columnList,columnNmList,parentNmList);
		 getMaxLevel(headerlist);

		XSSFRow[] rows = new XSSFRow[MaxLevel+1];	
		for(int i=0;i<rows.length;i++) {	
			rows[i]=sheet.createRow(i+rownm);
		}
		
		int[] cellsindx= new int[MaxLevel+1];
	
		drawthroughrecur(sheet,rows,cellsindx,headerlist,0,setHeaderStyle(workbook));
		
		rownm+=MaxLevel+1;
		return sheet;
	}
	public static void getMaxLevel(List<Header> headerlist) {
		
		for(Header header:headerlist) {
			MaxLevelrecur(header,0);
		}
		
	}
	
	public static void MaxLevelrecur(Header header,int level) {
		if(level>MaxLevel) {
			MaxLevel=level;
		}
		if(header.children.size()==0) {
			System.out.println(header.id+"본인"+header.name+"본인"+header.children.size());
			return;
		}
		
		for(Header child:header.children) {
			System.out.println(header.id+"부모"+header.name+"부모"+child.id+"자식"+child.name+"자식");
			MaxLevelrecur(child,level+1);
			
			
		}
	}
	
	
	public static List<Header> getHeaderlist(List<String> columnList,List<String> columnNmList,List<String> parentNmList){
		
		
		List<Header> headerTree=new ArrayList();
		Map<String,Header> headertemp=new HashMap();
		
//		List<String> columnList=excelBean.getColumnList();
//		List<String> columnNmList=excelBean.getColumnNmList();
//		List<String> parentNmList=excelBean.getParentNmList();
		
		for(int i=0;i<columnNmList.size();i++) {
			
			
			String name=columnNmList.get(i);
			String id=columnList.get(i);
			
			Header header=new Header(name,id);
			
			if(parentNmList!=null&&!"".equals(parentNmList.get(i))){
				
				Header parentheader=new Header(parentNmList.get(i),parentNmList.get(i));
			
				headertemp.put(parentNmList.get(i),parentheader);
			}
			
			headertemp.put(id,header);
			
			
		}
		
		for(int i=0;i<columnList.size();i++) {
			Header header=headertemp.get(columnList.get(i));
			
			if(parentNmList==null) {
				headerTree.add(header);
				continue;
			}
			
			String parent=parentNmList.get(i);
			
			if(!"".equals(parent)){
				
				
				Header parentNode=headertemp.get(parent);
				parentNode.children.add(header);
				if(!headerTree.contains(parentNode)) {
					headerTree.add(parentNode);
				}
				
			}else {
				headerTree.add(header);
			}
			
			
			
			
		}
		
		return headerTree;
		
	}
	
	public static void drawthroughrecur(XSSFSheet sheet,XSSFRow[] rows,int [] cellsindx,List<Header> headerlist,int level,XSSFCellStyle[] headStyle) {

		if(headerlist.size()==0) {
			return ;
			}
			
		for(Header child:headerlist) {
			System.out.println(level+","+child.name+","+child.children.size());
				drawthroughrecur(sheet,rows,cellsindx,child.children,level+1,headStyle);

				System.out.println(MaxLevel);
				System.out.println(level);
				
				XSSFCell cell = rows[level].createCell(cellsindx[level]++);
				cell.setCellValue(child.name);

				CellRangeAddress mergedCell = null;
				
				
				if(child.children.size()==0) {
					
					if(MaxLevel>level) {
						
						for(int i=level+1;i<=MaxLevel;i++) {
							cellsindx[i]++;
						}
					
						mergedCell=new CellRangeAddress(rownm+level,rownm+MaxLevel,cellsindx[level]-1,cellsindx[level]-1);
						sheet.addMergedRegion(mergedCell);
					}
				
				}else {	
				
					mergedCell=new CellRangeAddress(rownm+level,rownm+level,cellsindx[level]-1,cellsindx[level+1]-1);
					sheet.addMergedRegion(mergedCell);
					cellsindx[level]=cellsindx[level+1];
				
				}
	
				
				if(mergedCell!=null) {
					
					RegionUtil.setBorderTop(BorderStyle.THICK, mergedCell, sheet); 
					RegionUtil.setBorderRight(BorderStyle.THIN, mergedCell, sheet);
					
					if(level==0) {
						RegionUtil.setBorderTop(BorderStyle.THICK, mergedCell, sheet); 
				}
				 
					 
				}
				cell.setCellStyle(headStyle[level]);
				}
	
			

		
	}

// 입력값이 제대로 들어올시 사용바람
    //excelbean nodelist<head>={id:,namd:,parentid:,}...이런식으로 들어와야함
//public static List<Header> getHeaderlist(ExcelBean excelBean){
//	
//	List<Header> headerTree=new ArrayList();
//	Map<String,Header> headertemp=new HashMap();
//	
//
//	
// 	List<Header> nodelist=excelBean.getNodeList();
// 	
// 	for(Header header:nodelist) {
// 		
// 		
// 		if(header.parentId=="") {
// 			headerTree.add(header);
// 		}else {
// 			
// 			for(Header header2:nodelist) {
// 				if(header2.id.equals(header.parentId)) {
// 					header2.children.add(header);
// 					
// 				}
// 				
// 			}
// 		}
// 	}
//
//
//	
//	return headerTree;
//	
//}

	
}
