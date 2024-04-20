import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.03"

project {

    buildType(BuildNTest)
}

object BuildNTest : BuildType({
    name = "Build-n-Test"

    artifactRules = """
        ctest-log.txt
        ctest-log.xml
        ctest-log-lcd.txt
        ctest-log-lcd.xml
        build/Debug/l4-disco-tc-agent-test.map
        build/Debug/l4-disco-tc-agent-test.elf
        test-image.jpg
    """.trimIndent()

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            id = "simpleRunner"
            enabled = false
            scriptContent = "sh agent-run.sh"
        }
        script {
            name = "CMake configure"
            id = "CMake_configure"
            scriptContent = "cmake --preset Debug --fresh -DRPI_GPIO=1"
        }
        script {
            name = "CMake Build"
            id = "CMake_Build"
            scriptContent = "cmake --build --preset Debug"
        }
        script {
            name = "CTest on-chip (no graphics)"
            id = "CTest_on_chip_no_graphics"
            scriptContent = """
                ctest --preset on-chip-test
                exit 0
            """.trimIndent()
        }
        script {
            name = "CTest on-clip (LCD)"
            id = "CTest_on_clip_LCD"
            scriptContent = "ctest --preset on-chip-lcd-test"
        }
        script {
            name = "Capture Image"
            id = "Capture_Image"
            scriptContent = """
                fswebcam -r 640x480 -F 5 test-image.jpg
                exit 0
            """.trimIndent()
        }
    }

    triggers {
        vcs {
        }
    }

    failureConditions {
        nonZeroExitCode = false
    }

    features {
        perfmon {
        }
        xmlReport {
            reportType = XmlReport.XmlReportType.JUNIT
            rules = """
                ctest-log.xml
                ctest-log-lcd.xml
            """.trimIndent()
        }
    }
})
