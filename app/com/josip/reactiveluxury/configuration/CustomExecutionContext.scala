package com.josip.reactiveluxury.configuration

import play.api.Logger

import scala.concurrent.Future

object CustomExecutionContext {

//  implicit lazy val customForkJoinPoolContext = createExecutionContext()

  implicit lazy val customForkJoinPoolContext = scala.concurrent.ExecutionContext.Implicits.global

  implicit class ErrorMessageFuture[A](val future: Future[A]) extends AnyVal {
    def handleError(error: String = "Exception occurred."): Future[A] = future.recoverWith {
      case t: Throwable =>
        Logger.logger.error(error, t)
        Future.failed(new RuntimeException(error, t))
    }
  }

//  protected def createExecutionContext(): ExecutionContextExecutorService = {
//    class NamedFjpThread(fjp: ForkJoinPool) extends ForkJoinWorkerThread(fjp)
//
//    /**
//      * A named thread factory for the scala fjp as distinct from the Java one.
//      */
//    case class NamedFjpThreadFactory(name: String) extends ForkJoinWorkerThreadFactory {
//      val threadNo = new AtomicInteger()
//      val backingThreadFactory = Executors.defaultThreadFactory()
//
//      def newThread(fjp: ForkJoinPool) = {
//        val thread = new NamedFjpThread(fjp)
//        thread.setName(name + "-" + threadNo.incrementAndGet())
//        thread
//      }
//    }
//
//    def loggerReporter(throwable: Throwable): Unit = {
//      Logger.logger.error("Exception occurred.", throwable)
//    }
//
//    val uncaughtExceptionHandler: Thread.UncaughtExceptionHandler = new Thread.UncaughtExceptionHandler {
//      def uncaughtException(thread: Thread, cause: Throwable): Unit = loggerReporter(cause)
//    }
//
//    val numberOfThreads = Runtime.getRuntime.availableProcessors
//    val service = new ForkJoinPool(
//      numberOfThreads,
//      NamedFjpThreadFactory("custom-execution-context"),
//      uncaughtExceptionHandler,
//      true
//    )
//
//    ExecutionContext.fromExecutorService(service)
//  }

}
