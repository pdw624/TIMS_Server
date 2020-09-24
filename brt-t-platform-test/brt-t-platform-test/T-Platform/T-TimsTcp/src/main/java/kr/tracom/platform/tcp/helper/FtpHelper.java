package kr.tracom.platform.tcp.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpHelper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private FTPClient ftpClient = null;

    public FtpHelper() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
    }

    public boolean open(String host, int port) throws IOException {
        ftpClient.connect(host, port);

        int reply = ftpClient.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            logger.debug("FTP channel refused connection");
            return false;
        }

        ftpClient.setSoTimeout(10000);

        return true;
    }

    public void close() {
        if(ftpClient != null && ftpClient.isConnected()){
            try {
                ftpClient.disconnect();
            }catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void makeDirectory(String path) {
        /*
        if(ftpClient != null && ftpClient.isConnected()) {
            try {
                Debug.log(path);
                ftpClient.makeDirectory(path);
                //printReply(ftpClient);
            } catch (IOException e) {
                e.printStackTrace();
                Debug.log(e.getMessage());
            }
        }
        */
        if(ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpCreateDirectoryTree(ftpClient, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean login(String id, String pw) throws IOException {
        return ftpClient.login(id, pw);
    }

    public void setActiveMode(boolean bActive) {
        if(bActive) {
            ftpClient.enterLocalActiveMode();
        } else {
            ftpClient.enterLocalPassiveMode();
        }
    }

    public boolean upload(String localPath, String remotePath) {
        FileInputStream stream = null;
        boolean result = false;
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File file = new File(localPath);
            stream = new FileInputStream(file);
            //result = ftpClient.appendFile(remotePath, stream);
            result = ftpClient.storeFile(remotePath, stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private boolean download(String localPath, String remotePath) {
        FileOutputStream stream = null;
        boolean result = false;
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File file = new File(localPath);
            stream = new FileOutputStream(file);
            result = ftpClient.retrieveFile(remotePath, stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void printReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("reply : " + aReply);
            }
        }
    }

    private void ftpCreateDirectoryTree(FTPClient client, String dirTree) throws IOException {
        boolean dirExists = true;

        //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
        String[] directories = dirTree.split("/");
        for (String dir : directories ) {
            if (!dir.isEmpty() ) {
                if (dirExists) {
                    dirExists = client.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    if (!client.makeDirectory(dir)) {
                        //throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + client.getReplyString()+"'");
                    }
                    if (!client.changeWorkingDirectory(dir)) {
                        //throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + client.getReplyString()+"'");
                    }
                }
            }
        }
    }
}
