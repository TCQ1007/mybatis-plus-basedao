# Mybatis-Plus--BaseDao

1、解放每次新建一个实体类就需要自己手动写一次BaseMapper的接口继承。

2、提供了一个BaseDao,可以直接继承该类，直接进行dao层开发

实例：

```java
@Component
@IMapper(IdConfigDDO.class)
/**
* @IMapper(IdConfigDDO.class)  注解用于在编译期生成指定的实体类对应的BaseMapper接口
* BaseDao是一个简单的base抽象类继承
**/
public class IdConfigDao extends BaseDao<IdConfigDDO> {
}
```

只需要这样就可以通过IdConfigDao 可以同时使用BaseMapper的方法和IService的方法



还待完善的地方

1、如果有想自己写BaseMapper 的话，可以再提供一个判断如果已经有了，那么就不帮coder自动生成的逻辑。