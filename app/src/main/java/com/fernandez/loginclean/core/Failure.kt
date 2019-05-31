package com.fernandez.loginclean.core

sealed class Failure {
    class NetworkConnection: Failure()
    class ServerErrorCode(val code: Int): Failure()
    class ServerException(val throwable: Throwable): Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure: Failure()

    class NullResult: FeatureFailure()


}