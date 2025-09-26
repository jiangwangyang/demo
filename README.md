# DEMO

Spring Web 功能示例

## 异步执行接口

返回一个异步执行方法，Spring会在异步mvc线程中执行该方法，执行完成后将结果返回给客户端。

需要配置mvcAsyncExecutor线程池，否则Spring会自动使用SimpleAsyncTaskExecutor线程池。

可以对异步接口设置全局超时时间

### Callable

Controller中返回Callable，会进入mvcAsyncExecutor线程池执行，执行完成后会返回Callable的结果。

如果Callable执行过程中抛出异常，会被全局异常处理器捕获。

如果Callable执行超时，会抛出AsyncRequestTimeoutException异常，同样会被全局异常处理器捕获。

如果Callable执行超时，执行任务的线程会被interrupt，可以在其中捕获InterruptedException异常并做出相应处理。

**超时异常被捕获后，不应在超时异常处理方法中返回超时数据给客户端，而是应该给业务线程一个提示，最终返回的数据应始终由业务线程处理。
**

**如果超时异常处理时直接返回数据，会导致一致性问题，因为此时有两个线程在尝试返回数据。**

### WebAsyncTask

同Callable。

不同之处在于WebAsyncTask可以自定义超时时间，Callable只能使用全局超时时间。

不同之处在于WebAsyncTask可以自定义超时回调方法onTimeout，Callable只能全局捕获超时异常。

不同之处在于WebAsyncTask可以自定义异常回调方法onError，Callable只能全局捕获异常。

## 异步响应接口

返回一个异步数据容器，业务线程可以在任意时间点将数据设置到该容器中，Spring会在数据设置完成后将数据返回给客户端，并且数据只能设置一次。

### DeferredResult

相比于Callable和WebAsyncTask，DeferredResult的优势在于可以自己指定线程执行任务，而不是依赖于全局线程池。

并且同WebAsyncTask一样，DeferredResult也可以自定义超时回调方法onTimeout，异常回调方法onError。

因此相比于Callable和WebAsyncTask，更推荐使用DeferredResult。

**由于DeferredResult需要自己指定线程执行任务，因此抛出的异常不受全局异常处理器的影响，需要在业务线程中捕获异常并做出相应处理。
**

**SpringMvc项目仍然不推荐使用异步接口，因为在同步容器中异步接口无法带来性能优势，反而会增加项目的复杂度，同时也会增加项目的维护成本。
**

**DeferredResult只能返回一次数据，一旦数据设置完成，就不能再修改。**

**DeferredResult在超时处理时，同样不应该返回超时数据给客户端，而应该给业务线程一个提示，最终返回的数据应始终由业务线程处理。
**

## 流式执行接口

返回一个流式执行方法，Spring会在异步线程中执行该方法，每执行完一个元素，就将该结果发送给客户端。

需要配置mvcAsyncExecutor线程池，否则Spring会启用默认异步线程池并发出警告，因为默认线程池不适合生产环境。

### Flux

Controller中返回Flux流，会进入mvcAsyncExecutor线程池执行，每执行一个元素，就会将该结果发送给客户端。

如果Flux执行过程中抛出异常，会被全局异常处理器捕获。

如果Flux执行超时，同样会抛出异常被全局异常处理器捕获。

**推荐使用onErrorResume处理异常，而不是全局异常处理器。**

## 流式响应接口

返回一个流式数据容器，Spring负责管理这个容器，等到容器有数据时，将数据返回给客户端，并且可以多次返回数据。

### ResponseBodyEmitter

ResponseBodyEmitter也是一个数据容器，业务线程可以在任意时间点将数据send到该容器中，Spring会在收到数据后将其返回给客户端。

相比于Flux，ResponseBodyEmitter的优势在于可以自己指定线程执行任务，而不是依赖于全局线程池。

同样的，ResponseBodyEmitter也可以由全局异常处理器处理超时，也可以自定义超时回调方法onTimeout和异常回调方法onError。

**同样的，ResponseBodyEmitter在超时处理时，不应该返回超时数据给客户端，而应该给业务线程一个提示，让业务线程做超时处理。**

**不同的是，ResponseBodyEmitter在执行超时回调时，数据流已经被关闭，此时无法再返回数据给客户端，否则会产生异常。**

### SseEmitter

同ResponseBodyEmitter。

不同之处在于SseEmitter发送的数据为固定事件类型ServerSentEvent，而ResponseBodyEmitter可以发送任意格式的数据。

**在实际使用中，相比ResponseBodyEmitter，更推荐使用SseEmitter，因为它发送的数据为固定事件类型ServerSentEvent，客户端可以很方便地处理这些事件。
**

*
*在实际使用中，相比ResponseBodyEmitter和SseEmitter，更推荐使用Flux，因为Flux则是一个响应式流，由Spring自动管理流的生命周期，不容易出错。而ResponseBodyEmitter和SseEmitter需要手动控制流的生命周期，容易出错。
**

