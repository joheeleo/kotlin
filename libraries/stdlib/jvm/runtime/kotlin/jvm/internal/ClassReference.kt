/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.jvm.internal

import kotlin.reflect.*

public class ClassReference(override val jClass: Class<*>) : KClass<Any>, ClassBasedDeclarationContainer {
    override val simpleName: String?
        get() = getClassSimpleName(jClass)

    override val qualifiedName: String?
        get() = error()

    override val members: Collection<KCallable<*>>
        get() = error()

    override val constructors: Collection<KFunction<Any>>
        get() = error()

    override val nestedClasses: Collection<KClass<*>>
        get() = error()

    override val annotations: List<Annotation>
        get() = error()

    override val objectInstance: Any?
        get() = error()

    @SinceKotlin("1.1")
    override fun isInstance(value: Any?): Boolean = error()

    @SinceKotlin("1.1")
    override val typeParameters: List<KTypeParameter>
        get() = error()

    @SinceKotlin("1.1")
    override val supertypes: List<KType>
        get() = error()

    @SinceKotlin("1.3")
    override val sealedSubclasses: List<KClass<out Any>>
        get() = error()

    @SinceKotlin("1.1")
    override val visibility: KVisibility?
        get() = error()

    @SinceKotlin("1.1")
    override val isFinal: Boolean
        get() = error()

    @SinceKotlin("1.1")
    override val isOpen: Boolean
        get() = error()

    @SinceKotlin("1.1")
    override val isAbstract: Boolean
        get() = error()

    @SinceKotlin("1.1")
    override val isSealed: Boolean
        get() = error()

    @SinceKotlin("1.1")
    override val isData: Boolean
        get() = error()

    @SinceKotlin("1.1")
    override val isInner: Boolean
        get() = error()

    @SinceKotlin("1.1")
    override val isCompanion: Boolean
        get() = error()

    private fun error(): Nothing = throw KotlinReflectionNotSupportedError()

    override fun equals(other: Any?) =
        other is ClassReference && javaObjectType == other.javaObjectType

    override fun hashCode() =
        javaObjectType.hashCode()

    override fun toString() =
        jClass.toString() + Reflection.REFLECTION_NOT_AVAILABLE

    companion object {
        private val primitiveFqNames = HashMap<String, String>().apply {
            put("boolean", "kotlin.Boolean")
            put("char", "kotlin.Char")
            put("byte", "kotlin.Byte")
            put("short", "kotlin.Short")
            put("int", "kotlin.Int")
            put("float", "kotlin.Float")
            put("long", "kotlin.Long")
            put("double", "kotlin.Double")
        }

        private val primitiveWrapperFqNames = HashMap<String, String>().apply {
            put("java.lang.Boolean", "kotlin.Boolean")
            put("java.lang.Character", "kotlin.Char")
            put("java.lang.Byte", "kotlin.Byte")
            put("java.lang.Short", "kotlin.Short")
            put("java.lang.Integer", "kotlin.Int")
            put("java.lang.Float", "kotlin.Float")
            put("java.lang.Long", "kotlin.Long")
            put("java.lang.Double", "kotlin.Double")
        }

        // See JavaToKotlinClassMap.
        private val classFqNames = HashMap<String, String>().apply {
            put("java.lang.Object", "kotlin.Any")
            put("java.lang.String", "kotlin.String")
            put("java.lang.CharSequence", "kotlin.CharSequence")
            put("java.lang.Throwable", "kotlin.Throwable")
            put("java.lang.Cloneable", "kotlin.Cloneable")
            put("java.lang.Number", "kotlin.Number")
            put("java.lang.Comparable", "kotlin.Comparable")
            put("java.lang.Enum", "kotlin.Enum")
            put("java.lang.annotation.Annotation", "kotlin.Annotation")
            put("java.lang.Iterable", "kotlin.collections.Iterable")
            put("java.util.Iterator", "kotlin.collections.Iterator")
            put("java.util.Collection", "kotlin.collections.Collection")
            put("java.util.List", "kotlin.collections.List")
            put("java.util.Set", "kotlin.collections.Set")
            put("java.util.ListIterator", "kotlin.collections.ListIterator")
            put("java.util.Map", "kotlin.collections.Map")
            put("java.util.Map\$Entry", "kotlin.collections.Map.Entry")
            put("kotlin.jvm.internal.StringCompanionObject", "kotlin.String.Companion")
            put("kotlin.jvm.internal.EnumCompanionObject", "kotlin.Enum.Companion")

            putAll(primitiveFqNames)
            putAll(primitiveWrapperFqNames)
            primitiveFqNames.values.associateTo(this) { kotlinName ->
                "kotlin.jvm.internal.${kotlinName.substringAfterLast('.')}CompanionObject" to "$kotlinName.Companion"
            }
        }

        private val simpleNames = classFqNames.mapValues { (_, fqName) -> fqName.substringAfterLast('.') }

        public fun getClassSimpleName(jClass: Class<*>): String? = when {
            jClass.isAnonymousClass -> null
            jClass.isLocalClass -> {
                val name = jClass.simpleName
                jClass.enclosingMethod?.let { method -> name.substringAfter(method.name + "$") }
                    ?: jClass.enclosingConstructor?.let { constructor -> name.substringAfter(constructor.name + "$") }
                    ?: name.substringAfter('$')
            }
            jClass.isArray -> {
                val componentType = jClass.componentType
                when {
                    componentType.isPrimitive -> simpleNames[componentType.name]?.plus("Array")
                    else -> null
                } ?: "Array"
            }
            else -> simpleNames[jClass.name] ?: jClass.simpleName
        }
    }
}
