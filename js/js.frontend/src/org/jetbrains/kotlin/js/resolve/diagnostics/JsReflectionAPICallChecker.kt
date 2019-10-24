/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.ReflectionTypes
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.diagnostics.Errors.UNSUPPORTED
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.checkers.AbstractReflectionApiCallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.storage.StorageManager

private val ALLOWED_CLASSES = setOf(
    FqName("kotlin.reflect.KType"),
    FqName("kotlin.reflect.KTypeProjection"),
    FqName("kotlin.reflect.KTypeProjection.Companion"),
    FqName("kotlin.reflect.KVariance")
)

class JsReflectionAPICallChecker(
    reflectionTypes: ReflectionTypes,
    storageManager: StorageManager
) : AbstractReflectionApiCallChecker(reflectionTypes, storageManager) {
    override fun isAllowedKClassMember(name: Name): Boolean =
        super.isAllowedKClassMember(name) || name.asString() == "isInstance"

    override val isWholeReflectionApiAvailable: Boolean
        get() = false

    override fun report(element: PsiElement, context: CallCheckerContext) {
        context.trace.report(UNSUPPORTED.on(element, "This reflection API is not supported yet in JavaScript"))
    }

    override fun isAllowedReflectionApi(descriptor: CallableDescriptor, containingClass: ClassDescriptor): Boolean =
        super.isAllowedReflectionApi(descriptor, containingClass) || containingClass.fqNameSafe in ALLOWED_CLASSES
}
