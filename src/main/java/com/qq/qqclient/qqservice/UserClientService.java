package com.qq.qqclient.qqservice;// UserClientService.java - 客户端服务类


import com.qq.qqcommon.Message;
import com.qq.qqcommon.MessageType;
import com.qq.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static org.springframework.jdbc.support.JdbcUtils.closeConnection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证和用户注册等功能.
 */
public class UserClientService {

    //因为我们可能在其他地方用使用user信息, 因此作出成员属性
    private User u = new User();
    //因为Socket在其它地方也可能使用，因此作出属性
    private Socket socket;

public boolean checkUser(String userId, String pwd, String serverIp, int serverPort) { // 新增serverIp和serverPort参数
    boolean b = false;
    u.setUserId(userId);
    u.setPassword(pwd);

    // 使用参数传入的IP和端口
    final String SERVER_IP = serverIp;   // 动态IP
    final int SERVER_PORT = serverPort;  // 动态端口


    Socket socket = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;

    try {
        // 创建带超时的连接
        socket = new Socket();
        socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 5000); // 5秒超时

        // 创建对象流
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(u);
        oos.flush(); // 确保数据发送

        // 获取响应
        ois = new ObjectInputStream(socket.getInputStream());
        Message ms = (Message) ois.readObject();

        if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {
            // 创建并启动通信线程
            ClientConnectServerThread clientThread =
                    new ClientConnectServerThread(socket);
            clientThread.start();

            // 管理线程
            ManageClientConnectServerThread.addClientConnectServerThread(userId, clientThread);
            b = true;
        } else {
            // 登录失败，关闭连接
            closeResources(socket, oos, ois);
        }

    } catch (SocketTimeoutException e) {
        System.err.println("连接超时，请检查网络或服务器状态");
    } catch (ConnectException e) {
        System.err.println("连接被拒绝，服务器可能未启动或端口错误");
    } catch (ClassNotFoundException e) {
        System.err.println("协议错误：无法解析服务器响应");
    } catch (IOException e) {
        System.err.println("网络通信错误：" + e.getMessage());
    } finally {
        // 如果登录失败，确保关闭资源
        if (!b) {
            closeResources(socket, oos, ois);
        }
    }

    return b;
}

    // 辅助方法：安全关闭资源
    private void closeResources(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            if (ois != null) ois.close();
        } catch (IOException e) {
            // 忽略关闭异常
        }

        try {
            if (oos != null) oos.close();
        } catch (IOException e) {
            // 忽略关闭异常
        }

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            // 忽略关闭异常
        }
    }

    //向服务器端请求在线用户列表
    public void onlineFriendList() {

        //发送一个Message , 类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        //发送给服务器

        try {
            //从管理线程的集合中，通过userId, 得到这个线程对象
            ClientConnectServerThread clientConnectServerThread =
                    ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
            //通过这个线程得到关联的socket
            Socket socket = clientConnectServerThread.getSocket();
            //得到当前线程的Socket 对应的 ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message); //发送一个Message对象，向服务端要求在线用户列表
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //编写方法，退出客户端，并给服务端发送一个退出系统的message对象
    public void logout() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定我是哪个客户端id

        //发送message
        try {
            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + " 退出系统 ");
            System.exit(0);//结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
//package com.qq.qqclient.qqservice;
//
//import com.qq.qqclient.qqservice.ClientConnectServerThread;
//import com.qq.qqclient.qqservice.ManageClientConnectServerThread;
//import com.qq.qqcommon.Message;
//import com.qq.qqcommon.MessageType;
//import com.qq.qqcommon.User;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.InetAddress;
//import java.net.Socket;
//
///**
// * 该类完成用户登录验证和用户注册等功能.
// */
//public class UserClientService {
//
//    //因为我们可能在其他地方用使用user信息, 因此作出成员属性
//    private final User u = new User();
//    //因为Socket在其它地方也可能使用，因此作出属性
//    private Socket socket;
//
//    //根据userId 和 pwd 到服务器验证该用户是否合法
//    public boolean checkUser(String userId, String pwd) {
//        boolean b = false;
//        //创建User对象
//        u.setUserId(userId);
//        u.setPasswd(pwd);
//
//
//        try {
//            //连接到服务端，发送u对象
//            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
//            //得到ObjectOutputStream对象
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            oos.writeObject(u);//发送User对象
//
//            //读取从服务器回复的Message对象
//            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//            Message ms = (Message) ois.readObject();
//
//            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {//登录OK
//
//
//                //创建一个和服务器端保持通信的线程-> 创建一个类 ClientConnectServerThread
//                ClientConnectServerThread clientConnectServerThread =
//                        new ClientConnectServerThread(socket);
//                //启动客户端的线程
//                clientConnectServerThread.start();
//                //这里为了后面客户端的扩展，我们将线程放入到集合管理
//                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
//                b = true;
//            } else {
//                //如果登录失败, 我们就不能启动和服务器通信的线程, 关闭socket
//                socket.close();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return b;
//
//    }
//
//    //向服务器端请求在线用户列表
//    public void onlineFriendList() {
//
//        //发送一个Message , 类型MESSAGE_GET_ONLINE_FRIEND
//        Message message = new Message();
//        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
//        message.setSender(u.getUserId());
//
//        //发送给服务器
//
//        try {
//            //从管理线程的集合中，通过userId, 得到这个线程对象
//            ClientConnectServerThread clientConnectServerThread =
//                    ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
//            //通过这个线程得到关联的socket
//            Socket socket = clientConnectServerThread.getSocket();
//            //得到当前线程的Socket 对应的 ObjectOutputStream对象
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            oos.writeObject(message); //发送一个Message对象，向服务端要求在线用户列表
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    //编写方法，退出客户端，并给服务端发送一个退出系统的message对象
//    public void logout() {
//        Message message = new Message();
//        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
//        message.setSender(u.getUserId());//一定要指定我是哪个客户端id
//
//        //发送message
//        try {
//            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            ObjectOutputStream oos =
//                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
//            oos.writeObject(message);
//            System.out.println(u.getUserId() + " 退出系统 ");
//            System.exit(0);//结束进程
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}