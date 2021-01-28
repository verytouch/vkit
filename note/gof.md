## 一、原则
 * 开闭原则：扩展新的类来实现需求
     Software entities should be open for extension，but closed for modification

 * 依赖倒置原则：面向接口编程

     High level modules shouldnot depend upon low level modules.Both should depend upon abstractions.Abstractions should not depend upon details. Details should depend upon abstractions

 * 接口隔离原则：接口要精简

     Clients should not be forced to depend on methods they do not use

 * 单一职责原则：类职责要单一

     There should never be more than one reason for a class to change

 * 合成复用原则：优先使用关联而非继承

 * 里氏替换原则：尽量不要重写父类的方法

     Inheritance should ensure that any property proved about supertype objects also holds for subtype objects

 * 迪米特法则：不和陌生人说话

     Talk only to your immediate friends and not to strangers	

     

## 二、GOF23

* 单例模式
  * 一个类只有一个实例，且该类能自行创建这个实例的一种模式
  * 节省内存资源、保证数据内容的一致性
  * 私有构造方法，饿汉，懒汉
* 原型模式
  * 用一个已经创建的实例作为原型，通过复制该原型对象来创建一个和原型相同或相似的新对象
  * 传统的构造函数来创建对象，会比较复杂且耗时耗资源
  * Cloneable，浅克隆，深克隆
* 工厂方法模式
  * 定义一个创建产品对象的工厂接口，将产品对象的实际创建工作推迟到具体子工厂类当中
  * 满足开闭原则
  * 抽象工厂、具体工厂、抽象产品和具体产品（简单工厂只有一个工厂类，不属于GOF）
* 抽象工厂模式
  * 为访问类提供一个创建一组相关或相互依赖对象的接口，且访问类无须指定所要产品的具体类就能得到同族的不同等级的产品的模式结构
  * 综合型工厂、产品族、产品等级
* 建造者模式模式
  * 将一个复杂对象的构造与它的表示分离，使同样的构建过程可以创建不同的表示
  * 建造者模式注重零部件的组装过程，而工厂方法模式更注重零部件的创建过程