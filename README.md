JSR-199 Java 编译器 API 和 JSR-269 注解处理器 API 试验代码

编译示例代码：

``` bash
mvn clean package
```

运行 javac API 示例代码：

``` bash
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.JavacMain"
mvn exec:java -pl javac-demo -Dexec.mainClass="Hello1"
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.Jsr199Main"
mvn exec:java -pl javac-demo -Dexec.mainClass="Hello2"
```

运行遍历 Java
抽象语法树的注解处理器 [VisitProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/processor-demo/src/main/java/com/example/visit/VisitProcessor.java)
，编译 `src/main/resources/Example.java` 文件：：

``` bash
cd processor-demo
javac -processorpath target/classes -processor com.example.visit.VisitProcessor -proc:only src/main/resources/Example.java
```

或者通用 javac API 运行 `VisitProcessor` 注解处理器：

```
mvn exec:java -pl processor-demo -Dexec.mainClass="com.example.visit.VisitMain"
```

运行 @Builder
注解处理器 [BuilderProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/mylombok/src/main/java/com/example/filer/BuilderProcessor.java)
对应的单元测试：

``` bash
mvn test -pl processor-demo -Dtest="BuilderProcessorTest"
```

运行 @Data、@Slf4j
注解处理器 [MyLombokProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/mylombok/src/main/java/com/example/processor/MyLombokProcessor.java)
对应的单元测试：

``` bash
mvn test -pl processor-demo -Dtest="MyLombokTest"
```

---

参见博客：<https://nullwy.me/2017/04/javac-api/>
