package br.com.wgc.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação padrão de [CoroutineDispatchers] que encapsula os despachantes reais de [Dispatchers].
 */
@Singleton
class DefaultCoroutineDispatchers
    @Inject
    constructor() : CoroutineDispatchers {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main

        override val io: CoroutineDispatcher
            get() = Dispatchers.IO

        override val default: CoroutineDispatcher
            get() = Dispatchers.Default

        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
