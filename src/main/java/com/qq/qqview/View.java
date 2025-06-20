package com.qq.qqview;

import com.qq.qqclient.qqservice.FileClientService;
import com.qq.qqclient.qqservice.MessageClientService;
import com.qq.qqclient.qqservice.UserClientService;
import com.qq.utils.Utility;

/**
 * 客户端的菜单界面
 */
public class View {

    private boolean loop = true; //控制是否显示菜单
    private String key = ""; // 接收用户的键盘输入
    private final UserClientService userClientService = new UserClientService();//对象是用于登录服务/注册用户
    private final MessageClientService messageClientService = new MessageClientService();//对象用户私聊/群聊.
    private final FileClientService fileClientService = new FileClientService();//该对象用户传输文件

    public static void main(String[] args) {
        new View().mainMenu();
        System.out.println("客户端退出系统.....");
    }

    //显示主菜单
    private void mainMenu() {

        while (loop) {

            System.out.println("===========欢迎登录网络通信系统===========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            //根据用户的输入，来处理不同的逻辑
            switch (key) {
                case "1":
                    System.out.print("请输入用户号: ");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密  码: ");
                    String pwd = Utility.readString(50);
                    //这里就比较麻烦了, 需要到服务端去验证该用户是否合法
                    //这里有很多代码, 我们这里编写一个类 UserClientService[用户登录/注册]
                    if (userClientService.checkUser(userId, pwd)) { //还没有写完, 先把整个逻辑打通....
                        System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
                        //进入到二级菜单
                        while (loop) {
                            System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择: ");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    //这里老师准备写一个方法，来获取在线用户列表
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("请输入想对大家说的话: ");
                                    String s = Utility.readString(100);
                                    messageClientService.sendMessageToAll(s, userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号(在线): ");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入想说的话: ");
                                    String content = Utility.readString(100);
                                    //编写一个方法，将消息发送给服务器端
                                    messageClientService.sendMessageToOne(content, userId, getterId);
                                    break;
                                case "4":
                                    System.out.print("请输入你想把文件发送给的用户(在线用户): ");
                                    getterId = Utility.readString(50);
                                    System.out.print("请输入发送文件的路径(形式 d:\\xx.jpg)");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入把文件发送到对应的路径(形式 d:\\yy.jpg)");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src,dest,userId,getterId);
                                    break;
                                case "9":
                                    //调用方法，给服务器发送一个退出系统的message
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }

                        }
                    } else { //登录服务器失败
                        System.out.println("=========登录失败=========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }

        }

    }
}
//package com.qq.qqview;
//
//import com.qq.qqclient.qqservice.FileClientService;
//import com.qq.qqclient.qqservice.MessageClientService;
//import com.qq.qqclient.qqservice.UserClientService;
//import com.qq.utils.Utility;
//
//import java.io.IOException;
//import java.net.ConnectException;
//import java.net.Socket;
//
///**
// * 客户端的菜单界面
// */
//public class View {
//
//    private boolean loop = true; //控制是否显示菜单
//    private String key = ""; // 接收用户的键盘输入
//    private UserClientService userClientService = new UserClientService();//对象是用于登录服务/注册用户
//    private MessageClientService messageClientService = new MessageClientService();//对象用户私聊/群聊.
//    private FileClientService fileClientService = new FileClientService();//该对象用户传输文件
//
//    public static void main(String[] args) {
//        // 客户端只启动菜单界面
//        new View().mainMenu();
//        System.out.println("客户端退出系统.....");
//    }
//
//    // 添加服务器配置常量
//    private static final String SERVER_IP = "127.0.0.1";
//    private static final int SERVER_PORT = 9999;
//
//    // 检查服务器连接的方法
//    private boolean checkServerAvailable() {
//        int maxRetries = 3;
//        int retryDelay = 3000; // 3秒
//
//        for (int i = 1; i <= maxRetries; i++) {
//            try (Socket testSocket = new Socket(SERVER_IP, SERVER_PORT)) {
//                System.out.println(">>> 服务器连接成功 <<<");
//                return true;
//            } catch (ConnectException e) {
//                System.out.println("\n!!! 连接尝试失败 (" + i + "/" + maxRetries + ") !!!");
//                System.out.println("错误详情: " + e.getMessage());
//
//                if (i < maxRetries) {
//                    System.out.println("等待 " + (retryDelay/1000) + "秒后重试...");
//                    try {
//                        Thread.sleep(retryDelay);
//                    } catch (InterruptedException ie) {
//                        Thread.currentThread().interrupt();
//                    }
//                } else {
//                    System.out.println("可能原因:");
//                    System.out.println("1. 服务器程序未启动");
//                    System.out.println("2. 端口号配置错误（当前: " + SERVER_PORT + ")");
//                    System.out.println("3. 防火墙阻止了端口 " + SERVER_PORT);
//                    System.out.println("4. 服务器IP配置错误（当前: " + SERVER_IP + ")");
//                    System.out.println("5. 服务器绑定到错误地址");
//                    System.out.println("请检查以上问题后重试\n");
//                }
//            } catch (IOException e) {
//                System.out.println("网络错误: " + e.getMessage());
//                return false;
//            }
//        }
//        return false;
//    }
//
//    //显示主菜单
//    private void mainMenu() {
//        while (loop) {
//            System.out.println("\n===========欢迎登录网络通信系统===========");
//            System.out.println("\t\t 1 登录系统");
//            System.out.println("\t\t 2 注册用户");
//            System.out.println("\t\t 3 检查服务器连接"); // 新增服务器检查选项
//            System.out.println("\t\t 0 退出系统");
//            System.out.print("请输入你的选择: ");
//            key = Utility.readString(1);
//
//            switch (key) {
//                case "1":
//                    if (checkServerAvailable()) {
//                        handleLogin();
//                    }
//                    break;
//                case "2":
//                    if (checkServerAvailable()) {
//                        handleRegister();
//                    }
//                    break;
//                case "3": // 新增服务器检查功能
//                    if (checkServerAvailable()) {
//                        System.out.println(">>> 服务器连接正常 <<<");
//                    }
//                    break; // 添加break防止case穿透
//                case "0":
//                    loop = false;
//                    break;
//                default:
//                    System.out.println("输入有误，请重新输入(1, 2, 3 或 0)");
//            }
//        }
//    }
//
//    // 修改登录方法
//    private void handleLogin() {
//        System.out.print("请输入用户号: ");
//        String userId = Utility.readString(50);
//        System.out.print("请输入密  码: ");
//        String pwd = Utility.readString(50);
//
//        try {
//            if (userClientService.checkUser(userId, pwd)) {
//                System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
//                showSecondaryMenu(userId);
//            } else {
//                System.out.println("=========登录失败，用户名或密码错误=========");
//            }
//        } catch (Exception e) {
//            System.out.println("登录过程中发生错误: " + e.getMessage());
//        }
//    }
//
//    // 显示二级菜单
//    private void showSecondaryMenu(String userId) {
//        boolean innerLoop = true;
//        while (innerLoop) {
//            System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
//            System.out.println("\t\t 1 显示在线用户列表");
//            System.out.println("\t\t 2 群发消息");
//            System.out.println("\t\t 3 私聊消息");
//            System.out.println("\t\t 4 发送文件");
//            System.out.println("\t\t 9 退出系统");
//            System.out.print("请输入你的选择: ");
//            key = Utility.readString(1);
//
//            switch (key) {
//                case "1":
//                    userClientService.onlineFriendList();
//                    break;
//                case "2":
//                    handleGroupMessage(userId);
//                    break;
//                case "3":
//                    handlePrivateMessage(userId);
//                    break;
//                case "4":
//                    handleFileTransfer(userId);
//                    break;
//                case "9":
//                    userClientService.logout();
//                    innerLoop = false;
//                    break;
//                default:
//                    System.out.println("输入有误，请重新输入(1-4, 9)");
//            }
//        }
//    }
//
//    // 处理群发消息
//    private void handleGroupMessage(String userId) {
//        System.out.println("请输入想对大家说的话: ");
//        String s = Utility.readString(100);
//        messageClientService.sendMessageToAll(s, userId);
//    }
//
//    // 处理私聊消息
//    private void handlePrivateMessage(String userId) {
//        System.out.print("请输入想聊天的用户号(在线): ");
//        String getterId = Utility.readString(50);
//        System.out.print("请输入想说的话: ");
//        String content = Utility.readString(100);
//        messageClientService.sendMessageToOne(content, userId, getterId);
//    }
//
//    // 处理文件传输
//    private void handleFileTransfer(String userId) {
//        System.out.print("请输入你想把文件发送给的用户(在线用户): ");
//        String getterId = Utility.readString(50);
//        System.out.print("请输入发送文件的路径(形式 d:\\xx.jpg): ");
//        String src = Utility.readString(100);
//        System.out.print("请输入把文件发送到对应的路径(形式 d:\\yy.jpg): ");
//        String dest = Utility.readString(100);
//        fileClientService.sendFileToOne(src, dest, userId, getterId);
//    }
//
//    // 修改注册方法
//    private void handleRegister() {
//        System.out.println("\n===========用户注册===========");
//        System.out.print("请输入用户号: ");
//        String newUserId = Utility.readString(50);
//        System.out.print("请输入密码: ");
//        String newPwd = Utility.readString(50);
//        System.out.print("请再次输入密码: ");
//        String confirmPwd = Utility.readString(50);
//
//        if (!newPwd.equals(confirmPwd)) {
//            System.out.println("两次输入的密码不一致，请重新输入！");
//            return;
//        }
//
//        try {
//            boolean success = userClientService.registerUser(newUserId, newPwd);
//            if (success) {
//                System.out.println("===========注册成功===========");
//                System.out.println("用户 " + newUserId + " 已成功注册，请使用该账号登录");
//            } else {
//                System.out.println("===========注册失败===========");
//                System.out.println("可能原因：用户已存在");
//            }
//        } catch (Exception e) {
//            System.out.println("注册过程中发生错误: " + e.getMessage());
//        }
//    }
//}