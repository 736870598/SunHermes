#### 仿Hermes实现跨进程通信的核心原理。

#### 一. 使用说明
 1. 假设A进程为主进程，B进程为其他进程。
 2. 在A进程和B进行中必须要有一个完全相同的接口。该接口主要提供给B进程使用。
 3. A进程中要有一个单例类实现该接口。
 4. 在B进行中该接口类上面要加上注解 @ClassId("实现类的全路径")
 5. A进程中接口的实现类必须是单例的，而且获取单例方法名必须是：getInstance()

#### 二. 使用流程及原理
##### 1. register

   在A进程中执行：

    SunHermes.getDefault().register(UserManager.class);

   这一步就是将class消息进行保存，接下来或调用到TypeCenter中执行：

     public void register(Class<?> clazz){
            registerClass(clazz);
            registerMethod(clazz);
        }

   其中registerClass保存到mAnnotatedClasses中

    private final ConcurrentHashMap<String, Class<?>> mAnnotatedClasses；

    private void registerClass(Class<?> clazz){
            String className = clazz.getName();
            mAnnotatedClasses.putIfAbsent(className, clazz);
    }

   registerMethod是将class中的所有方法都保存到mRawMethods中

     private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mRawMethods;

     private void registerMethod(Class<?> clazz){
             mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
             ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);

             Method[] methods = clazz.getMethods();
             for (Method method : methods) {
                 String key = TypeUtils.getMethodId(method);
                 map.put(key, method);
             }
         }

##### 2. connect
   在B进程中执行：

    SunHermes.getDefault().connect(this, SunHermesService.class);

   这一步后会去调用ServiceConnectionManager中的bind方法，进行binder连接：

    ConcurrentHashMap<Class<? extends SunHermesService>, HermesServiceConnection> mHermesServiceConnections = new ConcurrentHashMap();

    public void bind(Context context, String packageName, Class<? extends SunHermesService> service){
            HermesServiceConnection connection = new HermesServiceConnection(service);
            mHermesServiceConnections.put(service, connection);
            Intent intent;
            if (TextUtils.isEmpty(packageName)){
                intent = new Intent(context, service);
            }else{
                intent = new Intent();
                intent.setClassName(packageName,service.getName());
            }
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

   在连接成功后，connection中会将binder信息保存起来。

        ConcurrentHashMap<Class<? extends SunHermesService>, SunService> mHermesServices = new ConcurrentHashMap<>();

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SunService sunService = SunService.Stub.asInterface(service);
            mHermesServices.put(mClass, sunService);
        }

   这一步后就在连个进程中建立了连接。

##### 3. getInstance
   在B进程中执行：

    IUserManager userManager = SunHermes.getDefault().getInstance(IUserManager.class);

   这一步就是将IUserManager接口进行代理处理，并返回代理。

    public <T> T getInstance(Class<T> clazz){
        return getProxy(SunHermesService.class, clazz);
    }

    private <T> T getProxy(Class<? extends SunHermesService> service, Class clazz){
        ClassLoader classLoader = service.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{clazz},
                new SunHermesInvocationHandler(service, clazz));
    }

   其中的SunHermesInvocationHandler会将每一步请求都封装成Request对象通过aidl传递到A进程处理，
   再将处理结果进行返回。

##### 4. 调用方法

   在B进程中调用userManager进行相关操作。在SunHermesInvocationHandler都会进行封装。

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Response responce= SunHermes.getDefault().sendObjectRequest(hermesService, clazz, method, args);
        if (!TextUtils.isEmpty(responce.getData())){
            ResponseBean responceBean = gson.fromJson(responce.getData(), ResponseBean.class);
            if (responceBean.getData() != null){
                String data = gson.toJson(responceBean.getData());
                Class<?> returnType = method.getReturnType();
                return gson.fromJson(data, returnType);
            }
        }
        return null;
    }

   其中sendObjectRequest将接口中设置的 注解类名，请求的方法名，请求的参数等都封装在requestBean中， 再把requestBean封装在Request中，进行发送给A进程处理。

     public <T> Response sendObjectRequest(Class<? extends SunHermesService> hermesServiceClass, Class<T> clazz, Method method, Object[] parameters) {
            RequestBean requestBean = new RequestBean();

            //设置class名
            ClassId classId = clazz.getAnnotation(ClassId.class);
            if (classId == null){
                requestBean.setClassName(clazz.getName());
                requestBean.setResultClassName(clazz.getName());
            }else{
                requestBean.setClassName(classId.value());
                requestBean.setResultClassName(classId.value());
            }

            //设置方法名
            if (method != null){
                requestBean.setMethodName(TypeUtils.getMethodId(method));
            }

            //设置参数信息，将参数都json化
            RequestParameter[] requestParameters = null;
            if (parameters != null && parameters.length > 0){
                requestParameters = new RequestParameter[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    String parameterClassName = parameter.getClass().getName();
                    String parameterValue = gson.toJson(parameter);

                    RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                    requestParameters[i] = requestParameter;
                }
            }
            if (requestParameters != null){
                requestBean.setRequestParameters(requestParameters);
            }

            //封装为Request对象
            Request request = new Request(gson.toJson(requestBean), TYPE_GET);
            //aidl传递
            return serviceConnectionManager.request(hermesServiceClass, request);
        }

   serviceConnectionManager中的mHermesServices在连接成功后就将binder保存进去了，这时取出进行处理。

    public Response request(Class<? extends SunHermesService> sunHermesServiceClass, Request request){
        SunService sunService = mHermesServices.get(sunHermesServiceClass);
        if (sunService != null){
            try {
                return sunService.send(request);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

   到这里逻辑就回到了A进程中，A进程在SunHermesService中执行请求。

    private SunService.Stub mBinder = new SunService.Stub() {
            @Override
            public Response send(Request request)  {
                ResponseMake responseMake = null;
                switch (request.getType()){
                    case SunHermes.TYPE_GET:
                        //获取单例
                        responseMake = new InstanceResponseMake();
                        break;
                }
                if (responseMake != null){
                    return responseMake.makeResponse(request);
                }
                return null;
            }
        };

   其中makeResponse就是处理Request，也就是真正的执行代码的地方。

    public Response makeResponse(Request request){
        //1. 取出request中的requestBean消息并转换为requestBean。
        RequestBean requestBean = gson.fromJson(request.getData(), RequestBean.class);

        //2. 通过requestBean中设置的目标单例类的名字去加载类。
        resultClass = typeCenter.getClassType(requestBean.getResultClassName());

        //3. 通过requestBean中的设置的方法名获取到要执行的具体方法。
        setMethod(requestBean);

        //4. 组装参数，将参数进行还原组装。
        RequestParameter[] requestParameters = requestBean.getRequestParameters();
        if (requestParameters != null && requestParameters.length > 0){
            mParameters = new Object[requestParameters.length];
            for (int i = 0; i < requestParameters.length; i++) {
                RequestParameter requestParameter = requestParameters[i];
                Class<?> clazz = typeCenter.getClassType(requestParameter.getParameterClassName());
                mParameters[i] = gson.fromJson(requestParameter.getParameterValue(), clazz);
            }
        }else{
            mParameters = new Object[0];
        }

        //5. 执行方法，并得到执行结果
        Object resultObj = invokeMethod();

        //6. 将执行结果封装为Response返回给进行B
        ResponseBean responseBean = new ResponseBean(resultObj);
        return new Response(gson.toJson(responseBean));
    }

   在第 2 步中，由于之前已经将类名和class都保存在mAnnotatedClasses中了，所以这时会先去那里面去找，如果找不到在通过反射区加载class信息。

    public Class<?> getClassType(String name)   {
            if (TextUtils.isEmpty(name)) {
                return null;
            }
            //先去mAnnotatedClasses找类的消息，找不到在通过 Class.forName(name)的方法找。
            Class<?> clazz = mAnnotatedClasses.get(name);
            if (clazz == null) {
                try {
                    clazz = Class.forName(name);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return clazz;
    }

   在第 3 步中，setMethod中的代码，其中通过反射去加载getInstance方法并得到单例。
   这里其实可以不把方法名写成getInstance也行，但是前提是在A进程中要在一开始就将类put到objectCenter。

    @Override
    protected void setMethod(RequestBean requestBean) {
        try {
            instance = objectCenter.get(requestBean.getClassName());
            if (instance == null){
                Method getInstanceMethod = resultClass.getMethod("getInstance", new Class[]{});
                if (getInstanceMethod != null){
                    instance = getInstanceMethod.invoke(null);
                    objectCenter.put(instance);
                }
            }

            mMethod = typeCenter.getMethod(resultClass, requestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   在调用typeCenter.getMethod获取方法时，由于之前也是将类中的方法都保存在了 mRawMethods 中了，这里也是先去那里面找。

    public Method getMethod(Class<?> clazz, RequestBean requestBean) {
        String name = requestBean.getMethodName();
        if (name != null){
            //先去 mRawMethods 中找方法，找不到在去通过反射加载。
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> methods = mRawMethods.get(clazz);
            Method method = methods.get(name);
            if (method != null){
                return method;
            }

            //由于之前保存的方法名是方法名(参数1..2..)，所以这里找到"("的位置，之前的就是方法名。
            int pos = name.indexOf("(");

            //还原参数信息。
            Class[] paramters = null;
            RequestParameter[] requestParameters = requestBean.getRequestParameters();
            if (requestParameters != null && requestParameters.length > 0){
                paramters = new Class[requestParameters.length];
                for (int i = 0; i < requestParameters.length; i++) {
                    paramters[i] = getClassType(requestParameters[i].getParameterClassName());
                }
            }
            method = TypeUtils.getMethod(clazz, name.substring(0, pos), paramters);
            methods.put(name, method);
            return method;
        }
        return null;
    }

   在第 5 步中就是通过反射执行代码：

    mMethod.invoke(instance, mParameters);
