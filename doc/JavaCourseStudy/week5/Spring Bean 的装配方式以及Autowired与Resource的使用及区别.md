
# 1.Spring的自动装配
在Spring的使用中，如果要将一个bean实例化，可以通过配置文件，也可以通过在java代码里面的注解来实现，Spring能够根据自动协作这些bean之间的关系，这种自动协作的过程，也称之为自动装配。
自动装配模式有如下四种模式：

| 模式        | 说明                                                                                                                                                |
|:------------|:---------------------------------------------------------------------------------------------------------------------------------------------------|
| no          | no表示关闭自动装配选项，必须通过显示的设置才能确认依赖关系。这也是采用xml配置的默认选项。                                                                     |
| byname      | 基于bean的名称name进行注入，在进行Bean的自动装配时，将属性的name在配置文件中搜索匹配的Bean,如果找到name一致的bean,则进行注入，如果找不打到，则会抛出异常。         |
| byType      | 基于bean的类型进行注入，在bean中自动装配属性的时候，将定义的属性类型与配置文件中定义的bean进行匹配，如果类型一致，就在属性中注入，如果没有找到这样的bean,就抛出异常。 |
| constructor | 通过构造函数自动装配bean,这个操作与ByType是一致的，在自动装配的过程中，将查找构造函数的参数类型，然后对所有构造函数参数执行自动装配。                              |

有三种方式可以实现spring Bean的装配过程：
- xml配置
- @Autowired
- @Resource

# 2.xml配置实现装配
首先定义了三个类，  分别为：  
 
表示用户关系的User类 [User.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v1/User.java)

表示角色的Role类 [Role.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v1/Role.java)

以及表示用户角色关联关系的UserRole类 [UserRole.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v1/UserRole.java)

## 2.1 xml实现基本的装配

[app-v1-1.xml](../../../src/main/resources/app-v1-1.xml)

[XmlTest1.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v1/XmlTest1.java)

这种方式需要在xml中对每一个属性进行配置，首先通过property装配了User和Role，UserRole对象的两个属性role和user通过property进行关联。这样就能实现spring中最基本的一种装配方式。
可以通过XmlTest1类进行测试，确认装配的正确性。
```
UserRole(user=User(id=1, name=张三, age=22), role=Role(roleId=1, roleName=管理员))
```

## 2.2 xml通过byName实现自动装配
xml配置代码：

 [app-v1-2.xml](../../../src/main/resources/app-v1-2.xml)

测试代码：
 [XmlTest2.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v1/XmlTest2.java)
 
 这种装配方式不用在UserRole中指定user和role对应的具体类，只需要增加一个属性：autowire="byName"，就能在自动装配的过程中，将根据UserRole的属性的name查找context中name与之对应的bean进行装配。
 测试结果如下：
 ```
 UserRole(user=User(id=1, name=张三, age=22), role=Role(roleId=1, roleName=管理员))
 ```
##  2.3 xml通过byType实现自动装配
xml配置代码：

[app-v1-3.xml](../../../src/main/resources/app-v1-3.xml)

测试代码：

[XmlTest3.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v1/XmlTest3.java)

 这种装配方式同样不用在UserRole中指定user和role对应的具体类，只需要增加一个属性：autowire="byType"，就能在自动装配的过程中，将根据UserRole成员变量的类型查找context中类型与之对应的bean进行装配。
 需要注意的是，byName方式可以确保bean的唯一性，但是byType方式，无法确保bean的唯一性，如果出现多个bean的类型相同，则会报错。
 测试结果如下：
```
UserRole(user=User(id=1, name=张三, age=22), role=Role(roleId=1, roleName=管理员))
```

##  2.4 xml通过constructor实现自动装配
xml配置代码：

[app-v1-4.xml](../../../src/main/resources/app-v1-4.xml)

测试代码：

[XmlTest4.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v1/XmlTest4.java)

这种装配方式同样不用在UserRole中指定user和role对应的具体类，只需要增加一个属性：autowire="constructor"，就能在自动装配的过程中，将根据UserRole构造函数参数表constructor-arg配置的name查找context中name与之对应的bean进行装配。
测试结果如下：
```
UserRole(user=User(id=1, name=张三, age=22), role=Role(roleId=1, roleName=管理员))
```

# 2.@Autowired实现装配
@Autowired是采用byType实现的自动装配，在装配的过程中，通过类型进行匹配。
同样，定义了Role  
 [Role.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v2/Role.java)
 以及User  
 [User.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v2/User.java)
 
 ## 2.1 注解在属性上
 UserRole代码如下：
 
 [UserRole.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v2/UserRole.java)
 
如上所示，只需要在UserRole的属性上增加@Autpwired,在context中查找与属性类型一致的bean,就可以实现UserRole的自动装配。

## 2.2 注解在构造函数上
 UserRole代码如下：
 
[UserRole1.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v2/UserRole1.java)

注解在构造函数上，等价于xml配置中的constructor配置。通过构造函数的属性值的类型去查找context中的bean。

## 2.3 注解在Set方法上
 UserRole代码如下：
 
 [UserRole2.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v2/UserRole2.java)

AutoWired也可以注解在set方法上来实现自动装配。根据set方法的参数，从context中选择type与之一致的bean实现装配。

## 2.4 测试
测试代码如下:
[AutowiredTest.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v2/AutowiredTest.java)

测试结果：
```
UserRole(user=User(id=1, name=用户, age=22), role=Role(roleId=2, roleName=用户))
UserRole1(user=User(id=1, name=用户, age=22), role=Role(roleId=2, roleName=用户))
UserRole2(user=User(id=1, name=用户, age=22), role=Role(roleId=2, roleName=用户))
```

# 3.@Resource实现装配
同样，通过J2EE的@Resource标签也能实现Bean的装配，但是需要注意的是，这个注解不支持构造函数，只支持属性或者set方法。需要注意的是，@Resource默认是采用byName的方式从contect中查找bean.
角色类：
[Role.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v3/Role.java)
用户类：
[User.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v3/User.java)

## 3.1 注解在属性上
代码如下：
[UserRole.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v3/UserRole.java)

## 3.2 注解在set方法上
代码如下：
[UserRole1.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v3/UserRole1.java)

## 3.3 @Resource测试：
测试类：

[ResourceTest.java](../../../src/main/java/com/dhb/gts/javacourse/week5/springbean/v3/ResourceTest.java)

测试配置：

[app-v3.xml](../../../src/main/resources/app-v3.xml)

测试结果:
```
UserRole(user=User(id=1, name=用户, age=22), role=Role(roleId=2, roleName=用户))
UserRole1(user=User(id=1, name=用户, age=22), role=Role(roleId=2, roleName=用户))
```

# 4.@Autowired与@Resource的比较
二者对比如下：
- @Autowired与@Resource都可以用来装配bean. 都可以写在字段上,或者在setter方法上。 但是@Resource不支持在构造函数上装配。
- @Autowired默认按类型装配（这个注解是属业spring的），默认情况下必须要求依赖对象必须存在，如果要允许null 值，可以设置它的required属性为false，如：@Autowired(required=false) 。如果我们想使用名称装配可以结合@Qualifier注解进行使用。
- @Resource（这个注解属于J2EE的），默认安照名称进行装配，名称可以通过name属性进行指定， 如果没有指定name属性，当注解写在字段上时，默认取字段名进行按照名称查找，如果注解写在setter方法上默认取属性名进行装配。 当找不到与名称匹配的bean时才按照类型进行装配。
