import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

import java.io.IOException;
import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {
        /**
         * 显示网络设备列表
         * 获取网络接口列表, 返回当前主机的所有网络设备数组
         * 显示所有网络设备的名称和描述信息
         * 不同网络环境下显示的网络设备是不同的
         */
        NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
        int k = 0;
        for (NetworkInterface networkInterface : deviceList) {
            System.out.println("序号 " + k + " " + networkInterface.name + " : " + networkInterface.description);
            k++;
        }
        System.out.println("--------------------------------------------------");

        /**
         * 选择网卡并打开网卡连接
         * 选择网卡序号
         * 选择当前用于上网的那张网卡, 可通过手动断开网络连接进行判断
         * 其它未用于上网的网卡无法捕获数据包
         */
        System.out.println("提示: 抓包需要时间请等待几秒后再输入");
        System.out.println("输入要监听的网卡序号: ");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        System.out.println("开启混杂模式?(y/n)");
        String next = scanner.next();
        while (!next.equals("y") && !next.equals("n")) {
            System.out.println("开启混杂模式?(y/n)");
            next = scanner.next();
        }
        boolean mode = next.equals("y") ? true : false;
        JpcapCaptor jpcapCaptor = null;
        try {
             jpcapCaptor = JpcapCaptor.openDevice(deviceList[index], 1512, mode, 6000);
            /**
             * 参数解释
             * 参数1：要打开连接的设备的实例
             * 参数2：限制每一次收到一个数据包，只提取该数据包中前多少字节；
             * 参数3：设置是否混杂模式。处于混杂模式将接收所有数据包，若之后又调用了包过滤函数 setFilter() 将不起任何作用
             * 参数4：指定捕获数据包超时的时间
             */
            jpcapCaptor.processPacket(5, new Receiver());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("抓取数据包异常");
        }

        /**
         * 多次捕获, 不保证连续
         */
        System.out.println("进行多次捕获, 输入捕获次数(小于等于0则不进行)");
        int times = scanner.nextInt();
        int i = 0;
        while (i < times) {
            /**
             * 捕获数据包
             * getPacket() 捕捉并返回一个数据包
             */
            Packet packet = jpcapCaptor.getPacket();
            cap(packet);
            i++;
        }
    }

    /**
     * 对捕捉到的包进行处理分析
     * @param packet
     */
    public static void cap(Packet packet) {
        if (packet instanceof IPPacket && ((IPPacket) packet).version == 4) {
            IPPacket ip = (IPPacket) packet;
            System.out.println(ip);

            // 数字对应的协议
            String protocol = "";
            switch (new Integer(ip.protocol)) {
                case 1:
                    protocol = "ICMP";
                    break;
                case 2:
                    protocol = "IGMP";
                    break;
                case 6:
                    protocol = "TCP";
                    break;
                case 8:
                    protocol = "EGP";
                    break;
                case 9:
                    protocol = "IGP";
                    break;
                case 17:
                    protocol = "UDP";
                    break;
                case 41:
                    protocol = "IPv6";
                    break;
                case 89:
                    protocol = "OSPF";
                    break;
            }

            // DF MF RF标志位
            String rf = ip.rsv_frag == true ? "1" : "0";
            String df = ip.dont_frag == true ? "1" : "0";
            String mf = ip.more_frag == true ? "1" : "0";
            String frag = rf + df + mf;

            System.out.println("版本号: " + ip.version);
            System.out.println("总长度: " + ip.length);
            System.out.println("标志位: " + frag);
            System.out.println("片偏移: " + ip.offset);
            System.out.println("协议: " + protocol);
            System.out.println("源地址: " + ip.src_ip.getHostAddress());
            System.out.println("目的地址: " + ip.dst_ip.getHostAddress());
        }
    }
    /**
     * 过滤数据包
     * 需要先设置openDevice取消混杂模式, 才能过滤
     */
    public static void filter(JpcapCaptor jpcapCaptor) throws IOException {
        // 只保留IPv4的TCP的数据包
        jpcapCaptor.setFilter("ip and tcp", true);
        jpcapCaptor.processPacket(5, new Receiver());
    }
}

/**
 * 连续捕获某个时间段的数据包(回调原理实现)
 * 重写PacketReceiver接口的receivePacket()方法
 */
class Receiver implements PacketReceiver {

    @Override
    public void receivePacket(Packet packet) {
        Demo.cap(packet);
    }
}
