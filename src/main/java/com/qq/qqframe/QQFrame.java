package com.qq.qqframe;

import com.qq.qqframe.QQFrame;
import com.qq.qqserver.service.QQServer;

/**
 * 该类创建QQServer ,启动后台的服务
 */
public class QQFrame {
    public static void main(String[] args)  {
        new QQServer();
    }
}