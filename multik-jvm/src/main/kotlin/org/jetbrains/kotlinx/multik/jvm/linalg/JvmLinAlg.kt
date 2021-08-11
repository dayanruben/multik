/*
 * Copyright 2020-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.kotlinx.multik.jvm.linalg

import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.linalg.LinAlg
import org.jetbrains.kotlinx.multik.api.linalg.LinAlgEx
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray

public object JvmLinAlg : LinAlg {

    override val linAlgEx: LinAlgEx
        get() = JvmLinAlgEx

    override fun <T : Number> pow(mat: MultiArray<T, D2>, n: Int): NDArray<T, D2> {
        if (n == 0) return mk.identity(mat.shape[0], mat.dtype)

        return if (n % 2 == 0) {
            val tmp = pow(mat, n / 2)
            dot(tmp, tmp)
        } else {
            dot(mat, pow(mat, n - 1))
        }
    }

    override fun <T : Number> norm(mat: MultiArray<T, D2>, p: Int): Double {
        require(p > 0) { "Power $p must be positive" }

        return when (mat.dtype) {
            DataType.DoubleDataType -> {
                norm(mat.data.getDoubleArray(), mat.offset, mat.strides, mat.shape[0], mat.shape[1], p, mat.consistent)
            }
            DataType.FloatDataType -> {
                norm(mat.data.getFloatArray(), mat.offset, mat.strides, mat.shape[0], mat.shape[1], p, mat.consistent)
            }
            else -> {
                norm(mat.data, mat.offset, mat.strides, mat.shape[0], mat.shape[1], p, mat.consistent)
            }
        }
    }

//    override fun <T : Number, D : Dim2> dot(a: MultiArray<T, D2>, b: MultiArray<T, D>): NDArray<T, D> {
//        require(a.shape[1] == b.shape[0]) {
//            "Shapes mismatch: shapes " +
//                "${a.shape.joinToString(prefix = "(", postfix = ")")} and " +
//                "${b.shape.joinToString(prefix = "(", postfix = ")")} not aligned: " +
//                "${a.shape[1]} (dim 1) != ${b.shape[0]} (dim 0)"
//        }
//
//        return if (b.dim.d == 2) {
//            dotMatrix(a, b as D2Array<T>) as NDArray<T, D>
//        } else {
//            dotVector(a, b as D1Array<T>) as NDArray<T, D>
//        }
//    }

//    override fun <T : Number> dot(a: MultiArray<T, D1>, b: MultiArray<T, D1>): T {
//        require(a.size == b.size) { "Sizes a and b don't match: a.size(${a.size}) != b.size(${b.size})" }
//        return dotVecToVec(a, b)
//    }
}
