package com.wj.kotlintest.test

import javax.inject.Inject

/**
 *
 *
 * @author 王杰
 */
class TestA @Inject constructor(){

    override fun toString(): String {
        return "Class TestA " + super.toString()
    }
}