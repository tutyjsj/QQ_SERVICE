package com.qq.qqclient.qqservice;

import com.qq.qqclient.qqservice.ClientConnectServerThread;
import com.qq.qqclient.qqservice.ManageClientConnectServerThread;
import com.qq.qqcommon.Message;
import com.qq.qqcommon.MessageType;
import com.qq.qqcommon.User;

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
    private final User u = new User();
    //因为Socket在其它地方也可能使用，因此作出属性
    private Socket socket;

    //根据userId 和 pwd 到服务器验证该用户是否合法
    public boolean checkUser(String userId, String pwd) {
        boolean b = false;
        //创建User对象
        u.setUserId(userId);
        u.setPasswd(pwd);


        try {
            //连接到服务端，发送u对象
            socket = new Socket(InetAddress.getByName("120.26.47.106"), 9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送User对象

            //读取从服务器回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {//登录OK


                //创建一个和服务器端保持通信的线程-> 创建一个类 ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread =
                        new ClientConnectServerThread(socket);
                //启动客户端的线程
                clientConnectServerThread.start();
                //这里为了后面客户端的扩展，我们将线程放入到集合管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;
            } else {
                //如果登录失败, 我们就不能启动和服务器通信的线程, 关闭socket
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;

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
//package com.qq.qqservice;
//
//
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
////完成用户登录的验证
//public class UserClientService {
//
//    //    因为在其他的地方可能会使用User属性所以作为成员变量
//    private User user = new User();
//    //    其他线程也会使用socket变量,所以设置成成员变量
//    private Socket socket;
//    //   验证传入的数据
//    public boolean checkUser(String userId,String pwd){
//        boolean b = false;
//
//        user.setPassword(pwd);
//        user.setUserId(userId);
//
//        try {
//            //与服务器建立链接
//            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
//            //得到ObjectOutputStream对象流,并将用户信息写入，发送出去后，服务端返回返回值
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            oos.writeObject(user);
//            //读取从服务器返回的Message对象
//            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//            Message ms = (Message) ois.readObject();
//            //根据返回来的服务器的信息来判断有没有登录成功，密码账户数据的对比是在服务端进行的
//            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)){//登录ok
//                //创建一个和服务器保持通信的线程 让线程持有Socket 所以创建ClientConnectServerThread
//                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
//                //启动一个客户端线程  执行run方法
//                clientConnectServerThread.start();
//                //将线程放到管理线程的集合当中
//                ManageClientConnectServerThread.addClientConnectServerThread(userId,clientConnectServerThread);
//                b = true;
//            }else {
////                登录失败
//                socket.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return b;
////        return true;
//    }
//
//    //向服务器端请求在线用户列表
//    public void onlineFriendList() {
//
//        //发送一个Message , 类型MESSAGE_GET_ONLINE_FRIEND
//        Message message = new Message();
//        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
//        message.setSender(user.getUserId());
//
//        //发送给服务器
//
//        try {
//            //从管理线程的集合中，通过userId, 得到这个线程对象
//            ClientConnectServerThread clientConnectServerThread =
//                    ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId());
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
//    /**
//     * 注册新用户
//     * @param userId 用户ID
//     * @param password 密码
//     * @return 注册是否成功
//     */
//    public boolean registerUser(String userId, String password) {
//        // 创建注册消息对象
//        Message message = new Message();
//        message.setMesType(MessageType.MESSAGE_REGISTER);
//        message.setSender(userId);
//        message.setContent(password);
//
//        try {
//            // 获取与服务器的连接
//            Socket socket = new Socket("127.0.0.1", 9999);
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            oos.writeObject(message);
//
//            // 获取服务器响应
//            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//            Message response = (Message) ois.readObject();
//
//            // 关闭连接
//            oos.close();
//            ois.close();
//            socket.close();
//
//            // 检查注册结果
//            return response.getMesType().equals(MessageType.MESSAGE_REGISTER_SUCCESS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//    //编写方法，退出客户端，并给服务端发送一个退出系统的message对象
//    public void logout() {
//        Message message = new Message();
//        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
//        message.setSender(user.getUserId());//一定要指定我是哪个客户端id
//
//        //发送message
//        try {
//            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            ObjectOutputStream oos =
//                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
//            oos.writeObject(message);
//            System.out.println(user.getUserId() + " 退出系统 ");
//            System.exit(0);//结束进程
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
