package com.yao2san.sim.framework.utils.ftpUtil;

import com.jcraft.jsch.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Properties;

@Component
public class SftpClient {
    private static final Logger logger = LoggerFactory.getLogger(SftpClient.class);

    private ChannelSftp sftp;

    private boolean isReady = false;

    @Getter
    private FtpConfig config;

    /**
     * 当前工作目录，每次关闭连接要回复到null，因为当前类是单例类
     */
    private String directory = null;

    private Session sshSession;

    @Autowired
    public SftpClient(FtpConfig config) {
        this.config = config;
        // 设置当前工作目录
        directory = config.getRootPath();
    }

    private void setReady() throws Exception {
        try {
            if (!isReady) {
                JSch jsch = new JSch();
                sshSession =
                        jsch.getSession(config.getUsername(), config.getHost(), config.getPort());
                logger.info("Session created.");
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");
                sshSession.setPassword(config.getPassword());
                //sshSession.setConfig("PreferredAuthentications", "password,publickey,keyboard-interactive");
                sshSession.setConfig(sshConfig);
                isReady = true;
            }
            if (sshSession != null && !sshSession.isConnected()) {
                sshSession.connect();
                Channel channel;
                if (config.getType() == FtpConfig.Type.sftp) {
                    channel = sshSession.openChannel("sftp");
                } else {
                    channel = sshSession.openChannel("ftp");
                }
                channel.connect();
                sftp = (ChannelSftp) channel;

            }

        } catch (Exception e) {
            this.close();
            logger.error("sftp连接服务器出错,host:" + config.getHost(), e);
            throw e;
        }
    }

    /**
     * 上传文件
     *
     * @param uploadFile 要上传的文件
     * @param remoteName 远程文件
     */
    public boolean uploadFile(String uploadFile, String remoteName) throws Exception {
        try {
            setReady();
            if (remoteName.contains("/")) {
                String remotePath = remoteName.substring(0, remoteName.lastIndexOf("/"));
                sftp.cd(directory);
                sftp.mkdir(remotePath);
                sftp.cd(directory + "/" + remotePath);
                remoteName = remoteName.substring(remoteName.lastIndexOf("/") + 1);
            } else {
                sftp.cd(directory);
            }
            File file = new File(uploadFile);
            sftp.put(new FileInputStream(file), remoteName);
            return true;
        } catch (Exception e) {
            logger.error("sftp上传文件出错,directory:" + directory, e);
            throw e;
        }
    }

    public boolean uploadFile(String remoteName, InputStream inputStream) throws Exception {
        try {
            setReady();
            sftp.put(inputStream, remoteName);
            return true;
        } catch (Exception e) {
            logger.error("sftp上传文件出错,directory:" + directory, e);
            throw e;
        }
    }

    /**
     * 下载文件
     *
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public boolean downloadFile(String downloadFile, String saveFile) throws Exception {
        try {
            setReady();
            sftp.cd(directory);
            File localFile = new File(saveFile);
            if (!localFile.exists()) {
                if (localFile.getParentFile() != null && !localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }
                localFile.createNewFile();
            }
            sftp.get(downloadFile, new FileOutputStream(localFile));
            return true;
        } catch (Exception e) {
            logger.error("sftp下载文件出错,directory:{}", directory, e);
            throw e;
        }
    }

    /**
     * 删除文件
     *
     * @param deleteFile 要删除的文件
     */
    public boolean removeFile(String deleteFile) throws Exception {
        try {
            setReady();
            sftp.cd(directory);
            sftp.rm(deleteFile);
            return true;
        } catch (Exception e) {
            logger.error("sftp删除文件出错,directory:" + directory, e);
            throw e;
        }
    }

    /**
     * 复制文件
     *
     * @param @param  src
     * @param @param  dst
     * @param @return
     * @param @throws Exception
     * @return boolean
     */
    public boolean copyFile(String src, String dst) throws Exception {
        ByteArrayInputStream bStreams = null;
        try {
            setReady();
            if (!isFileExist(src)) {
                //文件不存在直接反回.
                return false;
            }
            String parentPath = dst.substring(0, dst.lastIndexOf("/"));
            if (!this.isDirExist(parentPath)) {
                createDir(parentPath);
            }
            byte[] srcFtpFileByte = inputStreamToByte(sftp.get(src));
            bStreams = new ByteArrayInputStream(srcFtpFileByte);
            //二进制流写文件
            sftp.put(bStreams, dst);

            return true;
        } catch (Exception e) {
            logger.error("sftp移动文件出错,[src:" + src + ",dst:" + dst + "]", e);
            throw e;
        } finally {
            if (bStreams != null) {
                bStreams.close();
            }
        }
    }

    /**
     * 判断远程文件是否存在
     */
    public boolean isFileExist(String remoteFile) throws SftpException {
        boolean isExitFlag = false;
        // 文件大于等于0则存在文件
        if (getFileSize(remoteFile) >= 0) {
            isExitFlag = true;
        }
        return isExitFlag;
    }

    /**
     * 得到远程文件大小
     *
     * @return 返回文件大小，如返回-2 文件不存在，-1文件读取异常
     */
    public long getFileSize(String remoteFile) throws SftpException {
        long filesize;//文件大于等于0则存在
        try {
            SftpATTRS sftpATTRS = sftp.lstat(remoteFile);
            filesize = sftpATTRS.getSize();
        } catch (Exception e) {
            filesize = -1;//获取文件大小异常
            if (e.getMessage().toLowerCase().equals("no such file")) {
                filesize = -2;//文件不存在
            }
        }
        return filesize;
    }

    /**
     * inputStream类型转换为byte类型
     */
    public byte[] inputStreamToByte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = inputStream.read()) != -1) {
            byteArrayOutputStream.write(ch);
        }
        byte bytes[] = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return bytes;
    }

    /**
     * 创建远程目录
     */
    public void createDir(String remoteFile) throws SftpException {
        sftp.cd("/");
        String split[] = remoteFile.split("/");
        for (String path : split) {
            if (path.equals("")) {
                continue;
            }
            if (isDirExist(path)) {
                sftp.cd(path);
            } else {
                //建立目录
                sftp.mkdir(path);
                //进入并设置为当前目录
                sftp.cd(path);
            }
        }
        sftp.cd(directory);
    }

    /**
     * 判断目录是否存在
     */
    public boolean isDirExist(String directory) throws SftpException {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     */
    public List<?> listFiles() throws Exception {
        setReady();
        return sftp.ls(directory);
    }

    public ChannelSftp getSftp() {
        return sftp;
    }

    public void setSftp(ChannelSftp sftp) {
        this.sftp = sftp;
    }

    public void close() throws IOException {
        if (sftp != null && sftp.isConnected()) {
            sftp.disconnect();
        }
        if (sshSession != null && sshSession.isConnected()) {
            sshSession.disconnect();
        }
        isReady = false;
        logger.info("JSCH session close");
    }
}
