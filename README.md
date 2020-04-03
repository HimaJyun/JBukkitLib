# JBukkitLib
Library for Bukkit

# Usage
Add Maven dependency

```xml
<repositories>
    <repository>
        <id>himajyun-repo</id>
        <url>https://himajyun.github.io/mvn-repo/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>jp.jyn</groupId>
        <artifactId>JBukkitLib</artifactId>
        <version>1.3.0</version>
    </dependency>
</dependencies>
```

Create a FatJar with maven-shade-plugin.  
It is recommended to "relocation" to prevent conflicts with different versions included in other plugins.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.2.2</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <artifactSet>
                            <includes>
                                <include>jp.jyn:JBukkitLib</include>
                            </includes>
                        </artifactSet>
                        <relocations>
                            <relocation>
                                <pattern>jp.jyn.jbukkitlib</pattern>
                                <shadedPattern>com.example.jbukkitlib</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

# Version
|Version|Bukkit|Java|
|:------|:-----|:---|
|1.3.0|1.15.2-R0.1-SNAPSHOT|8|
|1.2.0|1.14.4-R0.1-SNAPSHOT|8|
|1.1.0|1.14.1-R0.1-SNAPSHOT|8|
|1.0.0|1.13.2-R0.1-SNAPSHOT|8|
