import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.projectFeatures.ProjectReportTab
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.projectFeatures.projectReportTab
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

version = "2024.12"

project {

    buildType(BuildNTest)

    features {
        buildReportTab {
            id = "PROJECT_EXT_2"
            title = "Camera Snap"
            startPage = "report.zip!index.html"
        }
        projectReportTab {
            id = "PROJECT_EXT_3"
            title = "Last Camera Snap"
            startPage = "report.zip!index.html"
            buildType = "${BuildNTest.id}"
            sourceBuildRule = ProjectReportTab.SourceBuildRule.LAST_SUCCESSFUL
            sourceBuildBranchFilter = "+:<default>"
        }
    }
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
        report.zip
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
                echo \#\#teamcity[testStarted name=\'LCD.PICTURE\' captureStandardOutput=\'true\']
                rm -f test-image.jpg >/dev/null 2>/dev/null
                fswebcam test-image.jpg --list-controls  --skip 99 --resolution 640x480
                TIMESTAMP=${'$'}(date)
                
                # Generate the HTML content
                cat <<EOF > index.html
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Camera Snap of the build result</title>
                </head>
                <body>
                    <h1>Camera Snap of the build result</h1>
                    <img src="test-image.jpg" alt="Picture" style="width:100%; max-width:600px;">
                    <p>Generated on: ${'$'}TIMESTAMP</p>
                </body>
                </html>
                EOF
                zip report.zip index.html test-image.jpg
                echo \#\#teamcity[testMetadata type=\'image\' value=\'test-image.jpg\']
                echo \#\#teamcity[testFinished name=\'LCD.PICTURE\']
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
        errorMessage = true
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "No tests were found!!!"
            failureMessage = "No tests were found!!!"
            reverse = false
        }
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

    requirements {
        exists("discovery.connected")
    }
})
