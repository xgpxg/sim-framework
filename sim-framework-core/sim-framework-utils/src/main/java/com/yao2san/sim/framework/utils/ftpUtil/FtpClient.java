package com.yao2san.sim.framework.utils.ftpUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

@Deprecated
public class FtpClient implements AutoCloseable {

    private FTPClient ftpClient;

    public FtpClient() {

    }

    @Autowired
    public FtpClient(FtpConfig ftpConfig) throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
        ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
        ftpClient.setBufferSize(1024);//设置上传缓存大小
        ftpClient.setControlEncoding("UTF-8");//设置编码
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//设置文件类型
    }

    /**
     * 下载ftp文件到本地
     *
     * @param remoteFileName 远程文件名称
     * @param localFile      本地文件[包含路径]
     * @return true/false
     * @throws IOException 异常
     */
    public boolean downloadFile(String remoteFileName, String localFile) throws IOException {
        boolean isSucc;
        File outFileName = new File(localFile);
        if (ftpClient == null)
            throw new IOException("ftp server not login");
        try (OutputStream outputStream = new FileOutputStream(outFileName)) {
            isSucc = ftpClient.retrieveFile(remoteFileName, outputStream);
        }
        return isSucc;
    }

    /**
     * 上传文件制定目录
     *
     * @param remoteFileName 远程文件名
     * @param localFile      本地文件[必须带路径]
     * @return true/false
     * @throws IOException 异常
     */
    public boolean uploadFile(String remoteFileName, String localFile) throws IOException {
        boolean success;
        try (InputStream inputStream = new FileInputStream(localFile)) {
            if (ftpClient == null)
                throw new IOException("ftp server not login");
            success = ftpClient.storeFile(remoteFileName, inputStream);
        }
        return success;
    }

    public boolean uploadFile(String remoteFileName, InputStream inputStream) throws IOException {
        boolean success;
        if (ftpClient == null)
            throw new IOException("ftp server not login");
        success = ftpClient.storeFile(remoteFileName, inputStream);
        return success;
    }

    /**
     * 切换目录
     *
     * @param path 创建目录
     * @return 创建标志
     * @throws IOException 异常
     */
    public boolean changeDirectory(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }

    /**
     * 创建目录
     *
     * @param path 创建目录
     * @return 创建标志
     * @throws IOException 异常
     */
    public boolean createDirectory(String path) throws IOException {
        return ftpClient.makeDirectory(path);
    }

    /**
     * 自动关闭资源
     */
    @Override
    public void close() throws Exception {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }
}
