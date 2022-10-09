
JSR-199 Java 编译器 API 和 JSR-269 注解处理器 API 试验代码

----

编译示例项目：

``` bash
mvn clean install -DskipTests
```

运行 javac API 示例代码：

``` bash
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.JavacMain"
mvn exec:java -pl javac-demo -Dexec.mainClass="Hello1"
mvn exec:java -pl javac-demo -Dexec.mainClass="com.example.Jsr199Main"
mvn exec:java -pl javac-demo -Dexec.mainClass="Hello2"
```

运行注解处理器示例代码：

``` bash
mvn exec:java -pl processor-demo -Dexec.mainClass="com.example.MyLombokMain"
mvn exec:java -pl processor-demo -Dexec.mainClass="com.example.CompilerMain"
```

---

参见博客：<https://nullwy.me/2017/04/javac-api/>
