package kr.tracom.platform.launcher.http.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import kr.tracom.platform.launcher.TLauncher;
import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import kr.tracom.platform.net.config.TimsConfig;

@Path("/ftp")
public class FtpController extends ChannelController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TimsConfig timsConfig = TLauncher.getInstance().getTimsConfig();


    @POST
    @Path("/ftpTest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String ftpTest(@FormParam("dir") String directory
    					, @FormParam("ip") String ip
    					, @FormParam("id") String id
    					, @FormParam("pwd") String pwd) throws Exception {
    	FTPFile[] files = ftpFileList(directory, ip, id, pwd);
    	if(files == null) {
    		return JsonBuilder.getJson(false);
    	}else {
    		return JsonBuilder.getJson(files);
    		
    	}
    }
    
    @POST
    @Path("/ftpRename")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String ftpRename(@FormParam("dir") String directory,
    						@FormParam("oldName") String oldName,
    						@FormParam("newName") String newName,
    						@FormParam("ip") String ip,
    						@FormParam("id") String id,
    						@FormParam("pwd") String pwd) throws Exception {
    	boolean result = ftpFileRename(oldName, newName, directory, ip, id, pwd);
		return JsonBuilder.getJson(result);
    }
    
    @POST
    @Path("/ftpDelete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String ftpDelete(@FormParam("deleteList") String deleteList,
    					  	@FormParam("directory") String directory,
    					  	@FormParam("ip") String ip,
    					  	@FormParam("id") String id,
    					  	@FormParam("pwd") String pwd) throws Exception {
    					
    	String[] arr_deleteList = deleteList.split("<>");
    	for(String s : arr_deleteList) {
    		System.out.println(s);
    	}
    	boolean result = ftpFileDelete(arr_deleteList, directory, ip, id, pwd);
    	return JsonBuilder.getJson(result);
    }
    
    
    @POST
    @Path("/ftpUpload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String upload(
    					@FormDataParam("file") InputStream uploadedInputStream
            			,@FormDataParam("file") FormDataContentDisposition fileDetail
            			,@FormDataParam("directory") String dir
            			,@FormDataParam("ip") String ip
            			,@FormDataParam("id") String id
            			,@FormDataParam("pwd") String pwd
            			) throws Exception {
    	
        //String fileName = new String(fileDetail.getFileName().getBytes("8859_1"),"utf-8");
    	boolean result = ftpFileUpload(dir, ip, id, pwd, uploadedInputStream, fileDetail);
    	if(result) {
    		return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);    		
    	}else {
    		return JsonBuilder.getJson(JsonBuilder.FAIL_CD);
    	}
    }
    
    @POST
    @Path("/ftpDownload")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String download(@FormParam("dir") String dir
    					  ,@FormParam("ip") String ip
    					  ,@FormParam("id") String id
    					  ,@FormParam("pwd") String pwd
    					  ,@FormParam("fileName") String fileName
    					  ,@FormParam("downPath") String downPath) throws IOException {
    	
    	System.out.println("in download");
    	boolean result = ftpFileDownLoad(dir, ip, id, pwd, fileName, downPath);
    	return JsonBuilder.getJson(result);
    }
    
    @POST
    @Path("/mkdirRemote")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String makeRemote(@FormParam("remote") String remote
    						,@FormParam("ip") String ip
    						,@FormParam("id") String id
    						,@FormParam("pwd") String pwd) throws Exception {
    	boolean result = mkdirRemote(remote, ip, id, pwd);
    	return JsonBuilder.getJson(result);
    }
    
    
    public FTPClient ftpCon(String ip, String id, String pwd) throws Exception{
    	FTPClient client = new FTPClient();
    	client.setControlEncoding("euc-kr");//한글쓰려면 인코딩설정 해줘야함
    	client.connect(ip);
    	client.login(id, pwd);
    	return client;
    }
    
    //조회
    public FTPFile[] ftpFileList(String directory, String ip, String id, String pwd) throws Exception {
    	/*String server = "175.196.234.39";
    	String username = "tracom_bit";
    	String password = "bit56%^";
    	String directory = "/IMP/";*/
    	
    	FTPClient client = null;
    	try {
    		client = ftpCon(ip, id, pwd);
	    	FTPFile[] files = client.listFiles(directory);
	    	return files;
    	}catch(Exception e){
    		return null;
    	}
    	finally {
	    	if(client.isConnected()) {
	    		client.disconnect();
	    	}
    	}
    }
    
    //생성
    //파일 복사
    //파일다운로드
    public boolean ftpFileDownLoad(String dir, String ip, String id, String pwd, String fileName, String downPath) throws IOException {
    	FTPClient client = null;
    	OutputStream file = null;
    	boolean result = false;

    	String remote = dir + "/" + fileName;
    	String local = downPath+"/"+fileName;
    	char last = downPath.charAt(downPath.length()-1); 
    	System.out.println(last);
    	try {
    		client = ftpCon(ip, id, pwd);
    		
    		File getFile = new File(local);
    		file = new FileOutputStream(getFile);
    		result = client.retrieveFile(remote, file);
    		
    		
    		file.close();
    	
    		System.out.println(file + " : " + result);
    	}catch(Exception e) {
    		
    	}
    	finally {
    		if(client.isConnected()) {
    			client.disconnect();
    		}
    	}
    	return result;
    }
    
    //파일업로드
    public boolean ftpFileUpload(String dir, String ip, String id, String pwd, InputStream file, FormDataContentDisposition fileDetail) throws Exception {
    	boolean result = true;
    	FTPClient client = null;
    	String remote = "";
    	try {
    		client = ftpCon(ip, id, pwd);
    		String fileName = new String(fileDetail.getFileName().getBytes("8859_1"), StandardCharsets.UTF_8);
    		remote = dir+fileName;
    		result = client.appendFile(remote, file);

    	}
    	finally {
	    	if(client.isConnected()) {
	    		client.disconnect();
	    	}
    	}
    	
    	return result;
    }
    
    public boolean folderDownload(String remote, String local, FTPClient client) {
		return false;
    	
    }
    
    //원격으로 폴더생성
    public boolean mkdirRemote(String remote, String ip, String id, String pwd) throws Exception {
    	boolean result = false;
    	FTPClient client = null;
		try {
			client = ftpCon(ip, id, pwd);
			result = client.makeDirectory(remote);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(client.isConnected()) {
				client.disconnect();
			}
		}
		return result;
    }
    //이름변경
    public boolean ftpFileRename(String oldName, String newName, String directory, String ip, String id, String pwd) throws Exception {
    	FTPClient client = ftpCon(ip, id, pwd);

    	boolean result = client.rename(directory+"/"+oldName, directory+"/"+newName);
    
    	if(client.isConnected()) {
    		client.disconnect();
    	}
    	
    	return result;
    }
    
    //삭제
    public boolean ftpFileDelete(String[] deleteList, String directory, String ip, String id, String pwd) throws Exception {
    	FTPClient client = ftpCon(ip, id, pwd);
    	boolean temp = false;
    	boolean result = true;
    	
    	FTPFile[] allFile = client.listFiles(directory);
    	List<FTPFile> file_deleteList = new ArrayList<FTPFile>();

    	//FTPFile test = client.mlistFile(directory+allFile[i].getName());
    	//이게 안되서 포문돌려서 FTPFile객체로 리스트 다시만들었음
    	//7월 11일의 나는 디렉토리인지 확인하려면 파일 정보를 다 가져올수밖에 없었음
    	for(int i=0; i<allFile.length; i++) {    		
    		for(int j=0; j<deleteList.length; j++) {
    			if(allFile[i].getName().equals(deleteList[j])) {
    				file_deleteList.add(allFile[i]);
    			}
    		}
    	}
    	
    	for(FTPFile file : file_deleteList) {
    		if(file.isFile()) {
    			temp = client.deleteFile(directory+file.getName());
    		}
    		else if(file.isDirectory()) {
    			temp = client.removeDirectory(directory+file.getName());
    			if(!temp) {
    				deleteFolder(directory+file.getName(), client);
    				//안에있는걸 비워줄거임
    			}
    		}
    	}

    	if(client.isConnected()) {
    		client.disconnect();
    	}
    	return result;
    	
    }

	public static void deleteFolder(String path, FTPClient client) throws IOException {
		FTPFile[] file_inDirFile = client.listFiles(path);
		String finalPath;
		try {
			for(int i = 0; i < file_inDirFile.length; i++) {
				finalPath = path + "/" + file_inDirFile[i].getName();
				if(file_inDirFile[i].isFile()) {
					client.deleteFile(finalPath);
				}else {
					boolean temp = client.removeDirectory(finalPath);
					if(!temp) {//안에 뭔가 있다는거임
						deleteFolder(finalPath, client);//재귀
					}
				}
			}
			client.removeDirectory(path);
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
}
