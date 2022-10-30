JSR-199 Java 编译器 API 和 JSR-269 注解处理器 API 试验代码

编译示例代码：

``` bash
mvn clean package
```

执行 javac API 示例代码：

``` bash
# 使用内部编译器 API 编译 Greeting1.java 文件 
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.JavacMain"
java -cp javac-demo/target/classes Greeting1
# 使用 JSR-199 编译器 API 编译 Greeting2.java 文件 
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.Jsr199Main" -Dexec.args="javac-demo/src/main/resources/Greeting2.java"
java -cp javac-demo/target/classes Greeting2
```

执行扫描 Java
抽象语法树的注解处理器 [VisitorProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/processor-demo/src/main/java/com/example/visitor/VisitorProcessor.java)
，编译 `src/main/resources/VisitorExample.java` 文件：：

``` bash
cd processor-demo
javac -processorpath target/classes -processor com.example.visitor.VisitorProcessor -proc:only src/main/resources/VisitorExample.java
```

或者通用 javac API 运行 `VisitProcessor` 注解处理器：

```
mvn exec:java -pl processor-demo -Dexec.mainClass="com.example.visit.VisitMain"
```

执行修改 Java
抽象语法树的注解处理器 [PlusProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/processor-demo/src/main/java/com/example/maker/PlusProcessor.java)
，编译 `src/main/resources/PlusExample.java` 文件并运行 `PlusExample`，单元测试：

``` bash
mvn test -pl processor-demo -Dtest="PlusProcessorTest"
```

或者直接使用 javac 和 java 命令执行：

``` bash
cd processor-demo
javac -cp target/classes -processor com.example.maker.PlusProcessor -d target/classes src/main/resources/PlusExample.java
java -cp target/classes PlusExample 42
```

执行 @Builder
注解处理器 [BuilderProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/mylombok/src/main/java/com/example/filer/BuilderProcessor.java)
对应的单元测试：

``` bash
mvn test -pl processor-demo -Dtest="BuilderProcessorTest"
```

执行 @Data、@Getter、@Setter、@Slf4j 等
注解处理器 [MyLombokProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/mylombok/src/main/java/com/example/processor/MyLombokProcessor.java)
对应的单元测试：

``` bash
mvn test -pl processor-demo -Dtest="MyLombokTest"
```

---

参见博客：<https://nullwy.me/2017/04/javac-api/>
