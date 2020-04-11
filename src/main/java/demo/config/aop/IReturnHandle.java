package demo.config.aop;

public interface IReturnHandle<T,K> {
    //完成类型转换，从类型T转成类型K，和java8的内置函数apply一样的作用
    K handle(T t);
}
