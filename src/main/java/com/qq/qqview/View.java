//package com.qq.qqview;
//
//import com.qq.data.UserService;
//import com.qq.qqclient.qqservice.FileClientService;
//import com.qq.qqclient.qqservice.MessageClientService;
//import com.qq.qqclient.qqservice.UserClientService;
//import com.qq.utils.Utility;
//
//import java.io.IOException;
//import java.net.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
///**
// * 客户端的菜单界面（添加局域网服务器发现功能）
// */
//public class View {
//
//    private boolean loop = true; //控制是否显示菜单
//    private String key = ""; // 接收用户的键盘输入
//    private UserClientService userClientService = new UserClientService();//对象是用于登录服务/注册用户
//    private MessageClientService messageClientService = new MessageClientService();//对象用户私聊/群聊.
//    private FileClientService fileClientService = new FileClientService();//该对象用户传输文件
//
//    // 添加默认服务器配置
//    private String defaultServerIp = "60.223.147.56";
//    private int defaultServerPort = 1521;
//
//    // 广播发现相关配置
//    private static final int BROADCAST_PORT = 8888;
//    private static final String DISCOVERY_REQUEST = "QQ_SERVER_DISCOVERY_REQUEST";
//    private static final String DISCOVERY_RESPONSE_PREFIX = "QQ_SERVER_RESPONSE:";
//    private static final long DISCOVERY_TIMEOUT = 2000; // 2秒超时
//
//    public static void main(String[] args) {
//        new View().mainMenu();
//        System.out.println("客户端退出系统.....");
//    }
//
//    // 显示主菜单
//    private void mainMenu() {
//        System.out.println("正在扫描局域网可用服务器...");
//        List<ServerInfo> discoveredServers = discoverServers();
//
//        if (!discoveredServers.isEmpty()) {
//            System.out.println("\n发现以下服务器:");
//            for (int i = 0; i < discoveredServers.size(); i++) {
//                ServerInfo server = discoveredServers.get(i);
//                System.out.printf(" [%d] %s:%d%n", i+1, server.getIp(), server.getPort());
//            }
//
//            System.out.print("\n请选择服务器 (输入序号，或按回车使用默认服务器): ");
//            String choice = Utility.readString(2);
//            if (!choice.isEmpty()) {
//                try {
//                    int index = Integer.parseInt(choice) - 1;
//                    if (index >= 0 && index < discoveredServers.size()) {
//                        ServerInfo selected = discoveredServers.get(index);
//                        defaultServerIp = selected.getIp();
//                        defaultServerPort = selected.getPort();
//                        System.out.println("已选择服务器: " + defaultServerIp + ":" + defaultServerPort);
//                    }
//                } catch (NumberFormatException e) {
//                    // 输入无效，使用默认服务器
//                }
//            }
//        } else {
//            System.out.println("未发现局域网服务器，使用默认服务器配置");
//        }
//
//        while (loop) {
//            System.out.println("\n===========欢迎登录网络通信系统===========");
//            System.out.println("\t\t 1 登录系统");
//            System.out.println("\t\t 2 注册系统");
//            System.out.println("\t\t 3 重新扫描服务器");
//            System.out.println("\t\t 9 退出系统");
//            System.out.print("请输入你的选择: ");
//            key = Utility.readString(1);
//
//            // 根据用户的输入，来处理不同的逻辑
//            switch (key) {
//                case "1":
//                    login();
//                    break;
//                case "2":
//                    // registration();
//                    break;
//                case "3":
//                    scanServers();
//                    break;
//                case "9":
//                    loop = false;
//                    break;
//            }
//        }
//    }
//
//    // 扫描服务器
//    private void scanServers() {
//        System.out.println("\n正在扫描局域网可用服务器...");
//        List<ServerInfo> discoveredServers = discoverServers();
//
//        if (!discoveredServers.isEmpty()) {
//            System.out.println("\n发现以下服务器:");
//            for (int i = 0; i < discoveredServers.size(); i++) {
//                ServerInfo server = discoveredServers.get(i);
//                System.out.printf(" [%d] %s:%d%n", i+1, server.getIp(), server.getPort());
//            }
//
//            System.out.print("\n请选择服务器 (输入序号): ");
//            String choice = Utility.readString(2);
//            if (!choice.isEmpty()) {
//                try {
//                    int index = Integer.parseInt(choice) - 1;
//                    if (index >= 0 && index < discoveredServers.size()) {
//                        ServerInfo selected = discoveredServers.get(index);
//                        defaultServerIp = selected.getIp();
//                        defaultServerPort = selected.getPort();
//                        System.out.println("已选择服务器: " + defaultServerIp + ":" + defaultServerPort);
//                    }
//                } catch (NumberFormatException e) {
//                    System.out.println("输入无效，保持当前服务器配置");
//                }
//            }
//        } else {
//            System.out.println("未发现局域网服务器");
//        }
//    }
//
//    /**
//     * 发现局域网内的服务器
//     * @return 发现的服务器列表
//     */
//    private List<ServerInfo> discoverServers() {
//        List<ServerInfo> servers = new ArrayList<>();
//
//        try (DatagramSocket socket = new DatagramSocket()) {
//            // 设置广播
//            socket.setBroadcast(true);
//            socket.setSoTimeout((int) DISCOVERY_TIMEOUT);
//
//            // 发送广播请求
//            byte[] sendData = DISCOVERY_REQUEST.getBytes();
//            DatagramPacket sendPacket = new DatagramPacket(
//                    sendData,
//                    sendData.length,
//                    InetAddress.getByName("255.255.255.255"),
//                    BROADCAST_PORT
//            );
//            socket.send(sendPacket);
//
//            // 接收响应
//            long startTime = System.currentTimeMillis();
//            while (System.currentTimeMillis() - startTime < DISCOVERY_TIMEOUT) {
//                try {
//                    byte[] recvBuf = new byte[1024];
//                    DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
//                    socket.receive(recvPacket);
//
//                    String message = new String(recvPacket.getData(), 0, recvPacket.getLength()).trim();
//                    if (message.startsWith(DISCOVERY_RESPONSE_PREFIX)) {
//                        String[] parts = message.substring(DISCOVERY_RESPONSE_PREFIX.length()).split(":");
//                        if (parts.length == 2) {
//                            try {
//                                String ip = recvPacket.getAddress().getHostAddress();
//                                int port = Integer.parseInt(parts[0]);
//                                String serverName = parts[1];
//
//                                // 避免重复添加
//                                boolean exists = false;
//                                for (ServerInfo info : servers) {
//                                    if (info.getIp().equals(ip) && info.getPort() == port) {
//                                        exists = true;
//                                        break;
//                                    }
//                                }
//
//                                if (!exists) {
//                                    servers.add(new ServerInfo(ip, port, serverName));
//                                    System.out.println("发现服务器: " + ip + ":" + port + " (" + serverName + ")");
//                                }
//                            } catch (NumberFormatException e) {
//                                // 忽略格式错误的响应
//                            }
//                        }
//                    }
//                } catch (SocketTimeoutException e) {
//                    // 超时结束接收
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("服务器发现失败: " + e.getMessage());
//        }
//
//        return servers;
//    }
//
//    /**
//     * 服务器信息类
//     */
//    private static class ServerInfo {
//        private final String ip;
//        private final int port;
//        private final String name;
//
//        public ServerInfo(String ip, int port, String name) {
//            this.ip = ip;
//            this.port = port;
//            this.name = name;
//        }
//
//        public String getIp() {
//            return ip;
//        }
//
//        public int getPort() {
//            return port;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        @Override
//        public String toString() {
//            return name + " (" + ip + ":" + port + ")";
//        }
//    }
//
//    /**
//     * 登录
//     */
//    private void login() {
//        System.out.println("\n当前服务器: " + defaultServerIp + ":" + defaultServerPort);
//        System.out.print("是否使用当前服务器? (y/n, 默认y): ");
//        String useCurrent = Utility.readString(1);
//
//        if ("n".equalsIgnoreCase(useCurrent)) {
//            System.out.print("请输入服务器IP: ");
//            String serverIp = Utility.readString(20);
//            if (!serverIp.isEmpty()) {
//                defaultServerIp = serverIp;
//            }
//
//            try {
//                System.out.print("请输入服务器端口: ");
//                String portInput = Utility.readString(5);
//                if (!portInput.isEmpty()) {
//                    defaultServerPort = Integer.parseInt(portInput);
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("端口格式错误，使用默认端口 " + defaultServerPort);
//            }
//        }
//
//        System.out.print("请输入用户号: ");
//        String userId = Utility.readString(50);
//        System.out.print("请输入密  码: ");
//        String pwd = Utility.readString(50);
//
//        // 修改为传递四个参数
//        if (userClientService.checkUser(userId, pwd, defaultServerIp, defaultServerPort)) {
//            System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
//
//            // 进入到二级菜单
//            boolean innerLoop = true; // 使用新的变量控制二级菜单循环
//            while (innerLoop) {
//                System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
//                System.out.println("\t\t 1 显示在线用户列表");
//                System.out.println("\t\t 2 群发消息");
//                System.out.println("\t\t 3 私聊消息");
//                System.out.println("\t\t 4 发送文件");
//                System.out.println("\t\t 5 切换服务器");
//                System.out.println("\t\t 9 退出系统");
//                System.out.print("请输入你的选择: ");
//                key = Utility.readString(1);
//                switch (key) {
//                    case "1":
//                        // 这里写一个方法，来获取在线用户列表
//                        userClientService.onlineFriendList();
//                        break;
//                    case "2":
//                        System.out.println("请输入想对大家说的话: ");
//                        String s = Utility.readString(100);
//                        messageClientService.sendMessageToAll(s, userId);
//                        break;
//                    case "3":
//                        System.out.print("请输入想聊天的用户号(在线): ");
//                        String getterId = Utility.readString(50);
//                        System.out.print("请输入想说的话: ");
//                        String content = Utility.readString(100);
//                        // 编写一个方法，将消息发送给服务器端
//                        messageClientService.sendMessageToOne(content, userId, getterId);
//                        break;
//                    case "4":
//                        System.out.print("请输入你想把文件发送给的用户(在线用户): ");
//                        getterId = Utility.readString(50);
//                        System.out.print("请输入发送文件的路径(形式 d:\\xx.jpg): ");
//                        String src = Utility.readString(100);
//                        System.out.print("请输入把文件发送到对应的路径(形式 d:\\yy.jpg): ");
//                        String dest = Utility.readString(100);
//                        fileClientService.sendFileToOne(src, dest, userId, getterId);
//                        break;
//                    case "5":
//                        scanServers();
//                        innerLoop = false; // 退出当前登录状态
//                        break;
//                    case "9":
//                        // 调用方法，给服务器发送一个退出系统的message
//                        userClientService.logout();
//                        innerLoop = false;
//                        break;
//                }
//            }
//        } else { // 登录服务器失败
//            System.out.println("=========登录失败=========");
//        }
//    }
//}

package com.qq.qqview;

import com.qq.data.UserService;
import com.qq.qqclient.qqservice.FileClientService;
import com.qq.qqclient.qqservice.MessageClientService;
import com.qq.qqclient.qqservice.UserClientService;
import com.qq.qqcommon.MessageType;
import com.qq.utils.Utility;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 客户端的菜单界面
 */
public class View {

    private boolean loop = true; //控制是否显示菜单
    private String key = ""; // 接收用户的键盘输入
    private UserClientService userClientService = new UserClientService();//对象是用于登录服务/注册用户
    private MessageClientService messageClientService = new MessageClientService();//对象用户私聊/群聊.
    private FileClientService fileClientService = new FileClientService();//该对象用户传输文件

    // 添加默认服务器配置
    private String defaultServerIp = "172.20.7.79";
    private int defaultServerPort = 1525;

    public static void main(String[] args) {
        new View().mainMenu();
        System.out.println("客户端退出系统.....");
    }

    //显示主菜单
    private void mainMenu() {

        while (loop) {

            System.out.println("===========欢迎登录网络通信系统===========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 2 注册系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            //根据用户的输入，来处理不同的逻辑
            switch (key) {
                case "1":
                    Login();
                    break;
                case "2":
//                    Registration();
                    break;
                case "9":
                    loop = false;
                    break;
            }

        }

    }

    /**
     * 登录
     */
    private void Login() {
        // 获取服务器配置
        System.out.print("请输入服务器IP(默认 " + defaultServerIp + "): ");
        String serverIp = Utility.readString(20);
        if (serverIp.isEmpty()) {
            serverIp = defaultServerIp;
        }

        int serverPort = defaultServerPort;
        try {
            System.out.print("请输入服务器端口(默认 " + defaultServerPort + "): ");
            String portInput = Utility.readString(5);
            if (!portInput.isEmpty()) {
                serverPort = Integer.parseInt(portInput);
            }
        } catch (NumberFormatException e) {
            System.out.println("端口格式错误，使用默认端口 " + defaultServerPort);
        }

        System.out.print("请输入用户号: ");
        String userId = Utility.readString(50);
        System.out.print("请输入密  码: ");
        String pwd = Utility.readString(50);

        // 验证用户登录
        if (userClientService.checkUser(userId, pwd, serverIp, serverPort)) {
            System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
            secondaryMenu(userId); // 进入二级菜单
        } else {
            System.out.println("=========登录失败=========");
        }
    }

    private void secondaryMenu(String userId) {
        boolean innerLoop = true;
        while (innerLoop) {
            System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
            System.out.println("\t\t 1 显示在线用户列表");
            System.out.println("\t\t 2 群发消息");
            System.out.println("\t\t 3 私聊消息");
            System.out.println("\t\t 4 发送文件");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择: ");
            String key = Utility.readString(1);
            switch (key) {
                case "1":
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
                    messageClientService.sendMessageToOne(content, userId, getterId);
                    break;
                case "4":
                    System.out.print("请输入你想把文件发送给的用户(在线用户): ");
                    getterId = Utility.readString(50);
                    System.out.print("请输入发送文件的路径(形式 d:\\xx.jpg): ");
                    String src = Utility.readString(100);
                    System.out.print("请输入把文件发送到对应的路径(形式 d:\\yy.jpg): ");
                    String dest = Utility.readString(100);
                    fileClientService.sendFileToOne(src, dest, userId, getterId);
                    break;
                case "9":
                    userClientService.logout();
                    innerLoop = false;
                    break;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

}
//
///**
// * 登录
// */
//private void Login() { // 获取服务器配置
//    System.out.print("请输入服务器IP(默认 " + defaultServerIp + "): ");
//    String serverIp = Utility.readString(20);
//    if (serverIp.isEmpty()) {
//        serverIp = defaultServerIp;
//    }
//
//    int serverPort = defaultServerPort;
//    try {
//        System.out.print("请输入服务器端口(默认 " + defaultServerPort + "): ");
//        String portInput = Utility.readString(5);
//        if (!portInput.isEmpty()) {
//            serverPort = Integer.parseInt(portInput);
//        }
//    } catch (NumberFormatException e) {
//        System.out.println("端口格式错误，使用默认端口 " + defaultServerPort);
//    }
//
//    System.out.print("请输入用户号: ");
//    String userId = Utility.readString(50);
//    System.out.print("请输入密  码: ");
//    String pwd = Utility.readString(50);
//
//    // 修改为传递四个参数
//    if (userClientService.checkUser(userId, pwd, serverIp, serverPort)) {
//        System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
//
////    // 正确的TCP Socket连接方式
////    public void login(String userId, String password) {
////        try {
////            // 应使用Socket类，而非WebSocket相关类
////            Socket socket = new Socket(serverIP, serverPort);
////
////            // 发送登录信息
////            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
////            User user = new User(userId, password);
////            oos.writeObject(user);
////
////            // 接收响应
////            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
////            Message message = (Message) ois.readObject();
////
////            if(message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {
////                // 登录成功处理
////            } else {
////                // 登录失败处理
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        //进入到二级菜单
//        boolean innerLoop = true; // 使用新的变量控制二级菜单循环
//        while (innerLoop) {
//            System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
//            System.out.println("\t\t 1 显示在线用户列表");
//            System.out.println("\t\t 2 群发消息");
//            System.out.println("\t\t 3 私聊消息");
//            System.out.println("\t\t 4 发送文件");
//            System.out.println("\t\t 9 退出系统");
//            System.out.print("请输入你的选择: ");
//            key = Utility.readString(1);
//            switch (key) {
//                case "1":
//                    //这里写一个方法，来获取在线用户列表
//                    userClientService.onlineFriendList();
//                    break;
//                case "2":
//                    System.out.println("请输入想对大家说的话: ");
//                    String s = Utility.readString(100);
//                    messageClientService.sendMessageToAll(s, userId);
//                    break;
//                case "3":
//                    System.out.print("请输入想聊天的用户号(在线): ");
//                    String getterId = Utility.readString(50);
//                    System.out.print("请输入想说的话: ");
//                    String content = Utility.readString(100);
//                    //编写一个方法，将消息发送给服务器端
//                    messageClientService.sendMessageToOne(content, userId, getterId);
//                    break;
//                case "4":
//                    System.out.print("请输入你想把文件发送给的用户(在线用户): ");
//                    getterId = Utility.readString(50);
//                    System.out.print("请输入发送文件的路径(形式 d:\\xx.jpg): ");
//                    String src = Utility.readString(100);
//                    System.out.print("请输入把文件发送到对应的路径(形式 d:\\yy.jpg): ");
//                    String dest = Utility.readString(100);
//                    fileClientService.sendFileToOne(src, dest, userId, getterId);
//                    break;
//                case "9":
//                    //调用方法，给服务器发送一个退出系统的message
//                    userClientService.logout();
//                    innerLoop = false;
//                    break;
//            }
//
//        }
//    } else { //登录服务器失败
//        System.out.println("=========登录失败=========");
//    }
//}
//    /**
//     * 用户注册
//     */
//    private void Registration() {
//        System.out.println("\n=========== 用户注册 ===========");
//
//        // 获取用户名
//        System.out.print("请输入用户名: ");
//        String userId = Utility.readString(50);
//
//        // 获取密码并验证
//        String pwd;
//        String pwdConfirm;
//        do {
//            System.out.print("请输入密码: ");
//            pwd = Utility.readString(50);
//            System.out.print("请再次输入密码: ");
//            pwdConfirm = Utility.readString(50);
//
//            if (!pwd.equals(pwdConfirm)) {
//                System.out.println("两次输入的密码不一致，请重新输入！");
//            }
//        } while (!pwd.equals(pwdConfirm));
//
//        // 调用注册服务
//        boolean registrationSuccess = userClientService.registerUser(userId, pwd);
//
//        // 处理注册结果
//        if (registrationSuccess) {
//            System.out.println("\n=========== 注册成功 ===========");
//            System.out.println("用户 " + userId + " 已成功注册");
//            System.out.println("请使用新账号登录系统");
//        } else {
//            System.out.println("\n=========== 注册失败 ===========");
//            System.out.println("可能原因：");
//            System.out.println("1. 用户名已被使用");
//            System.out.println("2. 服务器连接问题");
//            System.out.println("3. 无效的用户名格式");
//        }
//
//        // 添加延迟让用户看清结果
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            // 忽略中断异常
//        }
//    }
//}