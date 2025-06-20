package com.qq.tools.security;

import com.qq.qqcommon.Message;
import com.qq.qqcommon.MessageType;
import com.qq.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private final Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            // 1. 读取客户端发送的User对象
            User user = (User) ois.readObject();
            System.out.println("收到登录请求: " + user.getUserId());

            // 2. 验证用户（这里简化处理）
            Message response = new Message();
            if ("1".equals(user.getUserId())){
                response.setMesType(MessageType.MESSAGE_LOGIN_SUCCESS);
                response.setContent("登录成功");
            } else {
                response.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                response.setContent("用户不存在");
            }

            // 3. 发送响应
            oos.writeObject(response);
            System.out.println("已发送响应: " + response.getMesType());

        } catch (Exception e) {
            System.err.println("客户端连接异常: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}