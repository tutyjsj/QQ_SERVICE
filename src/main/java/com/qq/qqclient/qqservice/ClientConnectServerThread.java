package com.qq.qqclient.qqservice;

import com.qq.qqcommon.Message;
import com.qq.qqcommon.MessageType;

import java.io.*;
import java.net.Socket;

/**
 * 客户端连接服务器线程
 */
public class ClientConnectServerThread extends Thread {
    // 文件传输相关状态变量
    private FileOutputStream fileOutputStream = null;
    private long totalReceived = 0;
    private long fileSize = 0;
    private String currentFilePath = null;

    // 该线程需要持有Socket
    private Socket socket;

    // 构造器可以接受一个Socket对象
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // 因为Thread需要在后台和服务器通信，因此我们while循环
        while (true) {
            try {
                System.out.println("客户端线程，等待从读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务器没有发送Message对象,线程会阻塞在这里
                Message message = (Message) ois.readObject();

                // 判断消息类型并做相应的业务处理
                switch (message.getMesType()) {
                    case MessageType.MESSAGE_RET_ONLINE_FRIEND:
                        handleOnlineFriendList(message);
                        break;
                    case MessageType.MESSAGE_COMM_MES:
                        handlePrivateMessage(message);
                        break;
                    case MessageType.MESSAGE_TO_ALL_MES:
                        handleGroupMessage(message);
                        break;
                    case MessageType.MESSAGE_FILE_MES:
                        handleFileMessage(message);
                        break;
                    default:
                        System.out.println("是其他类型的message, 暂时不处理....");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理在线用户列表消息
     */
    private void handleOnlineFriendList(Message message) {
        // 取出在线列表信息，并显示
        String[] onlineUsers = message.getContent().split(" ");
        System.out.println("\n=======当前在线用户列表========");
        for (String user : onlineUsers) {
            System.out.println("用户: " + user);
        }
    }

    /**
     * 处理私聊消息
     */
    private void handlePrivateMessage(Message message) {
        // 把从服务器转发的消息，显示到控制台即可
        System.out.println("\n" + message.getSender()
                + " 对 " + message.getGetter() + " 说: " + message.getContent());
    }

    /**
     * 处理群发消息
     */
    private void handleGroupMessage(Message message) {
        // 显示在客户端的控制台
        System.out.println("\n" + message.getSender() + " 对大家说: " + message.getContent());
    }

    /**
     * 处理文件消息
     */
    private void handleFileMessage(Message message) {
        try {
            // 第一次收到文件块时初始化
            if (fileOutputStream == null) {
                String destPath = message.getDest();
                File destFile = new File(destPath);

                // 处理目录路径
                if (destFile.isDirectory()) {
                    String fileName = new File(message.getSrc()).getName();
                    destPath = destPath + File.separator + fileName;
                    destFile = new File(destPath);
                }

                // 确保父目录存在
                File parentDir = destFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    boolean dirsCreated = parentDir.mkdirs();
                    if (!dirsCreated) {
                        System.err.println("警告：无法创建目录 - " + parentDir.getAbsolutePath());
                    }
                }

                fileOutputStream = new FileOutputStream(destPath);
                totalReceived = 0;
                fileSize = message.getFileSize();
                currentFilePath = destPath;

                System.out.println("\n开始接收文件: " + message.getSrc());
                System.out.println("文件大小: " + formatFileSize(fileSize));
                System.out.println("保存到: " + destPath);
            }

            // 写入数据块
            fileOutputStream.write(message.getFileBytes());
            totalReceived += message.getBytesRead();

            // 显示进度
            printProgress(totalReceived, fileSize);

            // 检查文件是否传输完成
            if (totalReceived >= fileSize) {
                fileOutputStream.close();
                System.out.println("\n✅ 文件接收完成: " + message.getSrc());

                // 重置状态
                fileOutputStream = null;
                totalReceived = 0;
                fileSize = 0;
                currentFilePath = null;
            }
        } catch (IOException e) {
            System.err.println("文件接收失败: " + e.getMessage());
            e.printStackTrace();

            // 发生错误时重置状态
            resetFileTransferState();
        }
    }

    /**
     * 重置文件传输状态
     */
    private void resetFileTransferState() {
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        fileOutputStream = null;
        totalReceived = 0;
        fileSize = 0;
        currentFilePath = null;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }

    /**
     * 显示传输进度
     */
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

    // 为了更方便的得到Socket
    public Socket getSocket() {
        return socket;
    }
}
//}
//
//package com.qq.qqclient.qqservice;
//
//import com.qq.qqcommon.Message;
//import com.qq.qqcommon.MessageType;
//
//import java.io.*;
//        import java.net.Socket;
//
//import java.io.ObjectInputStream;
//import java.net.Socket;
//
///**
// */
//public class ClientConnectServerThread extends Thread {
//    // 在类中添加实例变量
//    private FileOutputStream fileOutputStream = null;
//    private long totalReceived = 0;
//    private long fileSize = 0;
//    private File currentFile = null;
//    //该线程需要持有Socket
//    private Socket socket;
//
//    //构造器可以接受一个Socket对象
//    public ClientConnectServerThread(Socket socket) {
//        this.socket = socket;
//    }
//
//    //
//    @Override
//    public void run() {
//        //因为Thread需要在后台和服务器通信，因此我们while循环
//        while (true) {
//
//            try {
//                System.out.println("客户端线程，等待从读取从服务器端发送的消息");
//                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//                //如果服务器没有发送Message对象,线程会阻塞在这里
//                Message message = (Message) ois.readObject();
//                //注意，后面我们需要去使用message
//                //判断这个message类型，然后做相应的业务处理
//                //如果是读取到的是 服务端返回的在线用户列表
//                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
//                    //取出在线列表信息，并显示
//                    //规定
//                    String[] onlineUsers = message.getContent().split(" ");
//                    System.out.println("\n=======当前在线用户列表========");
//                    for (int i = 0; i < onlineUsers.length; i++) {
//                        System.out.println("用户: " + onlineUsers[i]);
//                    }
//
//                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {//普通的聊天消息
//                    //把从服务器转发的消息，显示到控制台即可
//                    System.out.println("\n" + message.getSender()
//                            + " 对 " + message.getGetter() + " 说: " + message.getContent());
//                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
//                    //显示在客户端的控制台
//                    System.out.println("\n" + message.getSender() + " 对大家说: " + message.getContent());
//                }
//                else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {//如果是文件消息
//                    //让用户指定保存路径。。。
//                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter()
//                            + " 发文件: " + message.getSrc() + " 到我的电脑的目录 " + message.getDest());
//
//                    //取出message的文件字节数组，通过文件输出流写出到磁盘
//                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest(), true);
//                    fileOutputStream.write(message.getFileBytes());
//                    fileOutputStream.close();
//                    System.out.println("\n 保存文件成功~");
//
//                } else {
//                    System.out.println("是其他类型的message, 暂时不处理....");
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //为了更方便的得到Socket
//    public Socket getSocket() {
//        return socket;
//    }
//}
