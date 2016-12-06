#Telemarketer

Telemarketer 是一个简单的web服务器,同时也提供了一个简单的Web框架。只是因为有个小需求而不想使用重量级的Web服务器而做。

仅支持Java version >= 1.8

first make it work, then make it fast

#MVC使用方式(目前只实现了C)
使用`@Service`标注控制器
使用`@Path`标注路径

使用`@FormParam`注入form数据，`@QueryParam`注入query数据，`@MultiPart`注入MultiplePart数据
HttpServletRequest 无需标注即可注入，其他参数默认当成javabean使用参数绑定机制

目前必须返回com.telemarket.telemarketer.http.responses.Response
