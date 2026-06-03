package com.sh.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class MyUtil {
	
	private int numPerBlock = 10;
	
	public MyUtil()
	{
		
	}
	
	
	public MyUtil(int numPerBlock)
	{
		this.numPerBlock = numPerBlock;
	}

   /**
    * 전체 페이지 수 구하기
    * 
    * @param dataCount 총 데이터 갯수
    * @param size      한 화면에 출력할 목록 갯수
    * @return 전체 페이지 수
    */
   public int pageCount(int dataCount, int size) {
      int result = 0;

      if (dataCount <= 0 || size <= 0) {
         return result;
      }

      result = dataCount / size + (dataCount % size > 0 ? 1 : 0);

      return result;
   }

   /**
    * 페이징(paging) 처리(GET 방식)
    * 
    * @param currentPage 현재 표시되는 페이지 번호
    * @param totalPage   전체 페이지 수
    * @param listUrl     링크를 설정할 주소
    * @return 페이지 처리 결과
    */
   public String paging(int currentPage, int totalPage, String listUrl) {
      StringBuffer result = new StringBuffer();

      
      int currentPageSetup;
      int page, n;

      // if (currentPage==0)
      if (currentPage < 1 || totalPage < currentPage || listUrl == null)
         return "";

      // listUrl += listUrl.contains("?") != -1 ? "&" : "?";
      listUrl += listUrl.contains("?") ? "&" : "?";

      // currentPageSetup : 표시할 첫 페이지 -1
      currentPageSetup = (currentPage / numPerBlock) * numPerBlock;

      if (currentPage % numPerBlock == 0)
         currentPageSetup = currentPageSetup - numPerBlock;

      result.append("<div class='paginate'>");

      // 처음 페이지, 이전(10페이지 전)
      n = currentPage - numPerBlock;
      if (totalPage > numPerBlock && currentPageSetup > 0)
      {
         //result.append("<a href=''>" + "<<" + "</a>");
         //result.append(createLinkUrl(listUrl, 1, "<<"));
         result.append(createLinkUrl(listUrl, 1, "&#x226A"));      // "<<"
         
         //result.append("<a href=''>" + "<" + "</a>");
         //result.append(createLinkUrl(listUrl, n, "<"));
         result.append(createLinkUrl(listUrl, n, "&#x003C"));      // "<"
      }
      
      // 페이징
      page = currentPageSetup + 1;
      while ((page<=totalPage) && (page<=currentPageSetup+numPerBlock))
      {
         if (page==currentPage)
            result.append("<span>" + page + "</span>");
         else
            //result.append("<a href=''>" + page + "</a>");
            result.append(createLinkUrl(listUrl, page, String.valueOf(page)));
         page++;
      }
      
      // 다음(10페이지 후), 마지막 페이지
      n = currentPage + numPerBlock;
      if (n > totalPage)
         n = totalPage;
      if (totalPage - currentPageSetup > numPerBlock)
      {
         //result.append("<a href=''>" + ">" + "</a>");
         //result.append(createLinkUrl(listUrl, n, ">"));
         result.append(createLinkUrl(listUrl, n, "&#x003E"));         // ">"
         
         //result.append("<a href=''>" + ">>" + "</a>");
         //result.append(createLinkUrl(listUrl, totalPage, ">>"));
         result.append(createLinkUrl(listUrl, totalPage, "&#x226B"));   // ">>"
      }
      
      result.append("</div>");

      return result.toString();
   }
   
   /**
    * 자바스크립트로 페이징(paging) 처리 : javascript 지정 함수 호출
    * 
    * @param currentPage 현재 표시되는 페이지 번호
    * @param totalPage   전체 페이지 수
    * @param methodName  호출할 자바스크립트 함수 이름
    * @return 페이지 처리 결과
    */
   public String pagingMethod(int currentPage, int totalPage, String methodName) {
      StringBuffer result = new StringBuffer();

      
      int currentPageSetup;
      int page, n;

      // if (currentPage==0)
      if (currentPage < 1 || totalPage < currentPage)
         return "";

      //listUrl += listUrl.contains("?") ? "&" : "?";

      // currentPageSetup : 표시할 첫 페이지 -1
      currentPageSetup = (currentPage / numPerBlock) * numPerBlock;

      if (currentPage % numPerBlock == 0)
         currentPageSetup = currentPageSetup - numPerBlock;

      result.append("<div class='paginate'>");                  /* .paginate */

      // 처음 페이지, 이전(10페이지 전)
      n = currentPage - numPerBlock;
      if (totalPage > numPerBlock && currentPageSetup > 0)
      {
         //result.append("<a onclick='...'>...</a>");
         result.append(createLinkClick(methodName, 1, "&#x226A"));   // "<<"
         
         //result.append(createLinkUrl(listUrl, n, "<"));
         result.append(createLinkClick(methodName, n, "&#x003C"));      // "<"
      }
      
      // 페이징
      page = currentPageSetup + 1;
      while ((page<=totalPage) && (page<=currentPageSetup+numPerBlock))
      {
         if (page==currentPage)
            result.append("<span>" + page + "</span>");
         else
            //result.append("<a href=''>" + page + "</a>");
            result.append(createLinkClick(methodName, page, String.valueOf(page)));
         page++;
      }
      
      // 다음(10페이지 후), 마지막 페이지
      n = currentPage + numPerBlock;
      if (n > totalPage)
         n = totalPage;
      if (totalPage - currentPageSetup > numPerBlock)
      {
         //result.append(createLinkUrl(listUrl, n, "&#x003E"));         // ">"
         result.append(createLinkClick(methodName, n, "&#x003E"));         // ">"
         
         //result.append(createLinkUrl(listUrl, totalPage, "&#x226B"));   // ">>"
         result.append(createLinkClick(methodName, totalPage, "&#x226B"));   // ">>"
      }
      
      result.append("</div>");

      return result.toString();
   }
   
   protected String createLinkUrl(String url, int page, String label)
   {
      return "<a href='"+ url +"page="+ page +"'>" + label + "</a>";
   }
   
   protected String createLinkClick(String methodName, int page, String label)
   {
      return "<a onclick='"+ methodName +"("+ page +");'>" + label + "</a>";
   }
   
   /**
    * 문자열을 주소형식으로 인코딩
    * 
    * @param str   인코딩할 대상 문자열
    * @return   주소 형식으로 인코딩된 문자열
    */
   public String encodeurl(String str)
   {
      if (str == null)
      {
         return null;
      }
      
      try
      {
         str = URLEncoder.encode(str, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e)
      {
         e.printStackTrace();
      }
      
      return str;
   }
   
   /**
    * 주소 형식의 문자열을 디코딩
    * 
    * @param str   디코딩을 수행할 인코딩된 대상 문자열
    * @return   디코딩된 문자열
    */
   public String decodeUrl(String str)
   {
      Pattern pattern = Pattern.compile(".*%[0-9a-fA-F]{2}.*");
      
      if (str==null)
         return null;
      
      try
      {
         if (!str.contains("%"))      // "%" 가 없으면 디코딩 하지 않아도 됨
            return str;
         
         // 인코딩 패턴이 유효한 경우에만 디코딩을 시도할 수 있도록 처리
         if (pattern.matcher(str).matches())
         {
            // 디코딩
            return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
         }
         
      }
      // 잘못된 "%" 인코딩 형식("%" 는 있지만 2자리 16진수가 아닌 경우)이 존재하는 경우
      catch (IllegalArgumentException e)
      {
         e.printStackTrace();
      }
      // 지원되지 않는 인코딩일 경우
      catch (UnsupportedEncodingException e)
      {
         e.printStackTrace();
      }
      
      return str;
   }
   
   /**
    * 특수문자를 HTML 문자로 변경 및 엔터를 <br> 로 변경
    * 
    * @param str   변경할 대상 문자열
    * @return   HTML 문자로 변경된 문자열
    */
   public String htmlSymbols(String str)
   {
      if (str==null || str.length()==0)
      {
         return "";
      }
      
      str = str.replaceAll("&", "&amp;");
      str = str.replaceAll("\"", "&quot;");
      str = str.replaceAll(">", "&gt;");
      str = str.replaceAll("<", "&lt;");
      
      str = str.replaceAll("\n", "<br>");
      str = str.replaceAll("\\s", "&nbsp;");   // check~!!!
      //-- 『\\s』 가 엔터도 변경하기 때문에 『\n』 처리보다 뒤에 기술할 것~!!!
      
      return str;
   }
   
   /**
    * 이메일(E-Mail) 형식 검사
    * 
    * @param email   검사할 대상 E-mail 
    * @return E-mail 유효성 검사 결과
    */
   public boolean isValidEmail(String email)
   {
      if (email==null)
      {
         return false;
      }
      
      return Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+", email.trim());
   }
   
}
