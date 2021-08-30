单例模式是学习设计模式过程中最基本的一个设计模式，基本上一开始学习就会学到单例模式，实际上在java中实现单例模式有很多种写法，不同写法也会导致不同的问题。
那么究竟哪些写法能用，而哪些写法不能用，或者不同实现方法在什么场景下能使用。本文对现有的9种单例模式的实现方式进行分析。

# 1.饿汉式单例模式--采用静态常量的方式
代码如下：

[SingletonDemo1.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo1.java)

此种实现方式的优点在于，写法简单，在类加载的过程种就完成了所需对象的实例化操作，从而避免的线程安全问题。

缺点在于，饿汉式单例模式，无论所需的对象是否被用到，一上来就会先创建这个对象，如果这个对象在整个业务过程中不被用到，那么势必会造成内存的浪费。

# 2.饿汉式单例模式--采用静态代码块的方式
代码如下：

[SingletonDemo2.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo2.java)

采用静态代码块的方式实现的单例模式与静态常量的方式实现的单例模式实际上是等价的，都是在类加载的过程中就实现了目标对象的实例化。从而避免了线程安全问题。

其缺点也与静态常量的饿汉模式一致，可能会造成内存的浪费。

# 3.懒汉式单例模式--基本实现
代码如下：

[SingletonDemo3.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo3.java)

最基本的懒汉式单例模式如上所示。我们只需要在getInstance方法中进行判断，if(INSTANCE == null) ，如果为True,则说明对象未被实例化，现在直接进行实例化即可。
但是这种方式会引入线程安全的问题，在多线程的环境下，如果一个线程进入了if判断，还没有执行完成，而另外一个线程也进入if判断。此时并会导致返回多个实例。  
因此这种方式在生产环境是不可取的。在getInstance方法中，专门添加了sleep时间，通过main方法中多线程执行效果就会非常明显，可以发现这样会导致每次输出的hashcode都不相同。

最终结论:此种实现方式虽然会降低不必要的内存开销，但是会导致线程安全问题，在并发情况下可能每次调用都创建一个新的实例，因此这种方法是不推荐的。

# 4.懒汉式单例模式--在方法上加锁
代码如下：

[SingletonDemo4.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo4.java)

通过在getInstance方法上加synchronized来实现的懒汉式单利模式。经过测试，这种写法能避免线程安全的问题，在mian函数中进行测试，全部的hashcode都相同。
但是这种写法的问题在于，直接将synchronized加锁在getInstance方法上，这样会导致，如果并行的请求getInstance方法，将不得不变成串行化操作。
这样在并发场景中使用将极大的影响系统的性能。因此虽然这种方式能实现单例模式，但是并不推荐在生产环境中来使用。

# 5.懒汉式单例模式--在方法内部加同步代码块
代码如下：

[SingletonDemo5.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo5.java)

考虑到将synchronized加锁在getInstance方法可能带来效率问题，因此，我们可以进一步尝试锁的细化。将synchronized的同步代码块加在if判断内部。
实验结果证明，这种方式不仅不会对效率有帮助，还导致线程的同步问题，每次输出的hashcode也都不一样了。导致创建了多个目标对象。
这是因为，如果有一个线程已经执行完了if判断，之后虽然进入了同步块，但是还没执行完成，Instance还是空的。那么此时再有另外一个线程执行getInstance.
那么if判断会判断其通过，从而执行其内部的同步代码块。这样虽然加锁导致了串行化，但是实例的对象还是会被创建多次。
因此，此种方法不是一个可用的单例模式的实现方式。我们在生产环境中不推荐使用。

# 6.懒汉式单例模式--Double Check
代码如下:

[SingletonDemo6.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo6.java)

考虑到同步代码块会存在线程安全问题，这个问题都是if判断引起的，那么一种解决方法就是在同步代码块中增加double check ,既实现双重判定检查。
经过验证，这种方式能在大多数情况下都能很好的实现单例模式，执行main函数，基本上hashcode都相同。
但是还是会在少数情况下，出现多个实例的问题。我们可以思考一下这个问题，这个问题正是jvm的可见性造成的。
前面我们的判断都是线程还在执行中，没有对INSTANCE进行赋值，后续的线程就要进入if判断了，因此会造成目标对象被初始化多次，
那么我们假设，如果第一个线程已经执行完了对INSTANCE的赋值，加锁结束，此时恰好有一个线程已经进入了第一个if判断，正在等待锁。
拿到锁之后，进入第二个if判断，但是由于可见性问题，此时第二个线程还不能看到线程一的值已经被写入完毕。误以为还是空，因此再次实现一次实例化。

# 7.懒汉式单例模式--Double Check + volatile
代码如下：

[SingletonDemo7.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo7.java)

为了解决DoubleCheck创建的单例模式中的可见性问题，我们在INSTANCE上增加了volatile,通过happens-before 原则，避免指令重排序，保障了INSTANCE的可见性。
这样在生产环境中，如果我们不考虑反序列化方式可以将这个类创造多个实例之外，这种方式是目前我们在上述所有当例模式的最优写法。

不过需要注意的是，如果通过反序列化，或者反射，那么可能就可能绕开DoubleCheck，造成目标对象被实例化多次。

# 8.懒汉式单利模式--静态内部类
代码如下：

[SingletonDemo8.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo8.java)

结合饿汉模式的优点，既然饿汉模式可以完美而又简单的实现单例模式，而且还能保证线程安全。那么可以参考饿汉模式，结合懒汉模式懒加载的优点。
在其内部建立一个静态的内部类，这个类只有调用getInstance的时候才会被加载，而利用classLoader，从而保证只有一个实例会被实例化。
这种实现方式同样是不能防止反序列化的。如果要解决这个问题，可以通过Serializable、transient、readResolve()实现序列化来解决。


# 9.懒汉式单利模式--利用枚举
代码如下：

[SingletonDemo9.java](../../../src/main/java/com/dhb/gts/javacourse/week5/singleton/SingletonDemo9.java)

在《effective java》中还有一种更简单的写法，那就是枚举。也是《effective java》作者最为推崇的方法。
这种方法不仅可以解决线程同步问题，还可以防止反序列化。
枚举类由于没有构造方法（枚举是java中约定的特殊格式，因此不需要构造函数。），因此不能够根据class反序列化之后实例化。因此这种写法是最完美的单例模式。

