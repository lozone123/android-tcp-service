# android-tcp-service

通过发送心跳到服务器，可以保持与TCP服务器连接，并能够实时的接受数据，主要是通过一个线程里不停的循环读取数据，然后每读取一次会就如休眠2秒，如果觉得2秒还是太频繁了，就把休眠时间设置长一点都没有问题，设置休眠是为了不要影响程序占用太高的CPU。

使用通过把文件TcpService文件复制到你自己项目里，并在manifest.xml文件声明
<service android:name=".TcpService"></service>

下面就是绑定了，
在MainActivity里面
