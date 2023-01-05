# network-analysis
计算机网络课程设计-Java实现

## 参考资料

特别推荐[doubleWoodMoon/Jpcap-Networkpacket-Capture_and_Analysis](https://github.com/doubleWoodMoon/Jpcap-Networkpacket-Capture_and_Analysis)仓库



### 原理

#### 网卡

网卡全称网络适配器，是工作在数据链路层的一个网络组件，通常是一块安装在计算机上的电路板，用于提供专用网络连接。网卡涉及帧的发送与接收、封装与拆封，介质访问控制，数据的编码与解码，数据缓存等。网络通信时，数据必然会经过网卡，因此捕获IP数据包就要先确定捕获的是哪块网卡

#### 待补充...

## 环境搭建

- windows10系统

- Jdk版本1.8
- winpcap4.1.2
- jpcap.jar和jpcap.dll

本项目使用的文件及工具已放到仓库中

## 使用

### 1.安装winpcap

### 2.在Java项目中导入jpcap.jar

### 3.在Jdk安装目录的jre/bin目录下放入jpcap.dll

### 4.启动项目，选择网卡进行监听

## 注意

- 需要选择当前正用于上网的网卡, 否则无法监听到数据
- jpcap抓包存在丢包或超时的问题, 请等待几秒后再输入
- 由于winpcap自身原因, winpcap无法捕获本地回环数据, 可考虑使用**Npcap+wireshark**(这里不再介绍)

## 最后

**欢迎各位GitHub用户对该仓库的不足之处进行补充**
