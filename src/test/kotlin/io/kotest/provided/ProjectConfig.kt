package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode

class ProjectConfig : AbstractProjectConfig() {
    override val specExecutionMode = SpecExecutionMode.Concurrent
    override val testExecutionMode = TestExecutionMode.Concurrent
}
