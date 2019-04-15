package com.mahmoud_ashraf.coroutines_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        exAsyncAwait()
    }

    // function takes 1 sec. to be executed ...
    fun printlnDelayed(message : String){
        // complex calculation using thread
        Thread.sleep(1000)
        println(message)
    }

    // same example delay using coroutine ...
    // coroutine -> don't block the current thread like the thread that sleep does ...
    // so delay is not like thread that sleep - it only suspends the particular suspending { the fun }
    // it execute all code without any blocking and when delay is finish it continues the executing of the code
    suspend fun printlnDelayedCoroutine(message : String){
        // complex calculation using coroutine
        delay(1000)
        println(message)
    }

    fun exBlockingWthiThread(){
        // this is synchronous code because it run one after another ...
        println("one")
        printlnDelayed("two")
        println("three")
    }

    fun exBlockingWtihCoroutines(){
        println("one -  thread ${Thread.currentThread().name}")
        /**
         * runBlocking -> BLOCKS the main thread like thread.sleep -_-
         */
        runBlocking {
          printlnDelayedCoroutine("two -  thread ${Thread.currentThread().name}")
      }
        println("three -  thread ${Thread.currentThread().name}")
    }

    // Running in another thread but still blocking the main thread ....
    fun exBlockingWtihDispatcher(){
        // Dispatchers -> switch the runBlocking to different thread
        runBlocking(Dispatchers.Default) {
            println("one - from thread ${Thread.currentThread().name}")
            printlnDelayedCoroutine("two - from thread ${Thread.currentThread().name}")
        }
        // outside the runBlocking to show that is run in the blocking of main thread
        println("three - from thread ${Thread.currentThread().name}")
    }

    fun exLaunchGlobal()=runBlocking{
        println("one - from thread ${Thread.currentThread().name}")

        // it doesn't block the main thread ...
        GlobalScope.launch {
            printlnDelayedCoroutine("two - from thread ${Thread.currentThread().name}")
        }
        println("three - from thread ${Thread.currentThread().name}")

        // to tell him wait to print "two" -- without this line it not print "two"
        // this blocking main thread -_-
        delay(3000)

    }

    fun exLaunchGlobalWaiting()=runBlocking{
        println("one - from thread ${Thread.currentThread().name}")

        // it doesn't block the main thread ...
      val job =  GlobalScope.launch {
            printlnDelayedCoroutine("two - from thread ${Thread.currentThread().name}")
        }
        println("three - from thread ${Thread.currentThread().name}")

        // to tell him wait to print "two" -- without this line it not print "two"
        // this blocking main thread -_-
       // delay(3000)
        /**
         * but using delay is really ugly and isn't a good practise
         * because you don't know the time of executing every fun ! :(
         * the best is 1- define val job ...
         * 2- call job.Join ()
         */
        // this is like delay fun but it waits for the job to finish ...
        job.join()


    }

    fun exLaunchCoroutinesScope()=runBlocking{
        println("one - from thread ${Thread.currentThread().name}")

        // it waiting until exec. the block
        launch(Dispatchers.Default) {
            printlnDelayedCoroutine("two - from thread ${Thread.currentThread().name}")
        }
        println("three - from thread ${Thread.currentThread().name}")


    }

    /**
     * But if you want fetching data from server (async) and wait until come and return value ...
     */
    suspend fun calculateHardThings(startNum : Int) : Int {
        delay(1000)
        return  startNum*10
    }
    fun exAsyncAwait() = runBlocking {
        val startTime = System.currentTimeMillis()

        val deferred1 = async { calculateHardThings(10) }
        val deferred2 = async { calculateHardThings(20) }
        val deferred3 = async { calculateHardThings(30) }

        val sum = deferred1.await() + deferred2.await() + deferred3.await()
        println("async/await result = $sum")

        val endTime = System.currentTimeMillis()
        println("Time taken: ${endTime - startTime}")
    }

    fun exWithContext() = runBlocking {
        val startTime = System.currentTimeMillis()

        val result1 = withContext(Dispatchers.Default) { calculateHardThings(10) }
        val result2 = withContext(Dispatchers.Default) { calculateHardThings(20) }
        val result3 = withContext(Dispatchers.Default) { calculateHardThings(30) }

        val sum = result1 + result2 + result3
        println("async/await result = $sum")

        val endTime = System.currentTimeMillis()
        println("Time taken: ${endTime - startTime}")
    }


}
