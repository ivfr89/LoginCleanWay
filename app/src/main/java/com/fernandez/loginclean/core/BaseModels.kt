package com.fernandez.loginclean.core

abstract class ModelEntity
{
    abstract fun toModel(json: String): ModelEntity

}

abstract class Model
{
    abstract fun empty(): Model
}