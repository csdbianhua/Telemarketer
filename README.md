#Telemarketer

Telemarketer 是一个简单的web服务器,同时也提供了一个简单的Web框架。只是因为有个小需求而不想使用重量级的Web服务器而做。


##编译

./build.sh output_path


##编译后的结构

	-Telemarket
		+libs
		-edu
			-telemarketer
				+http
				+services
				+util
			 	Controller.class
			 	Server.class
		-images
			...
		-template
			...
		 setting.properties


##启动
cd到编译后的目录下

`./manage.sh start [address:port]`

##编写自己的服务
继承Service接口,并用 `@InService(urlPattern = "...")` 标注。

字符串里写入对应路径的正则。会自动扫描并注册服务。

##编写自己的Response
继承Response