#Telemarketer

Telemarketer 是一个简单的web服务器,同时也提供了一个简单的Web框架。只是因为有个小需求而不想使用重量级的Web服务器而做。

#MVC使用方式(目前只实现了C)
使用`@Service`标注Controller
使用`@Path`标注路径

目前必须将com.telemarket.telemarketer.http.requests.Request作为第一个参数

并且必须返回com.telemarket.telemarketer.http.responses.Response