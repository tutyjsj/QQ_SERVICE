package com.qq.qqserver.service;

import com.qq.qqcommon.Message;
import com.qq.qqcommon.MessageType;
import com.qq.qqcommon.User;
import com.qq.qqserver.service.ManageClientThreads;
import com.qq.qqserver.service.SendNewsToAllService;
import com.qq.qqserver.service.ServerConnectClientThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是服务器, 在监听9999，等待客户端的连接，并保持通信
 */
public class QQServer {

    private ServerSocket ss = null;
    //创建一个集合，存放多个用户，如果是这些用户登录，就认为是合法
    //这里我们也可以使用 ConcurrentHashMap, 可以处理并发的集合，没有线程安全
    //HashMap 没有处理线程安全，因此在多线程情况下是不安全
    //ConcurrentHashMap 处理的线程安全,即线程同步处理, 在多线程情况下是安全
    private static final ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    //private static ConcurrentHashMap<String, ArrayList<Message>> offLineDb = new ConcurrentHashMap<>();

    static { //在静态代码块，初始化 validUsers

        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("至尊宝宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞姐姐", new User("紫霞仙子", "123456"));
        validUsers.put("菩提老头", new User("菩提老祖", "123456"));

    }

    //验证用户是否有效的方法
    private boolean checkUser(String userId, String passwd) {

        User user = validUsers.get(userId);
        //过关的验证方式
        if(user == null) {//说明userId没有存在validUsers 的key中
            return  false;
        }
        //userId正确，但是密码错误
        return user.getPasswd().equals(passwd);
    }

    public QQServer() {
        //注意：端口可以写在配置文件.
        try {
            System.out.println("服务端在9999端口监听...");
            //启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();
            ss = new ServerSocket(9999);

            while (true) { //当和某个客户端连接后，会继续监听, 因此while
                Socket socket = ss.accept();//如果没有客户端连接，就会阻塞在这里
                //得到socket关联的对象输入流
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());

                //得到socket关联的对象输出流
                ObjectOutputStream oos =
                        new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();//读取客户端发送的User对象
                //创建一个Message对象，准备回复客户端
                Message message = new Message();
                //验证用户 方法
                if (checkUser(u.getUserId(), u.getPasswd())) {//登录通过
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCESS);
                    //将message对象回复客户端
                    oos.writeObject(message);
                    //创建一个线程，和客户端保持通信, 该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, u.getUserId());
                    //启动该线程
                    serverConnectClientThread.start();
                    //把该线程对象，放入到一个集合中，进行管理.
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);

                } else { // 登录失败
                    System.out.println("用户 id=" + u.getUserId() + " pwd=" + u.getPasswd() + " 验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //如果服务器退出了while，说明服务器端不在监听，因此关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}//package com.qq.qqserver.service;
//
//import com.qq.qqcommon.User;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class QQServer {
//
//    private ServerSocket ss = null;
//    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
//
//    static {
//        // 添加用户"1" 密码为MD5("1") = c4ca4238a0b923820dcc509a6f75849b
//        validUsers.put("1", new User("1", "c4ca4238a0b923820dcc509a6f75849b"));
//
//        validUsers.put("100", new User("100", "123456"));
//        validUsers.put("200", new User("200", "123456"));
//        validUsers.put("300", new User("300", "123456"));
//        validUsers.put("至尊宝宝", new User("至尊宝", "123456"));
//        validUsers.put("紫霞姐姐", new User("紫霞仙子", "123456"));
//        validUsers.put("菩提老头", new User("菩提老祖", "123456"));
//    }
//
//    private boolean checkUser(String userId, String receivedPassword) {
//        User user = validUsers.get(userId);
//        if (user == null) {
//            System.out.println("用户不存在: " + userId);
//            return false;
//        }
//
//        // 直接比较接收到的加密密码和存储的加密密码
//        String storedPassword = user.getPassword();
//        boolean isValid = storedPassword.equals(receivedPassword);
//
//        System.out.println("用户验证: " + userId);
//        System.out.println("接收密码: " + receivedPassword);
//        System.out.println("存储密码: " + storedPassword);
//        System.out.println("验证结果: " + isValid);
//
//        return isValid;
//    }
//
//    public QQServer() {
//        try {
//            System.out.println("服务端在9999端口监听...");
//            new Thread(new SendNewsToAllService()).start();
//            ss = new ServerSocket(9999);
//            System.out.println("服务器已启动，等待客户端连接...");
//            System.out.println("ServerSocket状态: " + (ss.isBound() ? "已绑定" : "未绑定"));
//            System.out.println("监听地址: " + ss.getInetAddress() + ":" + ss.getLocalPort());
//
//            while (true) {
//                Socket socket = null;
//                ObjectInputStream ois = null;
//                ObjectOutputStream oos = null;
//
//                try {
//                    socket = ss.accept();
//                    System.out.println("客户端连接: " + socket.getInetAddress());
//                    System.out.println("Socket状态: " + (socket.isConnected() ? "已连接" : "未连接"));
//                    System.out.println("端口: " + socket.getPort());
//
//                    // 创建输入输出流
//                    ois = new ObjectInputStream(socket.getInputStream());
//                    oos = new ObjectOutputStream(socket.getOutputStream());
//                    System.out.println("输入输出流创建成功");
//
//                    System.out.println("等待接收客户端对象...");
//                    Object obj = ois.readObject();
//                    System.out.println("收到对象类型: " + obj.getClass().getName());
//
//                    // ... [后续处理代码] ...
//                } catch (ClassNotFoundException e) {
//                    System.err.println("类未找到异常: " + e.getMessage());
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // 添加详细异常信息
//                    System.err.println("IO异常详情:");
//                    System.err.println("异常类型: " + e.getClass().getName());
//                    System.err.println("错误信息: " + e.getMessage());
//                    System.err.println("可能原因:");
//                    System.err.println("1. 客户端断开连接");
//                    System.err.println("2. 对象序列化不兼容");
//                    System.err.println("3. 网络连接中断");
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    System.err.println("处理客户端请求时出错: ");
//                    e.printStackTrace();
//                } finally {
//                    // 确保资源关闭
//                    try {
//                        if (ois != null) ois.close();
//                        if (oos != null) oos.close();
//                        if (socket != null && !socket.isClosed()) {
//                            socket.close();
//                            System.out.println("连接已关闭");
//                        }
//                    } catch (IOException ex) {
//                        System.err.println("关闭资源失败: " + ex.getMessage());
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("服务器启动失败: " + e.getMessage());
//            e.printStackTrace();
////        } finally {
////
////        }
//        }
//    }
//}