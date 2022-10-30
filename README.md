JSR-199 Java 编译器 API 和 JSR-269 注解处理器 API 试验代码

编译示例代码：

``` bash
mvn clean package
```

运行 javac API 示例代码：

``` bash
# 使用内部编译器 API 编译 Greeting1.java 文件 
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.JavacMain"
java -cp javac-demo/target/classes Greeting1
# 使用 JSR-199 编译器 API 编译 Greeting2.java 文件 
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.Jsr199Main" -Dexec.args="javac-demo/src/main/resources/Greeting2.java"
java -cp javac-demo/target/classes Greeting2
```

# VisitorProcessor

运行扫描 Java
抽象语法树的注解处理器 [VisitorProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/processor-demo/src/main/java/com/example/visitor/VisitorProcessor.java)
，扫描 `src/main/resources/VisitorExample.java` 文件：

``` bash
cd processor-demo
javac -processorpath target/classes -processor com.example.visitor.VisitorProcessor -proc:only src/main/resources/VisitorExample.java
```

# PlusProcessor

运行修改 Java
抽象语法树的注解处理器 [PlusProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/processor-demo/src/main/java/com/example/maker/PlusProcessor.java)
，编译 `src/main/resources/PlusExample.java` 文件，并运行被 PlusProcessor 注解处理器修改过的 `PlusExample`：

``` bash
# 运行单元测试
mvn test -pl processor-demo -Dtest="PlusProcessorTest"
# 或者直接使用 javac 和 java 命令运行
cd processor-demo
# 使用 PlusProcessor 注解处理器编译 PlusExample 类
javac -cp target/classes -processor com.example.maker.PlusProcessor -d target/classes src/main/resources/PlusExample.java
# 运行 PlusProcessor 注解处理器处理过的 PlusExample 类
java -cp target/classes PlusExample 42
```

# GreetingProcessor

运行注解处理器 [GreetingProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/processor-demo/src/main/java/com/example/filer/GreetingProcessor.java)
，自动生成 Greeting 类文件，并运行注解处理器自动生成的 GeneratedGreeting 类：

``` bash
# 运行单元测试
mvn test -pl processor-demo -Dtest="GreetingProcessorTest"
# 或者直接使用 javac 和 java 命令运行
cd processor-demo
# 使用 GreetingProcessor 注解处理器生成 GeneratedGreeting 类
javac -processorpath target/classes -processor com.example.filer.GreetingProcessor -Agreeting.className=GeneratedGreeting -d target/classes src/main/resources/Greeting1.java
# 运行 GreetingProcessor 注解处理器生成的 GeneratedGreeting 类
java -cp target/classes GeneratedGreeting
```

# BuilderProcessor

运行 @Builder
注解处理器 [BuilderProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/mylombok/src/main/java/com/example/filer/BuilderProcessor.java)
对应的单元测试：

``` bash
mvn test -pl processor-demo -Dtest="BuilderProcessorTest"
```

# MyLombokProcessor

运行 @Data、@Getter、@Setter、@Slf4j 等
注解处理器 [MyLombokProcessor](https://github.com/yulewei/annotation-processor-demo/blob/master/mylombok/src/main/java/com/example/processor/MyLombokProcessor.java)
对应的单元测试：

``` bash
mvn test -pl processor-demo -Dtest="MyLombokTest"
```

---

参见博客：<https://nullwy.me/2017/04/javac-api/>
