## 一、Queue

* 继承了Collection
* add() 添加，队列满时抛异常
* offer() 添加，队列满时返回null
* remove() 删除，队列空时抛异常
* poll() 删除，队列空时返回null
* element() 获取不删除，队列空时抛异常
* peek()  获取不删除，队列空时返回null
  	

## 二、Deque

* 双向队列，继承了Queue
* 先进先出时等于Queue，先进后出时等于Stack



##  三、BlockingQueue

* put() 添加，队列满时阻塞
*  take() 删除，队列空时阻塞