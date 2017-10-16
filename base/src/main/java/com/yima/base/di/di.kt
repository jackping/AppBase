package com.yima.base.di

import javax.inject.Qualifier
import javax.inject.Scope

/**
 * ActivityScope
 * Created by yima on 2017/6/13.
 */
@Qualifier
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

/**
 * FragmentScope
 * Created by yima on 2017/6/13.
 */
@Qualifier
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope