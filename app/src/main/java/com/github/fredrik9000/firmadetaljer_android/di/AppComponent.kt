package com.github.fredrik9000.firmadetaljer_android.di

import android.app.Application
import com.github.fredrik9000.firmadetaljer_android.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            ApiModule::class,
            DBModule::class,
            ActivityModule::class,
            ViewModelModule::class
        ]
)
interface AppComponent: AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application) : Builder

        fun build() : AppComponent
    }
}