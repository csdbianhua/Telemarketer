#Telemarketer

Telemarketer 是一个简单的web服务器,同时也提供了一个简单的Web框架。只是因为有个小需求而不想使用重量级的Web服务器而做。

仅支持Java version >= 1.8

first make it work, then make it fast

#使用方法
`mvn -Dmaven.test.skip=true install`
```xml
    <dependency>
        <groupId>com.telemarket</groupId>
        <artifactId>telemarket</artifactId>
        <version>1.0.1-SNAPSHOT</version>
    </dependency>
```

``` java
import com.telemarket.telemarketer.TelemarketerStartup;
import com.telemarket.telemarketer.http.HttpMethod;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.responses.JsonResponse;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.QueryParam;
import com.telemarket.telemarketer.mvc.annotation.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
@Path("/")
public class HelloWorldService {

    @Path(value = "/", method = HttpMethod.GET)
    public JsonResponse helloWorld(@QueryParam("name") String name) {
        Map<String, String> obj = new TreeMap<String, String>();
        obj.put("hello", name);
        return new JsonResponse(Status.SUCCESS_200, obj);
    }

    public static void main(String[] args) {
        // 第二个参数为需要注册为控制器的的包下的随意一个类 可以使用多个
        TelemarketerStartup.run(new String[]{"start"}, HelloWorldService.class);
    }
}


```

#MVC使用方式(目前只实现了C)
使用`@Service`标注控制器
使用`@Path`标注路径

使用`@FormParam`注入form数据，`@QueryParam`注入query数据，`@MultiPart`注入MultiplePart数据
HttpServletRequest 无需标注即可注入，其他参数默认当成javabean使用参数绑定机制

目前必须返回com.telemarket.telemarketer.http.responses.Response

