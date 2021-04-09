## 一、内存模型

* 每个CPU都有一套自己的缓存，对于内存中的同一变量，缓存可能不同

* 内存屏障：操作系统为了解决上述问题提供的硬件层机制

* volatile：读写操作前后添加内存屏障，保证可见性，防止指令重排序

  

## 二、内存结构

* 堆
  * 新生代
    * Eden：
    * From：Survivor区，GC后与TO交换
    * To： Survivor区，永远为空
  * 老年代
  * 新生代晋升老年代
    * 老对象：阈值15
    * 大对象
    * To区满
    * 动态年龄判定：Survivor区相同年龄对象总大小大于Survivor区空间的一半
* 方法区/元空间
* 虚拟机栈
  * 栈帧
    * 局部变量表
    * 操作数栈
    * 动态链接
    * 方法出口
* 本地方法栈
  * 与虚拟机栈作用相似，为native方法服务
* 程序计数器



## 三、对象的内存布局

* 对象头：synchronized的基础
  * 存储内容：根据锁状态，存储内容不同，有对象的hashcode、分代年龄、锁指针等
  * 偏向锁标志位：0无锁，1偏向锁
  * 锁标志位：轻量级锁-00，偏向锁或无锁-01，重量级锁-10，可GC-11
* 实例数据
  * 存放对象程序中各种类型的字段
* 对齐填充
  * HotSpot规定对象的大小必须是8的整数倍，对象头刚好是整数倍，如果实例数据不是的话，就需要占位符对齐填充



## 四、对象的访问

* 句柄：引用指向句柄，句柄指向实例数据
* 直接引用：直接指向实例数据



## 五、GC

* GC ROOTS：从全局变量和本地变量表作可达性分析，不可达对象可被回收，分析时需要Stop the World

* Minor GC：发生在新生代

  * Eden内存不足

* Full GC：发生在整个堆

  * 老年代内存不足
  * 方法区内存不足
  * 调用System.gc方法，不一定触发Full GC

* 空间分配担保

  Minor GC前，如果

  * 老年代最大可用连续空间大于新生代对象总大小
  * 老年代最大可用连续空间大于历次晋升到老年代对象的平均大小触发

  则直接Minor GC，否则Full GC

## 六、垃圾收集器

* 新生代：复制算法
  * Serial：单线程，收集的时候必须得停掉其它线程，适用于client模式
  * ParNew：Serial升级版，多线程，支持和CMS使用
  * Parallel Scavenge：多线程，吞吐量优先
* 老年代
  * Serial Old（PS-MarkSweep）：标记/整理算法，单线程，Serial的老年代版本
  * Parallel Old：标记/整理算法，多线程，Parallel Scavenge的老年版本
  * CMS：标记/清除算法，支持并发，最短回收停顿时间
* 其他
  * G1：新型收集器



## 七、调优

* 参数

  * -Xms：初始堆空间内存，默认为物理内存的1/64
  * -Xmx：最大堆空间内存，默认为物理内存的1/4
  * -Xmn：新生代大小 
  * -XX:NewRatio：新生代和老年代比例，默认为2，即：New:Old=1:2
  * -XX:SurvivorRatio：Eden和Survivor比例，默认为8，即：Eden:From:To=8:1:1

  * -XX:+UseSerialGC：client模式默认值，Serial + Serial Old，适合小型应用
  * -XX:+UseParNewGC：ParNew + Serial Old
  * -XX:+UseConcMarkSweepGC：ParNew + CMS（失败转为Serial Old），响应时间优先
  * -XX:+UseParallelGC：server模式默认值，Parallel Scavenge + Serial Old，吞吐量优先
  * -XX:+UseParallelOldGC：Parallel Scavenge + Parallel Old
  * -XX:+PrintGCDetails：打印GC日志

* 命令
  * jps -l：查看jvm进程
  * jstat -gc pid：垃圾回收统计
  * top -Hp pid：查看进程中线程的CPU消耗情况
  * print "%x\n" pid：转换为16进制
  * jstack pid | grep hex：定位到问题线程
  * jmap dump:format=b,file=/tmp/dump.dat pid：生成dump文件
  * jhat -port 8888 /tmp/dump.bat：查看dump文件

* 工具
  * JConsole
  * JVisualVM 打开dump文件