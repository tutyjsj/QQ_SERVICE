package com.qq.qqclient.qqservice;

import com.qq.qqclient.qqservice.ManageClientConnectServerThread;
import com.qq.qqcommon.Message;
import com.qq.qqcommon.MessageType;

import java.io.*;

public class FileClientService {
    private static final int BUFFER_SIZE = 8192; // 8KB 缓冲区

    public void sendFileToOne(String src, String dest, String senderId, String getterId) {
        File file = new File(src);
        if (!file.exists() || !file.isFile()) {
            System.out.println("⚠️ 源文件不存在或不是文件: " + src);
            return;
        }

        long fileSize = file.length();
        System.out.println("\n开始发送文件: " + src);
        System.out.println("文件大小: " + formatFileSize(fileSize));
        System.out.println("发送给: " + getterId);
        System.out.println("目标位置: " + dest);

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            long totalRead = 0;
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];

            while ((bytesRead = bis.read(buffer)) != -1) {
                Message message = new Message();
                message.setMesType(MessageType.MESSAGE_FILE_MES);
                message.setSender(senderId);
                message.setGetter(getterId);
                message.setSrc(src);
                message.setDest(dest);

                // 设置文件大小和当前读取字节数
                message.setFileSize(fileSize);
                message.setBytesRead(bytesRead);

                // 复制缓冲区数据
                byte[] dataBlock = new byte[bytesRead];
                System.arraycopy(buffer, 0, dataBlock, 0, bytesRead);
                message.setFileBytes(dataBlock);

                // 发送数据块
                sendMessage(message);

                // 更新进度
                totalRead += bytesRead;
                printProgress(totalRead, fileSize);
            }

            System.out.println("\n✅ 文件发送完成: " + src);

        } catch (IOException e) {
            System.err.println("文件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessage(Message message) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(
                        message.getSender()).getSocket().getOutputStream()
        );
        oos.writeObject(message);
        oos.flush();
    }

    private void printProgress(long current, long total) {
        double percent = (double) current / total * 100;
        int progressBars = (int) (percent / 2); // 50个字符的进度条

        StringBuilder sb = new StringBuilder("\r[");
        for (int i = 0; i < 50; i++) {
            sb.append(i < progressBars ? "=" : " ");
        }
        sb.append("] ");
        sb.append(String.format("%.1f%%", percent));
        sb.append(" (");
        sb.append(formatFileSize(current));
        sb.append("/");
        sb.append(formatFileSize(total));
        sb.append(")");

        System.out.print(sb.toString());
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
}
//package com.qq.qqclient.qqservice;
//
//import com.qq.qqclient.qqservice.ManageClientConnectServerThread;
//import com.qq.qqcommon.Message;
//import com.qq.qqcommon.MessageType;
//
//import java.io.*;
//
//
///**
// * 该类/对象完成 文件传输服务
// */
//public class FileClientService {
//    /**
//     *
//     * @param src 源文件
//     * @param dest 把该文件传输到对方的哪个目录
//     * @param senderId 发送用户id
//     * @param getterId 接收用户id
//     */
//    public void sendFileToOne(String src, String dest, String senderId, String getterId) {
//
//        //读取src文件  -->  message
//        Message message = new Message();
//        message.setMesType(MessageType.MESSAGE_FILE_MES);
//        message.setSender(senderId);
//        message.setGetter(getterId);
//        message.setSrc(src);
//        message.setDest(dest);
//
//        //需要将文件读取
//        FileInputStream fileInputStream = null;
//        byte[] fileBytes = new byte[(int)new File(src).length()];
//
//        try {
//            fileInputStream = new FileInputStream(src);
//            fileInputStream.read(fileBytes);//将src文件读入到程序的字节数组
//            //将文件对应的字节数组设置message
//            message.setFileBytes(fileBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //关闭
//            if(fileInputStream != null) {
//                try {
//                    fileInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        //提示信息
//        System.out.println("\n" + senderId + " 给 " + getterId + " 发送文件: " + src
//                + " 到对方的电脑的目录 " + dest);
//        //发送
//        try {
//            ObjectOutputStream oos =
//                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
//            oos.writeObject(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//}